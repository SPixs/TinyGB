package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.cpu.FlagCondition.FlagField;
import com.s2soft.tinygb.mmu.GBMemory;

public class InstrCALLcc extends Instruction {

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
			case (byte)0xC4: return new FlagCondition(FlagField.Z, false);
			case (byte)0xCC: return new FlagCondition(FlagField.Z, true);
			case (byte)0xD4: return new FlagCondition(FlagField.C, false);
			case (byte)0xDC: return new FlagCondition(FlagField.C, true);
		}
		return null;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		int callAddress = ((memory.getByte(address+1) & 0x0ff) | ((memory.getByte(address+2) << 8) & 0xFF00));
		return "CALL " + getFlagCondition(opcode) + ","+ toHexShort(callAddress);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		FlagCondition flagCondition = getFlagCondition(opcode);
		int callAddress = ((additionalBytes[0] & 0x0ff) | ((additionalBytes[1] << 8) & 0xFF00));
		if (flagCondition.evaluate(cpu)) {
			cpu.pushShort(cpu.getPC());
			cpu.setPC(callAddress);
		}

		return 12;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 3;
	}
}

