package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrJPImm extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xC3;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		int immediateValue = BitUtils.toUShort(memory.getByte(address+1), memory.getByte(address+2));
		return "JP " + toHexShort(immediateValue);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		int address = BitUtils.toUShort(additionalBytes[0], additionalBytes[1]);
		cpu.setPC(address);

		return 16;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 3;
	}
}

