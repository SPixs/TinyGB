package com.s2soft.tinygb.apu;

public class LengthCounter {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private boolean m_enabled;
	private Voice m_voice;
	private int m_counter;

	//	 =========================== Constructor =============================
	
	public LengthCounter(Voice voice) {
		m_voice = voice;
		m_counter = 0;
		m_enabled = false;
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	public void init() {
		m_enabled  = true;
		m_counter = 64 - m_voice.getRawLength();
	}

	public void step() {
		if (m_enabled && m_counter-- == 0) {
			if (!m_voice.isLengthEnabled()) {
				m_counter = 64 - m_voice.getRawLength();
			}
			else {
				m_enabled = false;
				m_voice.setEnabled(false);
			}
		}
	}
}

