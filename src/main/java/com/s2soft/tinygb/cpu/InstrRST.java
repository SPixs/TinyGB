package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

/**
 * Restarts
 * Push present address onto stack.
 * Jump to address $0000 + n.
 * 
 * @author smametz
 *
 */
public class InstrRST extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return getJumpAddress(opcode) != -1;
	}
	
	private int getJumpAddress(byte opcode) {
		switch (opcode) {
			case (byte)0xC7 : return 0x0000;
			case (byte)0xCF : return 0x0008;
			case (byte)0xD7 : return 0x0010;
			case (byte)0xDF : return 0x0018;
			case (byte)0xE7 : return 0x0020;
			case (byte)0xEF : return 0x0028;
			case (byte)0xF7 : return 0x0030;
			case (byte)0xFF : return 0x0038;
		}
		return -1;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		return "RST " + toHexByte((byte)getJumpAddress(opcode));
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		int callAddress = getJumpAddress(opcode);
		cpu.pushShort(cpu.getPC());
		cpu.setPC(callAddress);

		return 16;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

