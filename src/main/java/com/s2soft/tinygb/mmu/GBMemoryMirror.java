package com.s2soft.tinygb.mmu;

public class GBMemoryMirror extends AbstractAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private IAddressable m_original;
	private int m_offset;

	//	 =========================== Constructor =============================

	public GBMemoryMirror(IAddressable original, int offset) {
		m_original = original;
		m_offset = offset;
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public void setByte(int address, byte b) {
		m_original.setByte(address + m_offset, b);
	}

	@Override
	public byte getByte(int address) {
		return m_original.getByte(address + m_offset);
	}
}

