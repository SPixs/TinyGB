package com.s2soft.tinygb.apu;

public class LengthCounter {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private boolean m_enabled;
	private Voice m_voice;
	private int m_counter;
	private int m_maxLength;
//	private int m_length;

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
//		reload();
	}

	public void step() {
//		System.out.println(m_voice.getName() + " Length Step, LENGTH="+m_counter);
		if (/*m_enabled && */m_voice.isLengthEnabled()) {
			m_counter = (m_counter - 1) & (m_maxLength - 1); 
			if (m_counter == 0) {
				if (!m_voice.isLengthEnabled()) {
				}
				else {
					m_enabled = false;
					m_voice.setEnabled(false);
				}
				m_counter = m_maxLength;
			}
		}
	}

	public void setValue(int length) {
		m_counter = m_maxLength - length;
		if (GBAPU.TRACE) {
			float lengthInSeconds = (m_maxLength - length) / 256.0f;
			System.out.println(m_voice.getName()+". Set length = " + length +", " + lengthInSeconds + "s");
		}
	}
}

