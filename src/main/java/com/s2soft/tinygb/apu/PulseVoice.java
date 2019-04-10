package com.s2soft.tinygb.apu;

import com.s2soft.utils.BitUtils;

public abstract class PulseVoice extends Voice {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private int m_rawDuty;
	private float m_duty; // in pourcentage
	
	private int m_rawFrequency;
	
	private int m_counter; // Generate oscillator clocks by dividing machine clock
	
	private int m_dutyPosition = 0; // 0 to 8 to navigate in 
	
	private final byte[] m_dutyCycles = new byte[] {
			(byte)0b00000001,
			(byte)0b10000001,
			(byte)0b10000111,
			(byte)0b01111110
	};

	private VolumeEnveloppe m_envelope;

	//	 =========================== Constructor =============================
	
	public PulseVoice() {
		m_envelope = new VolumeEnveloppe();
		getFrameSequencer().setVolumeEnvelope(m_envelope);
	}

	//	 ========================== Access methods ===========================

	public int getRawDuty() {
		return m_rawDuty;
	}

	public float getDuty() {
		return m_duty;
	}

	//	 ========================= Treatment methods =========================
	
	/**
	 * 00: 12.5% ( _-------_-------_------- )
	 * 01: 25%   ( __------__------__------ )
	 * 10: 50%   ( ____----____----____---- ) (normal)
	 * 11: 75%   ( ______--______--______-- )
	 * 
	 * @param i
	 */
	public void setRawDuty(int duty) {
		m_rawDuty = duty;
		switch (duty & 0x03) {
			case 0b00: m_duty = 0.125f; break;
			case 0b01: m_duty = 0.25f; break;
			case 0b10: m_duty = 0.50f; break;
			case 0b11: m_duty = 0.75f; break;
		}
		if (GBAPU.TRACE) {
			System.out.println("Voice 1. Duty = " + m_duty);
		}
	}

	public int getRawFrequency() {
		return m_rawFrequency;
	}

	public void setRawFrequency(int frequency) {
		m_rawFrequency = frequency;
	}

	public void init() {
		float frequency = 4194304.0f / (4 * 8 * (2048 - m_rawFrequency));
		if (GBAPU.TRACE) {
			System.out.println(getName()+". Init. Frequency = " + frequency);
		}
		m_counter = 0;
		m_envelope.init();
	}
	
	@Override
	public boolean isPlaying() {
		return isEnabled();
	}

	@Override
	public void stepImpl() {

		if (m_counter-- == 0) {
			// A square channel's frequency timer period is set to (2048-frequency)*4. 
			// Four duty cycles are available, each waveform taking 8 frequency timer clocks to cycle through
			m_counter = 4 * (2048 - m_rawFrequency) - 1;
			int oscillatorValue = BitUtils.isSet(m_dutyCycles[getRawDuty()], 7-m_dutyPosition) ? 1 : -1;
			m_dutyPosition = (m_dutyPosition + 1) % 8; // progress into duty cycle
			
//			int oldValue = getValue();
			setOutputValue(oscillatorValue * getEnvelopeValue());
//			if (GBAPU.TRACE && getValue() != oldValue) {
//				System.out.println(getName()+". Output value " + getValue());
//			}
		}
	}
	
	private int getEnvelopeValue() { 
		return m_envelope.getVolume();
	}

	public void setEnvelopeIncrease(boolean increase) {
		if (GBAPU.TRACE) {
			System.out.println("Voice 1. Set envelope increase " + increase);
		}
		m_envelope.setIncrease(increase);
	}

	public void setInitialEnvelopeVolume(int volume) {
		if (GBAPU.TRACE) {
			System.out.println("Voice 1. Set envelope volume " + volume);
		}
		m_envelope.setInitialVolume(volume);
	}

	public boolean isEnvelopeIncrease() {
		return m_envelope.isIncrease();
	}

	public int getInitialEnvelopeVolume() {
		return m_envelope.getInitialEnvelopeVolume();
	}

	public int getEnvelopeSweep() {
		return m_envelope.getSweep();
	}

	public void setEnvelopeSweep(int envelopeSweep) {
		if (GBAPU.TRACE) {
			System.out.println("Voice 1. Set envelope sweep " + envelopeSweep);
		}
		m_envelope.setSweep(envelopeSweep);
	}
	
	protected abstract String getName();
}

