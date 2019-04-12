package com.s2soft.tinygb.apu;

public abstract class Voice implements ISoundProvider {

	//   ============================ Constants ==============================

	public final static boolean TRACE = true;
	
	//	 =========================== Attributes ==============================

	private boolean m_enabled = false;
	
	private FrameSequencer m_frameSequencer;

	private int m_rawLength;
	private float m_lengthInSeconds;
	
	private int m_value;
	
	private boolean m_lengthEnabled;

	private int m_rawFrequency;
	
	//	 =========================== Constructor =============================

	public Voice() {
		m_frameSequencer = new FrameSequencer(this);
	}
	
	//	 ========================== Access methods ===========================

	protected FrameSequencer getFrameSequencer() {
		return m_frameSequencer;
	}

	public int getRawLength() {
		return m_rawLength;
	}

	/**
	 * Sound Length = (64-t1)*(1/256) seconds
	 * The Length value is used only if Bit 6 in NR14 is set (at $FF14)
	 * 
	 * @param i
	 */
	public void setRawLength(int t1) {
		m_rawLength = t1;
		m_lengthInSeconds = (64 - (t1 & 0x00111111)) / 256.0f;
		if (m_rawLength == 60) {
			Thread.yield();
		}
		if (GBAPU.TRACE && TRACE) {
			System.out.println(getName()+". Set length = " + m_rawLength +", " + m_lengthInSeconds + "s");
		}
	}
	
	public void setLengthEnabled(boolean state) {
		m_lengthEnabled = state;
		if (GBAPU.TRACE && TRACE) {
			System.out.println(getName()+". Length enabled = " + state);
		}
	}

	public boolean isLengthEnabled() {
		return m_lengthEnabled;
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
		m_enabled = enabled;
	}

	protected void setOutputValue(int value) {
		m_value = value;
	}

	@Override
	public int getValue() {
		return m_enabled ? m_value : 0;
	}
	
	public abstract boolean isPlaying();

	public float getLengthInSeconds() {
		return m_lengthInSeconds;
	}

	public void step() {
		if (!m_enabled) {
			return;
		}
		
		m_frameSequencer.step();
		
		stepImpl();
	}
	
	public abstract void stepImpl();

	protected abstract String getName();

	public abstract void init();
	
	
}

