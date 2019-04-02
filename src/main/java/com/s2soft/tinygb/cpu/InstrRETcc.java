package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.cpu.FlagCondition.FlagField;
import com.s2soft.tinygb.mmu.GBMemory;

public class InstrRETcc extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return getFlagCondition(opcode) != null;
	}
	
	private FlagCondition getFlagCondition(byte opcode) {
		switch (opcode) {
			case (byte)0xC0: return new FlagCondition(FlagField.Z, false);
			case (byte)0xC8: return new FlagCondition(FlagField.Z, true);
			case (byte)0xD0: return new FlagCondition(FlagField.C, false);
			case (byte)0xD8: return new FlagCondition(FlagField.C, true);
		}
		return null;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		FlagCondition flagCondition = getFlagCondition(opcode);
		return "RET " + flagCondition.asText();
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		int pulledValue = cpu.pullValue();
		cpu.setPC(pulledValue);

		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

