package com.s2soft.tinygb.apu;

import com.s2soft.utils.BitUtils;

public abstract class PulseVoice extends Voice implements IVolumeEnveloppeVoice {

	//   ============================ Constants ==============================

	public final static boolean TRACE = false;
	
	//	 =========================== Attributes ==============================

	private int m_rawDuty;
	private float m_duty; // in pourcentage
	
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
		if (GBAPU.TRACE && TRACE) {
			System.out.println("Voice 1. Duty = " + m_duty);
		}
	}

	public void trigger() {
		float frequency = 4194304.0f / (4 * 8 * (2048 - getRawFrequency()));
		if (GBAPU.TRACE && TRACE) {
			System.out.println(getName()+". Init. Frequency = " + frequency);
		}
		m_counter = 0; // the duty cycle starts immediately 
		setEnabled(true);
		getFrameSequencer().trigger();
	}
	
//	@Override
//	public boolean isPlaying() {
//		return isEnabled();// && m_envelope.getVolume() != 0;
//	}

	@Override
	public final void stepImpl() {

		if (isEnabled() && m_counter-- == 0) {
			// A square channel's frequency timer period is set to (2048-frequency)*4. 
			// A full duty cycle period is 8 * 4 * (2048 - m_rawFrequency) machine cycles
			// A duty cycle step is 4 * (2048 - m_rawFrequency) machine cycles (8 steps in a full cycle)
			// Four kind of duty cycles are available, each waveform taking 8 frequency timer clocks to cycle through
			m_counter = 4 * (2048 - getRawFrequency()) - 1;
			int oscillatorValue = BitUtils.isSet(m_dutyCycles[getRawDuty()], 7-m_dutyPosition) ? 1 : 0;
			m_dutyPosition = (m_dutyPosition + 1) % 8; // progress into duty cycle
			
			setOutputValue(oscillatorValue * getEnvelopeValue());
		}
	}
	
	private int getEnvelopeValue() { 
		return m_envelope.getVolume();
	}

	public void setEnvelopeIncrease(boolean increase) {
		if (GBAPU.TRACE && TRACE) {
			System.out.println("Voice 1. Set envelope increase " + increase);
		}
		m_envelope.setIncrease(increase);
	}

	public void setInitialEnvelopeVolume(int volume) {
		if (GBAPU.TRACE && TRACE) {
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
		if (GBAPU.TRACE && TRACE) {
			System.out.println("Voice 1. Set envelope sweep " + envelopeSweep);
		}
		m_envelope.setSweep(envelopeSweep);
	}
}

