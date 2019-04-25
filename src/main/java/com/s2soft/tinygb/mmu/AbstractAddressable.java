package com.s2soft.tinygb.mmu;

public abstract class AbstractAddressable implements IAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public void setByte(int address, byte b, boolean fromCPU) {
		setByte(address, b);
	}
	
	@Override
	public byte getByte(int address, boolean fromCPU) {
		return getByte(address);
	}
}

