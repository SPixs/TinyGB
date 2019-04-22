package com.s2soft.tinygb.cartidge;

import java.util.Formatter.BigDecimalLayoutForm;

import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.utils.BitUtils;

public class MBC1Strategy extends CartidgeStrategy {

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

	public MBC1Strategy(Cartidge cartidge, boolean hasRam, boolean hasBattery) {
		super(cartidge);
		m_hasRam = hasRam;
		m_hasBattery = hasBattery;
	}
	
	//	 ========================== Access methods ===========================
		
	//	 ========================= Treatment methods =========================
	
	@Override
	public void writeROM(int address, byte value) {
		address &= 0xFFFF;
		// RAM enabling ?
		if (address < 0x2000) {
			m_ramEnabled = ((value & 0x0F) == 0x0A);
		}
		// ROM bank selection (lower 5 bits) ?
		else if (address < 0x4000) {
			m_romBankIndex = ((m_romBankIndex & 0b11100000) | (value & 0b00011111));
			if (m_romBankIndex == 0) m_romBankIndex++;
			if (m_romBankIndex >= getCartidge().getRomBanks() && Cartidge.TRACE) {
				System.out.println("Warning : selecting a ROM bank out of range : " + m_romBankIndex + " >= " + getCartidge().getRomBanks());
			}
		}
		// ROM bank selection (upper 3 bits) or RAM bank
		else if (address < 0x6000) {
			if (m_modeSelect == ModeSelect.ROM) {
				m_romBankIndex = ((m_romBankIndex & 0b00011111) | (value & 0b11100000));
				if (m_romBankIndex >= getCartidge().getRomBanks() &&Cartidge.TRACE) {
					System.out.println("Warning : selecting a ROM bank out of range : " + m_romBankIndex + " >= " + getCartidge().getRomBanks());
				}
			}
			else {
				m_ramBankIndex  = value & 0x03;
			}
		}
		// RAM/ROM mode select
		else if (address < 0x8000) {
			m_modeSelect = BitUtils.isSet(value, 0) ? ModeSelect.RAM : ModeSelect.ROM;
			if (m_modeSelect == ModeSelect.RAM && !m_hasRam && Cartidge.TRACE) {
				System.out.println("Warning : trying to select RAM mode is a cartidge without RAM");
			}
		}
		else 
			throw new IllegalArgumentException("ROM write address out of range : " + Instruction.toHexShort(address));
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
		if (m_modeSelect == ModeSelect.ROM) {
			ramBank = 0;
		}
		return ramBank;
	}

	private int getROMBank() {
		int romBank = m_romBankIndex;
		if (m_modeSelect == ModeSelect.RAM) {
			romBank &= 0b00011111;
		}
		return romBank % getCartidge().getRomBanks(); // if the ROM bank is illegal (too high), loop to first bank
	}
}