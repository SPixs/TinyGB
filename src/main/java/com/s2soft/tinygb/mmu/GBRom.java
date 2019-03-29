package com.s2soft.tinygb.mmu;

import java.io.IOException;
import java.io.InputStream;

import com.s2soft.tinygb.cpu.Instruction;

public class GBRom implements IAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private int m_startAddress;
	private int m_length;
	
	private byte[] m_rom;

	//	 =========================== Constructor =============================

	/**
	 * @param startAddress the start address of this ROM area relative to global memory space of $0000
	 */
	public GBRom(int startAddress, int length) {
		m_startAddress = startAddress;
		m_length = length;
		m_rom = new byte[length];
	}
	
	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public void read(InputStream input) throws IOException {
		int read = input.read(m_rom, 0, m_rom.length);
		if (read != m_length) {
			System.out.println("Warning : read rom size does not match expecting size");
		}
	}
	
	public void setByte(int address, byte b) {
		System.out.println("Warning : attempting to write value " + Instruction.toHexByte((byte)b) + " at " + Instruction.toHexShort(address));
	}

	public byte getByte(int address) {
		if (address < m_startAddress || address >= m_startAddress + m_length) {
			System.out.println("Warning : attempting to read out of ROM area at " + Instruction.toHexShort(address));
			return 0;
		}
		return m_rom[address - m_startAddress];
	}
}

