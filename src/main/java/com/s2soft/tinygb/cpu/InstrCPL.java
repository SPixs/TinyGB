package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrCPL extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0x2F;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "CPL";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		int value = cpu.getA();
		cpu.setA((byte)(~value &  0x0FF));
		
		cpu.setFlagSubtract(true);
		cpu.setFlagHalfCarry(true);
		
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

