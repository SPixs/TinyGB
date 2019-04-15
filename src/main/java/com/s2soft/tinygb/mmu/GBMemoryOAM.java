package com.s2soft.tinygb.mmu;

public final class GBMemoryOAM implements IAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private byte[] m_oam = new byte[160];


	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public void setByte(int address, byte b) {
//		throw new IllegalStateException("OAM not implemented. Write at " + Instruction.toHexShort(address));
//		System.out.println("Warning : OAM not implemented. Write at " + Instruction.toHexShort(address));
		m_oam[address-0xFE00]=b;
	}

	@Override
	public byte getByte(int address) {
//		throw new IllegalStateException("OAM not implemented. Read at " + Instruction.toHexShort(address));
//		System.out.println("Warning : OAM not implemented. Read at " + Instruction.toHexShort(address));
		return m_oam[address-0xFE00];
	}
}

