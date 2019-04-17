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
	
//	public void trigger() {
////		m_enabled = true;
//		System.out.println(m_voice.getName() + " trigger. Length counter = " + m_counter + "; enabled=" + m_enabled);
//		if (m_counter == 0) {
//			if (!m_enabled) {
//				m_counter = m_maxLength; 
//			}
//		}
//	}

	public void step() {
//		System.out.println(m_voice.getName() + " Length Step, counter = " + m_counter);
		if (m_enabled) {
			int old = m_counter;
			m_counter = (m_counter - 1) & (m_maxLength - 1); 
			if (m_voice.getName().contains("1"))
				System.out.println(old + " >> " +m_counter);
			if (m_counter == 0) {
				if (!m_voice.isLengthEnabled()) {
				}
				else {
					m_voice.setEnabled(false);
				}
			}
		}
	}

	public void setValue(int length) {
		m_counter = m_maxLength - length;
		if (GBAPU.TRACE) {
			float lengthInSeconds = (m_maxLength - length) / 256.0f;
			System.out.println(m_voice.getName()+". Set length = " + length +", " + lengthInSeconds + "s, counter = " + m_counter);
		}
	}

	public void setEnabled(boolean enable, boolean trigger) {

		if (trigger && m_counter == 0 && !m_enabled && !enable) {
			m_counter = m_maxLength; 
			return;
		}
		
		// TODO : implement ->
		// If a channel is triggered when the frame sequencer's next step is one that doesn't clock the length counter 
		// and the length counter is now enabled and length is being set to 64 (256 for wave channel) because it was 
		// previously zero, it is set to 63 instead (255 for wave channel).
		
		
		// Extra length clocking occurs when writing to NRx4 when the frame sequencer's next step 
		// is one that doesn't clock the length counter. 
		// In this case, if the length counter was PREVIOUSLY disabled and now enabled and the length 
		// counter is not zero, it is decremented.
		boolean lengthCounterInFirstHalf = !m_voice.getFrameSequencer().isLengthCounterInFirstHalf();
		if (lengthCounterInFirstHalf &&  !m_enabled && enable && m_counter > 0) {
			m_counter = (m_counter - 1) & (m_maxLength - 1); 
			System.out.println(m_voice.getName() + " Extra length clocking occurs while enabling length, causing counter = " + m_counter);
			if (m_counter == 0 && !trigger) {
				m_voice.setEnabled(false);
				System.out.println(m_voice.getName() + " Extra length clocking led to disabling voice");
			}
		}
		m_enabled = enable;
	}

	public boolean isEnabled() {
		return m_enabled;
	}
}

