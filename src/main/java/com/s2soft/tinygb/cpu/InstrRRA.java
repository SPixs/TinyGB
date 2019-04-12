package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrRRA extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0x1F;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "RRA";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte valueToRotate = cpu.getA();
		byte newValue = (byte) (valueToRotate >> 1);
		newValue = BitUtils.setBit(newValue, 7, cpu.getFlagCarry());
		
		cpu.setA(newValue);

		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry(BitUtils.isSet(valueToRotate, 0));
		
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

