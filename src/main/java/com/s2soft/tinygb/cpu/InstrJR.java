package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrJR extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0x18;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		byte offset = memory.getByte(address+1);
		return "JR " + toHexShort(address + offset + getLengthInBytes(opcode));
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		byte offset = additionalBytes[0];
		cpu.setPC(cpu.getPC() + offset);

		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 2;
	}
}

