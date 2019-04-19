package com.s2soft.tinygb.apu;

import com.s2soft.tinygb.cpu.Instruction;

public final class Voice3 extends Voice {

	//   ============================ Constants ==============================

	private int[] m_volumeShift = new int[] { 4, 0, 1, 2 };
	
	//	 =========================== Attributes ==============================
	
	private int m_counter;
	private int m_outputLevel;

	private byte[] m_samples = new byte[16];
	
	private int m_samplePosition;

	private int m_stepsSinceLastRead = 255;
	private int m_lastReadIndex;

	private byte m_lastSamplePairPlayed;

	private boolean m_justTriggered;

	//	 =========================== Constructor =============================

	public Voice3() {
		m_lengthCounter = new LengthCounter(this, 256);
		getFrameSequencer().setLengthCounter(m_lengthCounter);
	}
	
	//	 ========================== Access methods ===========================

	public void setOutputLevel(int level) {
		m_outputLevel = level;
	}

	public int getOutputLevel() {
		return m_outputLevel;
	}

	public void setWAVData(int index, byte v) {
		if (!isEnabled()) {
			m_samples[index] = v;
			return;
		}
		if (m_stepsSinceLastRead < 2) {
			m_samples[m_lastReadIndex] = v;
		}
	}

	public byte getWAVData(int index) {
		if (!isEnabled()) {
			return m_samples[index];
		}
		// If the wave channel is enabled, accessing any byte from $FF30-$FF3F is equivalent 
		// to accessing the current byte selected by the waveform position.
		// Further, on the DMG accesses will only work in this manner if made within a couple of 
		// clocks of the wave channel accessing wave RAM
		if (m_stepsSinceLastRead < 2) {
			return m_samples[m_lastReadIndex];
		}
		// If made at any other time, reads return $FF and writes have no effect.
		return (byte) 0xFF;
	}
	
//	@Override
//	public boolean isPlaying() {
//		return isEnabled() && isPlayback();
//	}

	//	 ========================= Treatment methods =========================

	@Override
	public final void stepImpl() {
		
		m_stepsSinceLastRead++;
		
		if (isEnabled() && --m_counter == 0) {
			// The frequency timer period is set to (2048-frequency)*2.
			// When the timer generates a clock, the position counter is advanced one sample in the wave table,
			// looping back to the beginning when it goes past the end, then a sample is read into the sample buffer from this NEW position.
			m_counter = 2 * (2048 - getRawFrequency());
			
			m_stepsSinceLastRead = 0;
			m_lastReadIndex = m_samplePosition / 2;
			byte samplePair = m_samples[m_samplePosition / 2];
			int sample = (m_samplePosition % 2 == 0) ? ((samplePair >> 4) & 0x0F) : (samplePair & 0x0F);
			
			m_samplePosition = (m_samplePosition + 1) & 0x1F;

			byte outputSample = (byte) (sample >> m_volumeShift[getOutputLevel()]);

			// When triggering the wave channel, the first sample to play is the previous one still in the high nibble 
			// of the sample buffer, and the next sample is the second nibble from the wave table. 
			// This is because it doesn't load the first byte on trigger like it "should". 
			// The first nibble from the wave table is thus not played until the waveform loops.
			if (m_justTriggered) {
				outputSample = (byte) (((m_lastSamplePairPlayed >> 4) & 0x0F) >> m_volumeShift[getOutputLevel()]);
				m_justTriggered = false;
			}
			
			m_lastSamplePairPlayed = samplePair;

			setOutputValue(outputSample);
		}
	}
	
	public void trigger() {
		// Triggering the wave channel on the DMG while it reads a sample byte will alter the first four bytes of wave RAM
		if (isEnabled() && getDAC().isEnabled() && m_counter == 2) { // No idea why this counter value is required... But required for BLARGG dmg test N°10
			//  If the channel was reading one of the first four bytes, the only first 
			// byte will be rewritten with the byte being read.
			int index = m_samplePosition / 2;
			if (index < 4) {
				m_samples[0] = m_samples[index];
			}
			// If the channel was reading one of the later 12 bytes, the first FOUR bytes of wave RAM will be rewritten 
			// with the four aligned bytes that the read was from (bytes 4-7, 8-11, or 12-15)
			else {
				System.arraycopy(m_samples, index & ~0x03, m_samples, 0, 4);
			}
		}
		
		setEnabled(true);
		m_justTriggered = true; // required for the first sample to play weird behavior
		getFrameSequencer().trigger();
		m_counter = 6; // the sampling replay starts with a small delay (NO DOC ABOUT THIS !!!?)
		m_samplePosition = 0;
	}
	
	@Override
	public void start() {
		m_lastSamplePairPlayed = 0;
		m_justTriggered = false;
		super.start();
	}
	
	@Override
	protected String getName() {
		return "Voice 3";
	}
}

