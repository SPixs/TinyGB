package com.s2soft.tinygb.apu;

public abstract class Voice implements IVoice {

	//   ============================ Constants ==============================

	public final static boolean TRACE = true;
	
	//	 =========================== Attributes ==============================

	private boolean m_enabled = false;
	
	private FrameSequencer m_frameSequencer;

//	private int m_rawLength;
//	private float m_lengthInSeconds;
	
	private int m_value;
	
//	private boolean m_lengthEnabled;

	private int m_rawFrequency;
	
	protected LengthCounter m_lengthCounter;

	private DAC m_dac;
	
	//	 =========================== Constructor =============================

	public Voice() {
		m_frameSequencer = new FrameSequencer(this);
		
		m_lengthCounter = new LengthCounter(this, 64);
		getFrameSequencer().setLengthCounter(m_lengthCounter);
		
		m_dac = new DAC(this);
	}
	
	//	 ========================== Access methods ===========================

	public DAC getDAC() {
		return m_dac;
	}
	
	protected FrameSequencer getFrameSequencer() {
		return m_frameSequencer;
	}

//	public int getRawLength() {
//		return m_rawLength;
//	}

	/**
	 * Sound Length = (64-t1)*(1/256) seconds
	 * The Length value is used only if Bit 6 in NR14 is set (at $FF14)
	 * 
	 * @param i
	 */
	public void setRawLength(int t1) {
		m_lengthCounter.setValue(t1);
//		m_rawLength = t1;
//		m_lengthInSeconds = (64 - (t1 & 0x00111111)) / 256.0f;
//		m_lengthCounter.reload();
	}
	
	public void setLengthEnabled(boolean state, boolean trigger) {
		m_lengthCounter.setEnabled(state, trigger);
//		m_lengthEnabled = state;
		if (GBAPU.TRACE && TRACE) {
			System.out.println(getName()+". Length enabled = " + state);
		}
	}

	public boolean isLengthEnabled() {
		return m_lengthCounter.isEnabled();//m_lengthEnabled;
	}

	public int getRawFrequency() {
		return m_rawFrequency;
	}

	public void setRawFrequency(int frequency) {
		m_rawFrequency = frequency;
	}

	//	 ========================= Treatment methods =========================

	public boolean isEnabled() {
		return m_enabled;
	}

	public void setEnabled(boolean enabled) {
		if (GBAPU.TRACE && TRACE) {
			System.out.println(getName() + " enabled = " + enabled);
		}
		m_enabled = enabled;
	}

	protected void setOutputValue(int value) {
		m_value = value;
	}

	/**
	 * @return the voice value encoded in 4 bits (i.e oscillator value ranges between 0 and 15)
	 */
	public final int getValue() {
		return m_enabled ? m_value : 0;
	}
	
	public abstract boolean isPlaying();

//	public float getLengthInSeconds() {
//		return m_lengthInSeconds;
//	}

	public final void step() {
		m_frameSequencer.step();
		
		if (!m_enabled) {
			return;
		}
		
		stepImpl();
	}
	
	public abstract void stepImpl();

	protected abstract String getName();

	public abstract void trigger();

	public void start() {
		m_frameSequencer.reset();
	}
}

