package com.s2soft.tinygb;

public class RegisterAddressingMode implements IAddressingMode {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private Register8Bits m_register;

	//	 =========================== Constructor =============================

	public RegisterAddressingMode(Register8Bits register) {
		m_register = register;
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public byte readByte(GBCpu cpu, byte[] additionalBytes) {
		return m_register.getValue(cpu);
	}

	public void setByte(GBCpu cpu, byte value, byte[] additionalBytes) {
		m_register.setValue(cpu, value);
	}

	public String asText(byte[] additionnalBytes) {
		return m_register.name();
	}
}

