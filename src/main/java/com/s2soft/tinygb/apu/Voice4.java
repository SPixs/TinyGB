package com.s2soft.tinygb.apu;

import com.s2soft.utils.BitUtils;

/**
 * This voice is a noise channel implemented using a Linear-feedback shift register.
 * 
 * (LFSR) is a shift register whose input bit is a linear function of its previous state.
 * The most commonly used linear function of single bits is exclusive-or (XOR). 
 * Thus, an LFSR is most often a shift register whose input bit is driven by the XOR 
 * of some bits of the overall shift register value.
 * 
 * Gameboy :
 * The linear feedback shift register (LFSR) generates a pseudo-random bit sequence. 
 * It has a 15-bit shift register with feedback. When clocked by the frequency timer, 
 * the low two bits (0 and 1) are XORed, all bits are shifted right by one, 
 * and the result of the XOR is put into the now-empty high bit. If width mode is 1 (NR43), 
 * the XOR result is ALSO put into bit 6 AFTER the shift, resulting in a 7-bit LFSR. 
 * The waveform output is bit 0 of the LFSR, INVERTED.
 * 
 * 
 * @author smametz
 *
 */
public class Voice4 extends Voice implements IVolumeEnveloppeVoice {

	//   ============================ Constants ==============================

	public final static boolean TRACE = true;

	//	 =========================== Attributes ==============================

	private VolumeEnveloppe m_envelope;

	private byte m_dividingRatio;
	private boolean m_counterStepWidth;
	private byte m_shiftClockFrequency;
	
	// The polynomial counter clock
	private int m_counter;
	
	// The polynomial counter itself
	private int m_lfsr;

	private int[] PRESCALER_VALUES = new int[] { 8, 16, 32, 48, 64, 80, 96, 112 };

	private LengthCounter m_lengthCounter;

	//	 =========================== Constructor =============================

	public Voice4() {
		m_envelope = new VolumeEnveloppe();
		getFrameSequencer().setVolumeEnvelope(m_envelope);
		m_counter = 0;
		
		m_lengthCounter = new LengthCounter(this, 64);
		getFrameSequencer().setLengthCounter(m_lengthCounter);
	}
	
	//	 ========================== Access methods ===========================

	@Override
	public boolean isPlaying() {
		return isEnabled();
	}
	
	//	 ========================= Treatment methods =========================

	@Override
	public void stepImpl() {
		if (m_counter-- == 0) {
			m_counter = PRESCALER_VALUES[getDividingRatio()] * (1 << getShiftClockFrequency()) - 1; // reset value of polynomial counter's clock
			byte value = (byte) (clockLFSR() ? 1 : -1);
			setOutputValue(value * getEnvelopeValue()); 
		}
	}
	
	private boolean clockLFSR() {
		// The low two bits (0 and 1) are XORed
		boolean xor = BitUtils.isSet(m_lfsr, 0) ^ BitUtils.isSet(m_lfsr, 1);
		// all bits are shifted right by one
		m_lfsr >>= 1;
		m_lfsr &= 0x7FFF; // Java shift is sign-extending. BTW, it is not mandatory as bit 14 will be overwritten
		// The result of the XOR is put into the now-empty high bit
		m_lfsr = BitUtils.setBit(m_lfsr, 14, xor);
		if (m_counterStepWidth) {
			m_lfsr = BitUtils.setBit(m_lfsr, 6, xor);
		}
		return !BitUtils.isSet(m_lfsr, 0);
	}
	
	public void init() {
		getFrameSequencer().init();
		m_lfsr = 0x7FFF;
		m_counter = 0;
	}
	
	public void setDividingRatio(byte dividingRatio) {
		m_dividingRatio = dividingRatio;
	}

	/**
	 * Sets the numbers of steps in the LFSR register
	 * (false=15 bits, true=7 bits)
	 * 
	 * @param counterStepWidth
	 */
	public void setCounterStepWidth(boolean counterStepWidth) {
		m_counterStepWidth = counterStepWidth;
	}

	public void setShiftClockFrequency(byte shiftClockFrequency) {
		m_shiftClockFrequency = shiftClockFrequency;
	}
	
	public byte getDividingRatio() {
		return m_dividingRatio;
	}

	public boolean isCounterStepWidth() {
		return m_counterStepWidth;
	}

	public byte getShiftClockFrequency() {
		return m_shiftClockFrequency;
	}
	
	private int getEnvelopeValue() { 
		return m_envelope.getVolume();
	}

	public void setEnvelopeIncrease(boolean increase) {
		if (GBAPU.TRACE && TRACE) {
			System.out.println("Voice 4. Set envelope increase " + increase);
		}
		m_envelope.setIncrease(increase);
	}

	public void setInitialEnvelopeVolume(int volume) {
		if (GBAPU.TRACE && TRACE) {
			System.out.println("Voice 4. Set envelope volume " + volume);
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
			System.out.println("Voice 4. Set envelope sweep " + envelopeSweep);
		}
		m_envelope.setSweep(envelopeSweep);
	}

	@Override
	protected String getName() {
		return "Voice 4";
	}
}

