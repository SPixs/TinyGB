package com.s2soft.tinygb.apu;

public class LengthCounter {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private boolean m_enabled;
	private Voice m_voice;
	private int m_counter;
	private int m_maxLength;

	//	 =========================== Constructor =============================
	
	public LengthCounter(Voice voice, int maxLength) {
		m_voice = voice;
		m_counter = 0;
		m_enabled = false;
		m_maxLength = maxLength;
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	public void init() {
		m_enabled = true;
		m_counter = m_maxLength - m_voice.getRawLength();
	}

	public void step() {
//		System.out.println(m_voice.getName() + " Length Step, LENGTH="+m_counter);
		if (m_enabled && --m_counter == 0) {
			if (!m_voice.isLengthEnabled()) {
				m_counter = m_maxLength - m_voice.getRawLength();
			}
			else {
				m_enabled = false;
				m_voice.setEnabled(false);
			}
		}
	}
}

