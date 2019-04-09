package com.s2soft.tinygb.apu;

public class Switch implements ISoundProvider {

	//   ============================ Constants ==============================

	private static final boolean TRACE = true;

	//	 =========================== Attributes ==============================
	
	private String m_name;

	private boolean m_enabled = false;
	private ISoundProvider m_input;

	//	 =========================== Constructor =============================

	public Switch(String name) {
		m_name = name;
	}

	//	 ========================== Access methods ===========================

	public ISoundProvider getInput() {
		return m_input;
	}

	public void setInput(ISoundProvider input) {
		m_input = input;
	}

	public boolean isEnabled() {
		return m_enabled;
	}

	public void setEnabled(boolean enabled) {
		m_enabled = enabled;
//		System.out.println((enabled ? "Enabling" : "Disabling") + " channel switch " + m_name);
	}

	//	 ========================= Treatment methods =========================

	private int readInput() {
		return m_input == null ? 0 : m_input.getValue();
	}

	@Override
	public int getValue() {
		return m_enabled ? readInput() : 0;
	}

}

