package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

/**
 * This instruction disables interrupts but not immediately. 
 * Interrupts are disabled after instruction after DI is executed.
 * 
 * @author smametz
 */
public class InstrDI extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xF3;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		return "DI";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		// TODO : implement interrupts
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

