package com.s2soft.tinygb.apu;

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

//	private boolean m_playback;

	//	 =========================== Constructor =============================

	public Voice3() {
		m_lengthCounter = new LengthCounter(this, 256);
		getFrameSequencer().setLengthCounter(m_lengthCounter);
	}
	
	//	 ========================== Access methods ===========================

//	public boolean isPlayback() {
//		return m_playback;
//	}

//	public void setPlayback(boolean playback) {
//		m_playback = playback;
//	}

	public void setOutputLevel(int level) {
		m_outputLevel = level;
	}

	public int getOutputLevel() {
		return m_outputLevel;
	}

	public void setWAVData(int index, byte v) {
		if (!isEnabled()) {
			m_samples[index] = v;
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
			m_samplePosition = (m_samplePosition + 1) % 32;

			byte outputSample = (byte) (sample >> m_volumeShift[getOutputLevel()]);
			setOutputValue(outputSample);
		}
	}
	
	public void trigger() {
		getFrameSequencer().trigger();
		m_counter = 6; // the sampling replay starts with a small delay
		m_samplePosition = 0;
	}
	
	@Override
	protected String getName() {
		return "Voice 3";
	}
}

