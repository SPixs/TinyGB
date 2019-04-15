package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.cpu.FlagCondition.FlagField;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

/**
 * JP cc,nn
 * Jump to address n if following condition is true:
 * cc = NZ, Jump if Z flag is reset.
 * cc = Z, Jump if Z flag is set.
 * cc = NC, Jump if C flag is reset.
 * cc = C, Jump if C flag is set.
 *
 * @author smametz
 */
public class InstrJPCond extends Instruction {

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
			case (byte)0xC2: return FlagCondition.NZ;
			case (byte)0xCA: return FlagCondition.Z;
			case (byte)0xD2: return FlagCondition.NC;
			case (byte)0xDA: return FlagCondition.C;
		}
		return null;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		FlagCondition flagCondition = getFlagCondition(opcode);
		int jumpAddress = BitUtils.toUShort(memory.getByte(address+1), memory.getByte(address+2));
		return "JP " + flagCondition.asText() + "," + toHexShort(jumpAddress);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		FlagCondition flagCondition = getFlagCondition(opcode);
		int jumpAddress = BitUtils.toUShort(additionalBytes[0], additionalBytes[1]);
		byte cycles = 12;
		if (flagCondition.evaluate(cpu)) {
			cpu.setPC(jumpAddress);
			cycles += 4;
		}
		return cycles;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 3;
	}
}

