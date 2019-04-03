package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrJPHL extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xE9;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "JP (HL)";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		int address = Register16Bits.HL.getValue(cpu);
		cpu.setPC(address);

		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

