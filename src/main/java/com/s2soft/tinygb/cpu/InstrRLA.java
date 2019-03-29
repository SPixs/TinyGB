package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrRLA extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0x17;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "RLA";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte valueToRotate = cpu.getA();
		byte newValue = (byte) (valueToRotate << 1);
		newValue |= (cpu.getFlagCarry() ? 1 : 0);
		
		cpu.setA(newValue);

		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry(((valueToRotate >> 7) & 0x01) != 0);
		
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

