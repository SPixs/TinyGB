package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrDAA extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0x27;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "DAA";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		int value = cpu.getA() & 0xFF;
		
		if (cpu.getFlagSubtract()) {
			if (cpu.getFlagHalfCarry()) {
				value = (value - 0x06) & 0xFF;
			}
			if (cpu.getFlagCarry()) {
				value = (value - 0x60) & 0xFF;
			}
		}
		else {
			if (cpu.getFlagHalfCarry() || (value & 0x0F) > 0x09) {
				value += 0x06;
			}
			if (cpu.getFlagCarry() || value > 0x9F) {
				value += 0x60;
			}
		}
		
		cpu.setFlagHalfCarry(false);
		if (value > 0xFF) {
			cpu.setFlagCarry(true);
		}
		cpu.setFlagZero((value & 0xFF) == 0);
		cpu.setA((byte)(value & 0xFF));
		
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

