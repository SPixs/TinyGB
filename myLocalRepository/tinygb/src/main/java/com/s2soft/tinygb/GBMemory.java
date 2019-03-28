package com.s2soft.tinygb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class GBMemory {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	/**
	 * 64Ko of addressable memory.
	 * Up to now, assume that all memory is RAM. 
	 */
	private byte[] m_memory = new byte[65536];
	
	/**
	 * If true, then the first 256 bytes in memory are the Gameboy boot ROM.
	 * The boot ROM is a bootstrap program which is a 256 bytes big piece of code 
	 * which checks the cartridge header is correct, scrolls the Nintendo bootup 
	 * graphics and plays the "po-ling" sound.
	 */
	private boolean m_bootStrapEnabled;
	
	private GBRom m_bootRom = null;
	
	//	 =========================== Constructor =============================
	
	public GBMemory() {
		reset();
		m_bootRom = new GBRom(0, 256);
		try {
			m_bootRom.read(getClass().getResourceAsStream("/DMG_ROM.bin"));
		}
		catch (Exception ex) {
			
		}
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	public void reset() {
		m_bootStrapEnabled = true;
	}

	public void setByte(int address, int b) {
		m_memory[address] = (byte)(b & 0x0FF);
		System.out.println("Set " + Instruction.toHexByte((byte)(b & 0x0FF)) + " at " + Instruction.toHexShort(address) );
	}
	
	public byte getByte(int address) {
		if (m_bootStrapEnabled && address < 0x0100) {
			return m_bootRom.getByte(address);
		}
		return m_memory[address];
	}
	
	public short getShort(int address) {
		return (short) ((getByte(address) << 8) | (getByte(address+1) & 0x00FF)); 
	}
	
	public void loadFromFile(File file, short startAddress) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		inputStream.read(m_memory, startAddress, (int)file.length());
		inputStream.close();
	}

	public byte[] getBytes(int address, int nibble) {
		return Arrays.copyOfRange(m_memory, address, address + nibble);
	}
}

