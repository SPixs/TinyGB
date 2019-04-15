package com.s2soft.tinygb.cpu;

public final class RegisterAddressingMode implements IAddressingMode {

	//   ============================ Constants ==============================

	public final static RegisterAddressingMode A = new RegisterAddressingMode(Register8Bits.A);
	public final static RegisterAddressingMode B = new RegisterAddressingMode(Register8Bits.B);
	public final static RegisterAddressingMode C = new RegisterAddressingMode(Register8Bits.C);
	public final static RegisterAddressingMode D = new RegisterAddressingMode(Register8Bits.D);
	public final static RegisterAddressingMode E = new RegisterAddressingMode(Register8Bits.E);
	public final static RegisterAddressingMode H = new RegisterAddressingMode(Register8Bits.H);
	public final static RegisterAddressingMode L = new RegisterAddressingMode(Register8Bits.L);
	public final static RegisterAddressingMode F = new RegisterAddressingMode(Register8Bits.F);
	
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

