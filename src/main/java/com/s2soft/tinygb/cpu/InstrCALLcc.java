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
			case (byte)0xC4: return FlagCondition.NZ;
			case (byte)0xCC: return FlagCondition.Z;
			case (byte)0xD4: return FlagCondition.NC;
			case (byte)0xDC: return FlagCondition.C;
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
		byte cycles = 12;
		if (flagCondition.evaluate(cpu)) {
			cpu.pushShort(cpu.getPC());
			cpu.setPC(callAddress);
			cycles += 12;
		}
		
		return cycles;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 3;
	}
}

