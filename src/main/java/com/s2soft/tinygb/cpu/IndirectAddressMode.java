package com.s2soft.tinygb.cpu;

public final class IndirectAddressMode implements IAddressingMode {

	//   ============================ Constants ==============================

	public final static IndirectAddressMode HL = new IndirectAddressMode(Register16Bits.HL);
	public final static IndirectAddressMode BC = new IndirectAddressMode(Register16Bits.BC);
	public final static IndirectAddressMode DE = new IndirectAddressMode(Register16Bits.DE);
	
	//	 =========================== Attributes ==============================

	private Register16Bits m_register;

	//	 =========================== Constructor =============================
	
	public IndirectAddressMode(Register16Bits register) {
		m_register = register;
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public byte readByte(GBCpu cpu, byte[] additionalBytes) {
		return cpu.getMemory().getByte(0x0FFFF & m_register.getValue(cpu));
	}

	public void setByte(GBCpu cpu, byte value,  byte[] additionalBytes) {
		cpu.getMemory().setByte(0x0FFFF & m_register.getValue(cpu), value);
	}

	public String asText(byte[] additionnalBytes) {
		return "("+m_register.name()+")";
	}
}

