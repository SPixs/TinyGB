package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrHALT extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0x76;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "HALT";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		cpu.halt();
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

