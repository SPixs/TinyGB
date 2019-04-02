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

