package com.s2soft.tinygb.apu;

/**
 * Sweep Time:
 *   000: sweep off - no freq change
 *   001: 7.8 ms  (1/128Hz)
 *   010: 15.6 ms (2/128Hz)
 *   011: 23.4 ms (3/128Hz)
 *   100: 31.3 ms (4/128Hz)
 *   101: 39.1 ms (5/128Hz)
 *   110: 46.9 ms (6/128Hz)
 *   111: 54.7 ms (7/128Hz)
 *   
 *   The change of frequency (NR13,NR14) at each shift is calculated 
 *   by the following formula where X(0) is initial freq & X(t-1) is last freq:
 *   
 *     X(t) = X(t-1) +/- X(t-1)/2^n
 *     
 * @author smametz
 *
 */
public class FrequencySweep {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private Voice1 m_voice;
	
	private int m_shadowFrequency;

	private boolean m_enabled;
	
	private int m_counter = 0;

	private boolean m_overflow;
	
	private byte m_sweepTime;
	private boolean m_increase;
	private byte m_sweepShift;

	private boolean m_hasPerformedOneNegate;

	//	 =========================== Constructor =============================

	public FrequencySweep(Voice1 voice) {
		m_voice = voice;
		m_enabled = false;
	}

	//	 ========================== Access methods ===========================

	public void setSweepShift(byte sweepShift) {
		m_sweepShift = sweepShift;
	}

	public void setSweepIncrease(boolean increase) {
		m_increase = increase;
		
		// Clearing the sweep negate mode bit in NR10 after at least one sweep calculation has 
		// been made using the negate mode since the last trigger causes the channel to be immediately disabled. 
		// This prevents you from having the sweep lower the frequency then raise the frequency without a trigger inbetween.
		if (increase && m_hasPerformedOneNegate) {
			m_voice.setEnabled(false);
		}
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

	public void step() {
		
		if (!m_enabled) return;

		// When sweep timer generates a clock and the sweep's internal enabled flag is set and the sweep period is not zero, 
		// a new frequency is calculated and the overflow check is performed. 
		if (--m_counter == 0) {
			m_counter = m_voice.getSweepTime() == 0 ? 8 : m_voice.getSweepTime();
			if (m_voice.getSweepTime() != 0) {
				// Compute new frequency
				int newFrequency = computeNextFrequency();
				if (newFrequency < 0) {
					newFrequency = m_shadowFrequency;
				}
				if (!m_overflow && m_voice.getSweepShift() != 0) {
					m_shadowFrequency = newFrequency;
				}
				m_voice.setRawFrequency(m_shadowFrequency);
				computeNextFrequency();
			}
		}
	}
	
	/**
	 * Compute the new frequency without applying it to voice.
	 * Overflow flag is updated
	 * @return
	 */
	private int computeNextFrequency() {
		int newFrequency = m_shadowFrequency + ((m_shadowFrequency >> m_voice.getSweepShift()) *
				(m_voice.isSweepIncrease() ? 1 : -1));
		if (!m_increase) m_hasPerformedOneNegate = true;
		if (newFrequency > 2047) {
			m_overflow = true;
			m_enabled = false;
			m_voice.setEnabled(false);
		}
		return newFrequency;
	}

	/**
	 * During a trigger event, several things occur:
	 * 
	 * Square 1's frequency is copied to the shadow register.
	 * The sweep timer is reloaded.
	 * The internal enabled flag is set if either the sweep period or shift are non-zero, cleared otherwise.
	 * If the sweep shift is non-zero, frequency calculation and the overflow check are performed immediately.
	 */
	public void trigger() {
		m_shadowFrequency = m_voice.getRawFrequency();
		// The volume envelope and sweep timers treat a period of 0 as 8.
		m_counter = m_voice.getSweepTime() == 0 ? 8 : m_voice.getSweepTime();
		m_enabled  = m_voice.getSweepTime() != 0 || m_voice.getSweepShift() != 0;
		m_overflow = false;
		m_hasPerformedOneNegate = false;
		// If the sweep shift is non-zero, frequency calculation and the overflow check are performed immediately.
		if (m_voice.getSweepShift() != 0) {
			computeNextFrequency();
		}
	}
}

