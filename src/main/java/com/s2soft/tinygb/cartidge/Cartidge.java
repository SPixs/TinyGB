package com.s2soft.tinygb.cartidge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.IConfiguration;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.utils.BitUtils;
import com.s2soft.utils.StreamCopier;

public class Cartidge {

	//   ============================ Constants ==============================
	
	public final static boolean TRACE = false;
	
	private final static int[] NINTENDO_LOGO = new int[] {
		0xCE, 0xED, 0x66, 0x66, 0xCC, 0x0D, 0x00, 0x0B, 0x03, 0x73, 0x00, 0x83, 0x00, 0x0C, 0x00, 0x0D,
		0x00, 0x08, 0x11, 0x1F, 0x88, 0x89, 0x00, 0x0E, 0xDC, 0xCC, 0x6E, 0xE6, 0xDD, 0xDD, 0xD9, 0x99,
		0xBB, 0xBB, 0x67, 0x63, 0x6E, 0x0E, 0xEC, 0xCC, 0xDD, 0xDC, 0x99, 0x9F, 0xBB, 0xB9, 0x33, 0x3E
	};

	public enum GameboyModel {
		DMG,
		CGB,
		BOTH
	}
	
	public enum Type {
		ROM_ONLY(0x00, (c) -> new RomOnlyCartidgeStrategy(c)),
		MBC1(0x01, (c) -> new MBC1Strategy(c, false, false)),
		MBC1_RAM(0x02, (c) -> new MBC1Strategy(c, true, false)),
		MBC1_RAM_BATTERY(0x03, (c) -> new MBC1Strategy(c, true, true));
		
		private int type;
		private IStrategyFactory strategyFactory;

		Type(int type, IStrategyFactory strategyFactory) {
			this.type = type;
			this.strategyFactory = strategyFactory;
		}
		
		CartidgeStrategy createStrategy(Cartidge c) { return strategyFactory.createStrategy(c); }
		public int getType() {
			return type;
		}
		
		public static Type fromValue(int typeCode) {
			for (Type type : Type.values()) {
				if (type.getType() == typeCode) { return type; }
			}
			throw new IllegalArgumentException("Cartidge type not handled : " + Instruction.toHexByte((byte)typeCode));
		}
	}
	
	//	 =========================== Attributes ==============================

	private byte[] m_rom;
	private byte[] m_ram;

	private StringBuffer m_name;

	private GameboyModel m_gameboyModel;

	private CartidgeStrategy m_strategy;

	private int m_romBanks;

	private boolean m_ramModified;
	
	private String m_manufacturerCode;
	private String m_licenseeCode;
	private byte m_headerChecksum;

	private String m_cartidgeID;

	private GameBoy m_gameboy;

	private int m_globalChecksum;

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	public Cartidge(GameBoy gameboy) {
		m_gameboy = gameboy;
	}

	public int getRomBanks() {
		return m_romBanks;
	}

	public byte[] getROM() { return m_rom; }
	public byte[] getRAM() { return m_ram; }

	public void setRamModified(boolean ramModified) {
		m_ramModified = ramModified;
	}

	//	 ========================= Treatment methods =========================

