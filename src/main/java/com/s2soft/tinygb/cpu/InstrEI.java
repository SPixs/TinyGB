package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

/**
 * Enable interrupts. This intruction enables interrupts but not immediately. 
 * Interrupts are enabled after instruction after EI is executed.
 * 
 * @author smametz
 */
public class InstrEI extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xFB;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "EI";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		// interrupts activation must be delayed after next instruction
		cpu.setInterruptEnabled(true, true);
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

