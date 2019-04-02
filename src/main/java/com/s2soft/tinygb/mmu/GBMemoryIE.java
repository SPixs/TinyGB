package com.s2soft.tinygb.mmu;

import com.s2soft.tinygb.cpu.Instruction;

public class GBMemoryIE implements IAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public void setByte(int address, byte b) {
		throw new IllegalStateException("Interrupts enable registyer IE not implemented. Write at " + Instruction.toHexShort(address));
	}

	@Override
	public byte getByte(int address) {
		throw new IllegalStateException("Interrupts enable registyer IE not implemented. Read at " + Instruction.toHexShort(address));
	}

}

