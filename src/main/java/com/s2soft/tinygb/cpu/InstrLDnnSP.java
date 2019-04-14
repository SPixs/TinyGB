package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDnnSP extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == 0x08;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		int immediateValue = (memory.getByte(address+1) & 0x00FF) | memory.getByte(address+2) << 8;
		return "LD (" + Instruction.toHexShort(immediateValue) + "),SP";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		// read the immediate 16 bits value
		int immediate = (additionalBytes[0] & 0x0FF) |  ((additionalBytes[1] & 0x0FF) << 8);
		cpu.getMemory().setByte(immediate, (byte) (cpu.getSp() & 0xFF));
		cpu.getMemory().setByte(immediate+1, (byte) ((cpu.getSp() >> 8) & 0xFF));
		
		return 20;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 3;
	}
}

