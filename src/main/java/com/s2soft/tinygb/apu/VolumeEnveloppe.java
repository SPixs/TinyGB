package com.s2soft.tinygb.apu;

public class VolumeEnveloppe {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================
	
	private int m_initialVolume = 0;
	private int m_volume = 0;
	
	private boolean m_scheduledIncrease = false;
	private boolean m_increase = false;
	
	private int m_scheduledSweep = 0;
	private int m_sweep = 0;

	private int m_counter = 0;
	private int m_sweepCounter = 0;

	private boolean m_running;
	
	
	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	public void setIncrease(boolean increase) {
		m_scheduledIncrease = increase;
	}

	public boolean isIncrease() {
		return m_scheduledIncrease;
	}

	public void setInitialVolume(int volume) {
		m_initialVolume = volume;
	}

	public int getInitialEnvelopeVolume() {
		return m_initialVolume;
	}

	public int getSweep() {
		return m_scheduledSweep;
	}

	public void setSweep(int envelopeSweep) {
		m_scheduledSweep = envelopeSweep;
	}
	
	public int getVolume() {
		return m_volume;
	}

	//	 ========================= Treatment methods =========================

	public void step() {
		if (!m_running) {
			return;
		}
		
		if (m_counter-- == 0) {
			if (--m_sweepCounter == 0)  {
				m_sweepCounter = m_sweep;
				if (m_volume == 1 && !m_increase) {
					Thread.yield();
				}
				int newVolume = m_volume + (m_increase ? 1 : -1);
				m_running = newVolume >= 0 && newVolume <= 0x0F;
				if (m_running) { m_volume = newVolume; }
			}
			if (m_sweep < 0) { m_sweep = 0; }
			m_counter = (4194304 / 64) - 1;
		}
	}
	
	public void init() {
		m_volume = m_initialVolume;
		m_increase = m_scheduledIncrease;
		m_sweep = m_scheduledSweep;
		m_counter = (4194304 / 64) - 1;
		m_sweepCounter = m_sweep;
		m_running = true;
	}
}

