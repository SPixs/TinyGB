package com.s2soft.tinygb.apu;

/**
 * The frame sequencer generates low frequency clocks for the modulation units. It is clocked by a 512 Hz timer.
 * 
 * Step   Length Ctr  Vol Env     Sweep
 * ---------------------------------------
 * 0      Clock       -           -
 * 1      -           -           -
 * 2      Clock       -           Clock
 * 3      -           -           -
 * 4      Clock       -           -
 * 5      -           -           -
 * 6      Clock       -           Clock
 * 7      -           Clock       -
 * ---------------------------------------
 * Rate   256 Hz      64 Hz       128 Hz
 * 
 * @author smametz
 *
 */
public class FrameSequencer {


	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private int m_counter;
	
	private int m_sequenceStep = 0;

	private Voice m_voice;

	private VolumeEnveloppe m_volumeEnvelope;
	private FrequencySweep m_frequencySweep;
	private LengthCounter m_lengthCounter;

	//	 =========================== Constructor =============================

	public FrameSequencer(Voice voice) {
		m_voice = voice;
	}

	//	 ========================== Access methods ===========================

	public void setVolumeEnvelope(VolumeEnveloppe envelope) {
		m_volumeEnvelope = envelope;
	}
	
	public void setFrequencySweep(FrequencySweep frequencySweep) {
		m_frequencySweep = frequencySweep;
	}
	
	public void setLengthCounter(LengthCounter lengthCounter) {
		m_lengthCounter = lengthCounter;
	}
	
	//	 ========================= Treatment methods =========================

	public void step() {
		
		if (m_counter-- == 0) {
			int sequenceStep = (m_sequenceStep++) % 8;
			if (sequenceStep == 2 || sequenceStep == 6) { stepSweep(); }
			if (sequenceStep % 2 == 0) { stepLength(); }
			if (sequenceStep == 7) { stepVolume(); }
			reset();
		}
	}
	
	private void stepSweep() {
		if (m_frequencySweep != null) {
			m_frequencySweep.step();
		}
	}

	private void stepLength() {
		if (m_lengthCounter != null) {
			m_lengthCounter.step();
		}
	}

	private void stepVolume() {
		if (m_volumeEnvelope != null) {
			m_volumeEnvelope.step();
		}
	}

	public void reset() {
		m_counter = (4194304 / 512) - 1; // Frame sequencer is clocked by a 512 Hz timer.
	}


}

