package com.s2soft.tinygb.apu;

public abstract class Voice implements ISoundProvider {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private int m_value;

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	protected void setOutputValue(int value) {
		m_value = value;
	}

	@Override
	public int getValue() {
		return m_value;
	}
	
	public boolean isPlaying() {
		return false;
	}

	public abstract void step();
}

