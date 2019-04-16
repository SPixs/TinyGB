package com.s2soft.tinygb.apu;

public final class Voice3 extends Voice {

	//   ============================ Constants ==============================

	private int[] m_volumeShift = new int[] { 4, 0, 1, 2 };
	
	//	 =========================== Attributes ==============================
	
	private int m_counter;
	private int m_outputLevel;

	private byte[] m_samples = new byte[16];
	
	private int m_samplePosition;

	private boolean m_playback;

	//	 =========================== Constructor =============================

	public Voice3() {
		m_lengthCounter = new LengthCounter(this, 256);
		getFrameSequencer().setLengthCounter(m_lengthCounter);
	}
	
	//	 ========================== Access methods ===========================

	public boolean isPlayback() {
		return m_playback;
	}

	public void setPlayback(boolean playback) {
		m_playback = playback;
	}

	public void setOutputLevel(int level) {
		m_outputLevel = level;
	}

	public int getOutputLevel() {
		return m_outputLevel;
	}

	public void setWAVData(int index, byte v) {
		m_samples[index] = v;
	}

	public byte getWAVData(int index) {
		return m_samples[index];
	}
	
	@Override
	public boolean isPlaying() {
		return isEnabled() && isPlayback();
	}

	//	 ========================= Treatment methods =========================

	@Override
	public final void stepImpl() {
		if (m_playback && m_counter-- == 0) {
			// The frequency timer period is set to (2048-frequency)*2.
			// When the timer generates a clock, the position counter is advanced one sample in the wave table,
			// looping back to the beginning when it goes past the end, then a sample is read into the sample buffer from this NEW position.
			m_counter = 2 * (2048 - getRawFrequency()) - 1;
			
			byte samplePair = m_samples[m_samplePosition / 2];
			int sample = (m_samplePosition % 2 == 0) ? ((samplePair >> 4) & 0x0F) : (samplePair & 0x0F);
			m_samplePosition = (m_samplePosition + 1) % 32;

			byte outputSample = (byte) (sample >> m_volumeShift[getOutputLevel()]);
			setOutputValue(outputSample);
		}
	}
	
	public void init() {
		getFrameSequencer().init();
		m_counter = 0; // the sampling replay starts immediately
		m_samplePosition = 0;
	}
	
	@Override
	protected String getName() {
		return "Voice 3";
	}
}