	public void read(InputStream input) throws IOException {
		if (input == null) {
			throw new IllegalArgumentException("Cartidge ressource not found");
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamCopier.copy(input, outputStream);
		m_rom = outputStream.toByteArray();
		
		// Parse header
		checkNintendoLogo();
		
		// Get cartidge name
		byte[] titleBytes = new byte[0x0143-0x134];
		System.arraycopy(m_rom, 0x0134, titleBytes, 0, titleBytes.length);
		StringBuffer titleBuffer = new StringBuffer();
		for (byte character : titleBytes) {
			if (character != 0) {
				titleBuffer.append((char)character);
			}
		}
		m_name = titleBuffer;
		System.out.println("Cartidge name : " + m_name);
		
		// Parse manufacturer code
		byte[] manufacturerCode = new byte[4];
		System.arraycopy(m_rom, 0x013F, manufacturerCode, 0, 4);
		m_manufacturerCode = new String(manufacturerCode);

		// Read gameboy model
		switch (m_rom[0x0143]) {
			case (byte)0x80 : m_gameboyModel = GameboyModel.BOTH; break;
			case (byte)0xC0 : m_gameboyModel = GameboyModel.CGB; break;
			default : m_gameboyModel = GameboyModel.DMG; break;
		}
		if (m_gameboyModel == GameboyModel.CGB) {
			throw new IllegalStateException("Color gameboy cartidge not handled yet");
		}
		
		// Licensee code
		byte[] licenseeCode = new byte[2];
		System.arraycopy(m_rom, 0x0144, licenseeCode, 0, 2);
		m_licenseeCode = new String(licenseeCode);
		
		// Cartidge type (ROM, MBC1, ...)
		Type type = Type.fromValue(m_rom[0x0147]);
		System.out.println("Cartidge type : " + type.name());
		m_strategy = type.createStrategy(this);
		
		// Retrieve ROM size (banks)
		m_romBanks = getRomBanks(m_rom[0x0148]);
		System.out.println("Cartidge ROM banks : " + m_romBanks + " (" + (16 * 1024 * m_romBanks) + " bytes)");
		
		// Retrieve RAM size (banks)
		int ramSize = getRamSize(m_rom[0x0149]) * 1024;
		System.out.println("Cartidge RAM size : " + ramSize + " bytes");
		m_ram = new byte[ramSize];
		
		// Parse checksum
		m_headerChecksum = m_rom[0x014D];
		m_globalChecksum = BitUtils.toUShort(m_rom[0x14F], m_rom[0x14E]);
		
		// Unique ID used to create RAM save file
		m_cartidgeID = Instruction.toHexShort(m_globalChecksum).substring(1) + "-" + Instruction.toHexByte(m_headerChecksum).substring(1);
		
		byte[] savedRAM = m_gameboy.getLastRAMSave(m_cartidgeID);
		if (savedRAM != null && savedRAM.length == ramSize) {
			m_ram = savedRAM;
		}
	}
	
	/**
	 * @param value encoded rom count
	 * @return the number of rom bank embeded in cartidge (16KB each)
	 */
	private int getRomBanks(int value) {
		if (value <= 8) { return 2 << value; }
		switch (value) {
			case 0x52: return 72; 
			case 0x53: return 80; 
			case 0x54: return 96;
			default:
				throw new IllegalStateException("Unknown bank numbers code : " + Instruction.toHexByte((byte)value));
		}
	}
	
	/**
	 * @param value
	 * @return the cartidge RAM size in KB
	 */
	private int getRamSize(int value) {
		switch (value) {
			case 0x00 : return 0;
			case 0x01 : return 2;
			case 0x02 : return 8;
			case 0x03 : return 32;
			case 0x04 : return 128;
			case 0x05 : return 64;
			default:
				throw new IllegalStateException("Unknown RAM size code : " + Instruction.toHexByte((byte)value));
		}
	}

	private void checkNintendoLogo() {
		boolean success = true;
		for (int i=0x104;i<=0x0133;i++) {
			success &= (NINTENDO_LOGO[i-0x0104] == (getROMByte(i) & 0xFF));
		}
		if (!success) {
			System.out.println("Cartidge Nintendo logo check failed");
		}
	}
	public void setROMByte(int address, byte value) { 
		m_strategy.writeROM(address, value); 
	}
	
	public byte getROMByte(int address) {
		return m_strategy == null ? (byte) 0xFF : m_strategy.readROM(address); 
	}


	/**
	 * @param address (relative to absolute address 0xA000)
	 * @return
	 */
	public byte getRAMByte(int address) {
		return m_strategy.readRAM(address);
	}

	/**
	 * @param address the RAM address relative to absolute address 0xA000
	 * @param value
	 */
	public void setRAMByte(int address, byte value) { 
		m_strategy.writeRAM(address, value);
	}
	
	public void saveRAM() {
		if (m_ramModified) {
			m_gameboy.save(this, m_cartidgeID);
		}
		m_ramModified = false;
	}
}

