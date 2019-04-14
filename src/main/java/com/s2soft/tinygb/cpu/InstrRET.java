package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrRET extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xC9;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "RET";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		int pulledValue = cpu.pullValue();
		cpu.setPC(pulledValue);

		return 16;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

