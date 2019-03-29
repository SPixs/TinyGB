package com.s2soft.tinygb.mmu;

import com.s2soft.tinygb.GameBoy;

public class GBMemory {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private GBRom m_bootRom = null; 				// $0000 - $00FF
	private GBMemoryVRAMTiles m_vramTiles = null; 	// $8000 - $97FF ([0]:$8000-$87FF [1]:$8800-$8FFF [1]:$9000-$97FF)
	private GBMemoryVRAMMaps m_vramMaps;			// $9800 - $9FFF ([0]:$9800-$9BFF [1]:$9C00-$9FFF)
	private GBMemoryIO m_ioMemory; 					// $FF00 - $FF7F

	private GBMemoryRAM m_ramMemory;

	/**
	 * The boot ROM is a bootstrap program which is a 256 bytes big piece of code 
	 * which checks the cartridge header is correct, scrolls the Nintendo bootup 
	 * graphics and plays the "po-ling" sound.
	 */
	private boolean m_bootROMLock;
	
	//	 =========================== Constructor =============================
	
	public GBMemory(GameBoy gameBoy) {
		
		m_ioMemory = new GBMemoryIO(gameBoy); // $FF00 - $FF7F
		
		m_bootRom = new GBRom(0, 256);
		try {
			m_bootRom.read(getClass().getResourceAsStream("/DMG_ROM.bin"));
		}
		catch (Exception ex) {
		}
		
		m_vramTiles = new GBMemoryVRAMTiles();
		m_vramMaps = new GBMemoryVRAMMaps();
		m_ramMemory = new GBMemoryRAM();
	}

	//	 ========================== Access methods ===========================

	public void setBootROMLock(boolean b) {
		m_bootROMLock = b;
	}
	
	//	 ========================= Treatment methods =========================
	
	public void reset() {
		m_ioMemory.reset();
		m_vramTiles.reset();
	}

	private IAddressable getAddressable(int address) {
//		if (address == 0x0FFFE) {
//			Thread.yield();
//		}
		if ((address & 0xFF80) == 0xFF00) {
			return m_ioMemory;
		}
		if (address < 0x0100 && m_bootROMLock) {
			return m_bootRom;
		}
		if (address >= 0x8000 && address <= 0x97FF) {
			return m_vramTiles;
		}
		if (address >= 0x9800 && address <= 0x9FFF) {
			return m_vramMaps;
		}
		return m_ramMemory;
	}
	
	public void setByte(int address, byte b) {
		getAddressable(address).setByte(address, b);
	}
	
	public byte getByte(int address) {
		return getAddressable(address).getByte(address);
	}
	
	public short getShort(int address) {
		return (short) ((getByte(address) << 8) | (getByte(address+1) & 0x00FF)); 
	}

//	public void loadFromFile(File file, short startAddress) throws IOException {
//		FileInputStream inputStream = new FileInputStream(file);
//		inputStream.read(m_memory, startAddress, (int)file.length());
//		inputStream.close();
//	}
//
//	public byte[] getBytes(int address, int nibble) {
//		return Arrays.copyOfRange(m_memory, address, address + nibble);
//	}
}

