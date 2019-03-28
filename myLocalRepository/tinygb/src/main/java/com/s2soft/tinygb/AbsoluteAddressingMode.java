package com.s2soft.tinygb;

public class AbsoluteAddressingMode implements IAddressingMode {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================
	
	public AbsoluteAddressingMode() {
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public byte readByte(GBCpu cpu, byte[] additionalBytes) {
		int address = (short) ((additionalBytes[0] & 0x0ff) | ((additionalBytes[1] << 8) & 0xFF00));
		return cpu.getMemory().getByte(address);
	}

	public void setByte(GBCpu cpu, byte value,  byte[] additionalBytes) {
		int address = ((additionalBytes[0] & 0x0ff) | ((additionalBytes[1] << 8) & 0xFF00));
		cpu.getMemory().setByte(address, value);
	}

	public String asText(byte[] additionalBytes) {
		int address = ((additionalBytes[0] & 0x0ff) | ((additionalBytes[1] << 8) & 0xFF00));
		return "("+Instruction.toHexShort(address)+")";
	}
}

