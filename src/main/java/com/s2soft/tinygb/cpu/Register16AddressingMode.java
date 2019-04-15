package com.s2soft.tinygb.cpu;

public class Register16AddressingMode {

	//   ============================ Constants ==============================
	
	public final static Register16AddressingMode AF = new Register16AddressingMode(Register16Bits.AF);
	public final static Register16AddressingMode BC = new Register16AddressingMode(Register16Bits.BC);
	public final static Register16AddressingMode DE = new Register16AddressingMode(Register16Bits.DE);
	public final static Register16AddressingMode HL = new Register16AddressingMode(Register16Bits.HL);
	public final static Register16AddressingMode SP = new Register16AddressingMode(Register16Bits.SP);

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

