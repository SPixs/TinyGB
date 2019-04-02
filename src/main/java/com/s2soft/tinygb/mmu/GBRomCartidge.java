package com.s2soft.tinygb.mmu;

import com.s2soft.tinygb.Cartidge;

public class GBRomCartidge implements IAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================
	
	private Cartidge m_cartidge = null;

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================
	
	public void setCartidge(Cartidge cartidge) {
		m_cartidge = cartidge;
	}

	//	 ========================= Treatment methods =========================

	@Override
	public void setByte(int address, byte b) {
		if (address <= 0x1FFF) {
			System.out.println("Cartidge RAM enabled : " + ((b == (byte)0x0A) ? true : false ));
			return;
		}
		if (address >= 0x2000 && address <= 0x3FFF) {
			System.out.println("Writing ROM bank number : " + b);
			return;
		}
		if (address >= 0x4000 && address <= 0x5FFF) {
			System.out.println("Writing RAM bank number or Upper bits of ROM bank number : " + b);
			return;
		}
		if (address >= 0x6000 && address <= 0x7FFF) {
			System.out.println("Writing ROM/RAM Mode select : " + b);
			return;
		}
		throw new IllegalStateException("Cannot write to ROM");
	}

	@Override
	public byte getByte(int address) {
		if (m_cartidge == null) {
			return (byte) 0xFF;
		}
		return m_cartidge.getROMByte(address);
	}

}

