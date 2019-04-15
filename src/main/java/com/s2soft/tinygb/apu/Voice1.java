package com.s2soft.tinygb.apu;

public final class Voice1 extends PulseVoice {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private FrequencySweep m_frequencySweep;
	
	private byte m_sweepTime;
	private boolean m_increase;
	private byte m_sweepShift;

	//	 =========================== Constructor =============================

	public Voice1() {
		m_frequencySweep = new FrequencySweep(this);
		getFrameSequencer().setFrequencySweep(m_frequencySweep);
	}
	
	//	 ========================== Access methods ===========================

	@Override
	protected String getName() {
		return "Voice 1";
	}

	public void setSweepShift(byte sweepShift) {
		m_sweepShift = sweepShift;
	}

	public void setSweepIncrease(boolean increase) {
		m_increase = increase;
	}

	public void setSweepTime(byte sweepTime) {
		m_sweepTime = sweepTime;
	}

	public byte getSweepTime() {
		return m_sweepTime;
	}

	public int getSweepShift() {
		return m_sweepShift;
	}

	public boolean isSweepIncrease() {
		return m_increase;
	}
	
	//	 ========================= Treatment methods =========================

	@Override
	public void init() {
		super.init();
		m_frequencySweep.init();
	}
	
}

