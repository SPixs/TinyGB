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
		int value = cpu.getA();
		
		if (cpu.getFlagHalfCarry() || (value & 0x0F) > 0x09) {
			value += 0x06;
		}
		if (cpu.getFlagCarry() || (value & 0xF0) > 0x90) {
			value += 0x60;
		}
		
		cpu.setA((byte)(value &  0x0FF));
		
		cpu.setFlagZero(value == 0);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry(value > 99);
		
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

