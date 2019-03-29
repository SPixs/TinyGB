package com.s2soft.tinygb.cpu;

public class Register16AddressingMode {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private Register16Bits m_register;

	//	 =========================== Constructor =============================

	public Register16AddressingMode(Register16Bits register) {
		m_register = register;
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public int readWord(GBCpu cpu) {
		return m_register.getValue(cpu);
	}

	public void setWord(GBCpu cpu, int value) {
		m_register.setValue(cpu, value);
	}

	public String asText(byte[] additionnalBytes) {
		return m_register.name();
	}
}

