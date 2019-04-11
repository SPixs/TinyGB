package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrRLCA extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0x07;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "RLCA";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte valueToRotate = cpu.getA();
		byte newValue = (byte) ((valueToRotate << 1) & 0xFF);
		newValue = BitUtils.setBit(newValue, 0, BitUtils.isSet(valueToRotate, 7));

		cpu.setA(newValue);

		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry(BitUtils.isSet(valueToRotate, 7));
		
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

