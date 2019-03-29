package com.s2soft.tinygb.mmu;

import com.s2soft.tinygb.cpu.Instruction;

public class GBMemoryRAM implements IAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	/**
	 * 64Ko of addressable memory.
	 * Up to now, assume that all memory is RAM. 
	 */
	private byte[] m_memory = new byte[65536];
	
	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public void setByte(int address, byte b) {
		m_memory[address] = b;
		System.out.println("Set " + Instruction.toHexByte((byte)(b & 0x0FF)) + " at " + Instruction.toHexShort(address) );
	}
	
	public byte getByte(int address) {
		return m_memory[address];
	}
}

