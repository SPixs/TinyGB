package com.s2soft.tinygb.apu;

import com.s2soft.utils.BitUtils;

public class Voice1 extends Voice {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private int m_rawLength;
	private float m_lengthInSeconds;
	
	private int m_rawDuty;
	private float m_duty; // in pourcentage
	
	private boolean m_lengthEnabled;
	
	private int m_rawFrequency;
	
	private int m_counter; // Generate oscillator clocks by dividing machine clock
	
	private int m_dutyPosition = 0; // 0 to 8 to navigate in 
	
	private final byte[] m_dutyCycles = new byte[] {
			(byte)0b00000001,
			(byte)0b10000001,
			(byte)0b10000111,
			(byte)0b01111110
	};
	private boolean m_playing;
	
	private VolumeEnveloppe m_envelope = new VolumeEnveloppe();

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	public int getRawLength() {
		return m_rawLength;
	}

	public int getRawDuty() {
		return m_rawDuty;
	}

	public float getLengthInSeconds() {
		return m_lengthInSeconds;
	}

	public float getDuty() {
		return m_duty;
	}

	/**
	 * Sound Length = (64-t1)*(1/256) seconds
	 * The Length value is used only if Bit 6 in NR14 is set (at $FF14)
	 * 
	 * @param i
	 */
	public void setRawLength(int t1) {
		m_rawLength = t1;
		m_lengthInSeconds = (64 - (t1 & 0x001111)) / 256.0f;
		if (GBAPU.TRACE) {
			System.out.println("Voice 1. Length = " + m_lengthInSeconds + "s");
		}
	}

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

	public void setLengthEnabled(boolean state) {
		m_lengthEnabled = state;
		if (GBAPU.TRACE) {
			System.out.println("Voice 1. Length enabled = " + state);
		}
	}

	public boolean isLengthEnabled() {
		return m_lengthEnabled;
	}

	//	 ========================= Treatment methods =========================

	@Override
	public void step() {
		
		m_envelope.step();
		
		if (m_counter-- == 0) {
			// A square channel's frequency timer period is set to (2048-frequency)*4. 
			// Four duty cycles are available, each waveform taking 8 frequency timer clocks to cycle through
			m_counter = 4 * (2048 - m_rawFrequency) - 1;
			if (m_rawFrequency != 0) {
				Thread.yield();
			}
			int oscillatorValue = BitUtils.isSet(m_dutyCycles[getRawDuty()], 7-m_dutyPosition) ? 1 : 0;
			m_dutyPosition = (m_dutyPosition + 1) % 8; // progress into duty cycle
			
			setOutputValue(oscillatorValue * getEnvelopeValue());
			if (GBAPU.TRACE && getValue() != 0) {
				System.out.println("Voice 1. Output value " + getValue());
			}
		}
	}

	private int getEnvelopeValue() { 
		return m_envelope.getVolume();
	}

	public void init() {
		float frequency = 4194304.0f / (4 * 8 * (2048 - m_rawFrequency));
		if (GBAPU.TRACE) {
			System.out.println("Voice 1. Init. Frequency = " + frequency);
		}
		m_counter = 0;
		m_playing = true;
		m_envelope.init();
	}
	
	@Override
	public boolean isPlaying() {
		return m_playing;
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
}

