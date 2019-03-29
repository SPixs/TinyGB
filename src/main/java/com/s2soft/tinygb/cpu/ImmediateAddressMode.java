package com.s2soft.tinygb.cpu;

public class ImmediateAddressMode implements IAddressingMode {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public byte readByte(GBCpu cpu, byte[] additionalBytes) {
		return additionalBytes[0];
	}

	public void setByte(GBCpu cpu, byte value, byte[] additionalBytes) {
		throw new IllegalStateException();
	}
	
	public String asText(byte[] additionnalByte) {
		return Instruction.toHexByte(additionnalByte[0]);
	}
}

