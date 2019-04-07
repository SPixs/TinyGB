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
//		if (address == 0xFFE1) {
//			System.out.println(">>> Tetris set machine state : " + Instruction.toHexByte(b));
//		}
//		if (address == 0xFFA6) {
//			System.out.println(">>> Tetris writing to COUNTDOWN : " + Instruction.toHexByte(b));
//		}
//		if (address == 0xC000) {
//			System.out.println(">>> Tetris writing to SPRITE1.Y : " + Instruction.toHexByte(b));
//			Thread.yield();
//		}
//		if (address == 0xC001) {
//			System.out.println(">>> Tetris writing to SPRITE1.X : " + Instruction.toHexByte(b));
//		}
		if (address == (0xC141)) {
			Thread.yield();
		}
	}
	
	public byte getByte(int address) {
		if (address == 0xFFA6 && m_memory[address] == 0) {
			System.out.println(">>> Tetris reading COUNTDOWN : " + Instruction.toHexByte(m_memory[address]));
		}
		return m_memory[address];
	}
}

