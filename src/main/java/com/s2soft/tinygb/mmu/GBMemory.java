package com.s2soft.tinygb.mmu;

import java.io.FileInputStream;
import java.io.InputStream;

import com.s2soft.tinygb.Cartidge;
import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.utils.BitUtils;

public class GBMemory {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private GBRom m_bootRom = null; 				// $0000 - $00FF
	private GBRomCartidge m_cartidgeROM;			// $0000 - $7FFF
	private GBMemoryVRAMTiles m_vramTiles = null; 	// $8000 - $97FF ([0]:$8000-$87FF [1]:$8800-$8FFF [1]:$9000-$97FF)
	private GBMemoryVRAMMaps m_vramMaps;			// $9800 - $9FFF ([0]:$9800-$9BFF [1]:$9C00-$9FFF)
	private GBRamCartidge m_cartidgeRAM;			// $A000 - $BFFF 8KB External RAM
	private GBMemoryRAM m_ramMemory;				// $C000 - $DFFF 8KB Internal RAM
	private GBMemoryMirror m_ramMirror;				// $E000 - $FDFF Mirror of C000~DDFF (ECHO RAM)	Typically not used
	private GBMemoryOAM m_oam;						// $FE00 - $FE9F Sprite attribute table (OAM)
	private GBMemoryNA m_naMemory;					// $FEA0 - $FEFF Not Usable
	private GBMemoryIO m_ioMemory; 					// $FF00 - $FF7F
	private GBMemoryRAM m_hram; 					// $FF80 - $FFFE
	private GBMemoryIE m_ieMemory; 					// $FFFF		 Interrupts Enable Register (IE)

	/**
	 * The boot ROM is a bootstrap program which is a 256 bytes big piece of code 
	 * which checks the cartridge header is correct, scrolls the Nintendo bootup 
	 * graphics and plays the "po-ling" sound.
	 */
	private boolean m_bootROMLock;
	
	/**
	 * Interrupt enable.
	 * When bits are set, the corresponding interrupt can be triggered.
	 * 0	Vblank
	 * 1	LCD stat
	 * 2	Timer
	 * 3	Serial
	 * 4	Joypad
	 */
	private byte m_interruptEnable = 0;

	/**
	 * Interrupt flag.
	 * When bits are set, an interrupt has happened.
	 * Bits in the same order as FFFF.
	 */
	private byte m_interruptFlag = (byte) 0xE1;
	
	//	 =========================== Constructor =============================
	
	public GBMemory(GameBoy gameBoy) {
		
		m_ioMemory = new GBMemoryIO(gameBoy); // $FF00 - $FF7F
		
		m_bootRom = new GBRom(0, 256);
		try {
			m_bootRom.read(getClass().getResourceAsStream("/DMG_ROM.bin"));
//			m_bootRom.read(new FileInputStream("EMPTY_DMG_ROM.bin"));
		}
		catch (Exception ex) {
		}
		
		m_cartidgeROM = new GBRomCartidge();
		m_cartidgeRAM = new GBRamCartidge();
		
		m_vramTiles = new GBMemoryVRAMTiles();
		m_vramMaps = new GBMemoryVRAMMaps();
		m_ramMemory = new GBMemoryRAM();
		m_hram = new GBMemoryRAM();
		m_ieMemory = new GBMemoryIE(this);
		m_oam = new GBMemoryOAM();
		m_naMemory = new GBMemoryNA();
		m_ramMirror = new GBMemoryMirror(m_ramMemory, -0x2000);
	}

	//	 ========================== Access methods ===========================

	public void setBootROMLock(boolean b) {
		m_bootROMLock = b;
	}
	
	public boolean isBootROMLock() {
		return m_bootROMLock;
	}

	public boolean isInterruptRequested() {
        return (m_interruptEnable & m_interruptFlag) != 0;
    }
	
	public byte getInterruptFlag() {
//		System.out.println("Read interrupt flag " + Instruction.toHexByte(m_interruptFlag));
		return m_interruptFlag;
	}

	public void setInterruptFlag(byte flags) {
//		System.out.println("Write interrupt flag " + Instruction.toHexByte(flags));
		m_interruptFlag = flags;
	}
	
	public byte getInterruptEnable() {
		return m_interruptEnable;
	}

	public void setInterruptEnabled(byte enabled) {
		m_interruptEnable = enabled;
	}

	//	 ========================= Treatment methods =========================
	
	public void setCartidge(Cartidge cartidge) {
		m_cartidgeROM.setCartidge(cartidge);
		m_cartidgeRAM.setCartidge(cartidge);
	}
	
	public void loadROM(InputStream input) {
	}
	
	public void reset() {
		m_ioMemory.reset();
		m_vramTiles.reset();
	}

	private IAddressable getAddressable(int address) {
		if (address > 0xFFFF) {
			throw new IllegalStateException("Address out of range : $" + Integer.toHexString(address));
		}
		if ((address & 0xFF80) == 0xFF00) {
			return m_ioMemory;
		}
		if (address < 0x0100 && m_bootROMLock) {
			return m_bootRom;
		}
		if (address < 0x8000) {
			return m_cartidgeROM;
		}
		if (address >= 0x8000 && address <= 0x97FF) {
			return m_vramTiles;
		}
		if (address >= 0x9800 && address <= 0x9FFF) {
			return m_vramMaps;
		}
		if (address >= 0xA000 && address <= 0xBFFF) {
			return m_cartidgeRAM;
		}
		if (address >= 0xC000 && address <= 0xDFFF) {
			return m_ramMemory;
		}
		if (address >= 0xE000 && address <= 0xFDFF) {
			return m_ramMirror;
		}
		if (address >= 0xFE00 && address <= 0xFE9F) {
			return m_oam;
		}
		if (address >= 0xFEA0 && address <= 0xFEFF) {
			return m_naMemory;
		}
		if (address >= 0xFF80 && address <= 0xFFFE) {
			return m_hram;
		}
		if (address == 0xFFFF) {
			return m_ieMemory;
		}
		throw new IllegalStateException("No memory mapped at address " + Instruction.toHexShort(address));
	}
	
	public void setByte(int address, byte b) {
		final IAddressable addressable = getAddressable(address);
		if (addressable == null) {
			throw new IllegalStateException("No memory mapped at : " + Instruction.toHexShort(address));
		}
		addressable.setByte(address, b);
	}
	
	public byte getByte(int address) {
		return getAddressable(address).getByte(address);
	}
	
	public short getShort(int address) {
		return (short) ((getByte(address) << 8) | (getByte(address+1) & 0x00FF)); 
	}
	
	public void requestInterrupt(int index) {
//		System.out.println("Request interrupt : " + index);
		m_interruptFlag = BitUtils.setBit(m_interruptFlag, index, true);
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

