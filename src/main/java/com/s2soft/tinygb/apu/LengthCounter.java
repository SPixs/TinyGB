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
//			int old = m_counter;
			m_counter = (m_counter - 1) & (m_maxLength - 1); 
//			if (m_voice.getName().contains("1"))
//				System.out.println(old + " >> " +m_counter);
			
			// A length counter disables a channel when it decrements to zero.
			if (m_counter == 0) {
				if (m_voice.isLengthEnabled()) {
					m_voice.setEnabled(false);
				}
			}
		}
	}

	/**
	 * The counter can be reloaded at any time.
	 * 
	 * @param length
	 */
	public void setValue(int length) {
		m_counter = m_maxLength - length;
		if (GBAPU.TRACE) {
			float lengthInSeconds = (m_maxLength - length) / 256.0f;
			System.out.println(m_voice.getName()+". Set length = " + length +", " + lengthInSeconds + "s, counter = " + m_counter);
		}
	}
	
	public int getValue() {
		return m_maxLength - m_counter;
	}

	public void setEnabled(boolean enable, boolean trigger) {

		if (GBAPU.TRACE) {
			System.out.println(m_voice.getName() + " Writing to length counter : enabled = " + enable + ", trigger = " + trigger + ", current length counter = " + m_counter);
		}

		boolean nextSequencerStepDoesNotClock = !m_voice.getFrameSequencer().isLengthCounterInFirstHalf();
		
		// Extra length clocking occurs when writing to NRx4 when the frame sequencer's next step
		// is one that doesn't clock the length counter. 
		if (nextSequencerStepDoesNotClock) {
			
			// In this case, if the length counter was PREVIOUSLY disabled and now enabled and the length counter is not zero, it is decremented.
			if (!m_enabled && enable) {
				if (m_counter > 0) {
					m_counter--;
					if (GBAPU.TRACE) {
						System.out.println(m_voice.getName() + " Extra length clocking occurs while enabling length, causing counter = " + m_counter);
					}
					
					// If this decrement makes it zero and trigger is clear, the channel is disabled
					if (m_counter == 0 && !trigger) {
						m_voice.setEnabled(false);
						System.out.println(m_voice.getName() + " Extra length clocking led to disabling voice");
					}
				}		
			}
			
			// If a channel is triggered  and the length counter is now enabled and 
			// length is being set to 64 (256 for wave channel) because it was 
			// previously zero, it is set to 63 instead (255 for wave channel).
			if (trigger && m_counter == 0) {
				m_counter = m_maxLength;
				if (enable) { m_counter--; }
			}
			
			m_enabled = enable;
			return;

		}
		
		m_enabled = enable;
	}

	public boolean isEnabled() {
		return m_enabled;
	}
}

