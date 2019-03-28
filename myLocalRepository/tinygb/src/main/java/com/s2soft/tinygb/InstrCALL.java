package com.s2soft.tinygb;

public class InstrCALL extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xCD;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		int callAddress = ((memory.getByte(address+1) & 0x0ff) | ((memory.getByte(address+2) << 8) & 0xFF00));
		return "CALL " + toHexShort(callAddress);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		int callAddress = ((additionalBytes[0] & 0x0ff) | ((additionalBytes[1] << 8) & 0xFF00));
		cpu.pushShort(cpu.getPc());
		cpu.setPC(callAddress);

		return 12;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 3;
	}
}

