package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.cpu.FlagCondition.FlagField;
import com.s2soft.tinygb.mmu.GBMemory;

public class InstrJRCond extends Instruction {

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
			case 0x020: return FlagCondition.NZ;
			case 0x028: return FlagCondition.Z;
			case 0x030: return FlagCondition.NC;
			case 0x038: return FlagCondition.C;
		}
		return null;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		FlagCondition flagCondition = getFlagCondition(opcode);
		byte offset = memory.getByte(address+1);
		return "JR " + flagCondition.asText() + "," + toHexShort(address + offset + getLengthInBytes(opcode));
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		FlagCondition flagCondition = getFlagCondition(opcode);
		byte offset = additionalBytes[0];
		byte cycles = 8;
		if (flagCondition.evaluate(cpu)) {
			cpu.setPC(cpu.getPC() + offset);
			cycles += 4;
		}
		return cycles;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 2;
	}
}

