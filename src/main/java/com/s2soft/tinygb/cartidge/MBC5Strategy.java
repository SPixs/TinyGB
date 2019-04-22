package com.s2soft.tinygb.cartidge;

import java.util.Formatter.BigDecimalLayoutForm;

import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.utils.BitUtils;

public class MBC5Strategy extends CartidgeStrategy {

	//   ============================ Constants ==============================
	
	//	 =========================== Attributes ==============================
	
	private int m_ramBankIndex = 0;
	private int m_romBankIndex = 1;

	private boolean m_ramEnabled;
	private ModeSelect m_modeSelect = ModeSelect.ROM;
	
	private boolean m_hasRam;
	private boolean m_hasBattery;

	private enum ModeSelect {
		ROM,
		RAM
	}
	
	//	 =========================== Constructor =============================

	public MBC5Strategy(Cartidge cartidge, boolean hasRam, boolean hasBattery) {
		super(cartidge);
		m_hasRam = hasRam;
		m_hasBattery = hasBattery;
	}
	
	//	 ========================== Access methods ===========================
		
	//	 ========================= Treatment methods =========================
	
	@Override
	public void writeROM(int address, byte value) {
		address &= 0xFFFF;
		// RAM Enable (Write Only)
		if (address < 0x2000) {
			m_ramEnabled = ((value & 0x0F) == 0x0A);
		}
		// Low 8 bits of ROM Bank Number (Write Only)
		// The lower 8 bits of the ROM bank number goes here. Writing 0 will indeed give bank 0 on MBC5, unlike other MBCs.
		else if (address < 0x3000) {
			m_romBankIndex = ((m_romBankIndex & 0x100) | (value & 0xFF));
			if (m_romBankIndex >= getCartidge().getRomBanks() && Cartidge.TRACE) {
				System.out.println("Warning : selecting a ROM bank out of range : " + m_romBankIndex + " >= " + getCartidge().getRomBanks());
			}
		}
		// High bit of ROM Bank Number (Write Only)
		// The 9th bit of the ROM bank number goes here.
		else if (address < 0x4000) {
			m_romBankIndex = ((m_romBankIndex & 0xFF) | ((value & 0x01) << 8));
			if (m_romBankIndex >= getCartidge().getRomBanks() && Cartidge.TRACE) {
				System.out.println("Warning : selecting a ROM bank out of range : " + m_romBankIndex + " >= " + getCartidge().getRomBanks());
			}
		}
		else if (address < 0x6000) {
			m_ramBankIndex  = value & 0x0F;
			if (m_ramBankIndex * 8 * 1024 >= getCartidge().getRAM().length && Cartidge.TRACE) {
				System.out.println("Warning : selecting a RAM bank out of range : " + m_ramBankIndex + " >= " + getCartidge().getRAM().length / (8 * 1024));
			}
		}
		else if (Cartidge.TRACE) {
			System.out.println("Warning : ROM write address out of range : " + Instruction.toHexShort(address));
		}
	}
	
	@Override
	public byte readROM(int address) {
		if (address >= 0x8000) { throw new IllegalArgumentException("ROM address out of range : " + Instruction.toHexShort(address)); }
		if (address < 0x4000) { return getROM()[address]; }
		return getROM()[getROMBank() * 0x4000 + (address - 0x4000)];
	}
	
	@Override
	public void writeRAM(int address, byte value) {
		if (!m_hasRam) {
			if (Cartidge.TRACE) {
				System.out.println("Warning : trying to write to RAM in a cartidge without RAM");
			}
		}

		if (!m_ramEnabled) {
			if (Cartidge.TRACE) {
				System.out.println("Warning : trying to write to RAM while it is disabled");
			}
			return;
		}
		
		getRAM()[getRAMBank() * 0x2000 + address] = value;
		getCartidge().setRamModified(true);
	}

	@Override
	public byte readRAM(int address) {
		if (!m_hasRam) {
			if (Cartidge.TRACE) {
				System.out.println("Warning : trying to read from RAM in a cartidge without RAM");
			}
		}

		if (!m_ramEnabled) {
			if (Cartidge.TRACE) {
				System.out.println("Warning : reading from RAM while it is disabled");
			}
			return (byte) 0xFF;
		}
		return getRAM()[getRAMBank() * 0x2000 + address];
	}

	private int getRAMBank() {
		int ramBank = m_ramBankIndex;
		return ramBank;
	}

	private int getROMBank() {
		int romBank = m_romBankIndex;
		return romBank % getCartidge().getRomBanks(); // if the ROM bank is illegal (too high), loop to first bank
	}
}