package com.s2soft.tinygb.apu;

public final class Voice1 extends PulseVoice {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private FrequencySweep m_frequencySweep;
	
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
		m_frequencySweep.setSweepShift(sweepShift);
	}

	public void setSweepIncrease(boolean increase) {
		m_frequencySweep.setSweepIncrease(increase);
	}

	public void setSweepTime(byte sweepTime) {
		m_frequencySweep.setSweepTime(sweepTime);
	}

	public byte getSweepTime() {
		return m_frequencySweep.getSweepTime();
	}

	public int getSweepShift() {
		return m_frequencySweep.getSweepShift();
	}

	public boolean isSweepIncrease() {
		return m_frequencySweep.isSweepIncrease();
	}
	
	//	 ========================= Treatment methods =========================

	@Override
	public void trigger() {
		super.trigger();
//		m_frequencySweep.init();
	}
}

