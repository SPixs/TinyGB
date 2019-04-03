package com.s2soft.tinygb.mmu;

import com.s2soft.tinygb.cpu.Instruction;

public class GBMemoryIE implements IAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================
	
	private byte m_ir = 0;
	
	private GBMemory m_memory;

	//	 =========================== Constructor =============================

	public GBMemoryIE(GBMemory memory) {
		m_memory = memory;
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public void setByte(int address, byte b) {
//		throw new IllegalStateException("Interrupts enable registyer IE not implemented. Write at " + Instruction.toHexShort(address));
//		System.out.println("Warning : Interrupts enable register IE not implemented. Write " + Instruction.toHexByte(b) + " at " + Instruction.toHexShort(address));
		m_memory.setInterruptEnabled(b);
	}

	@Override
	public byte getByte(int address) {
//		throw new IllegalStateException("Interrupts enable registyer IE not implemented. Read at " + Instruction.toHexShort(address));
//		System.out.println("Warning : Interrupts enable registyer IE not implemented. Read at " + Instruction.toHexShort(address));
		return m_memory.getInterruptEnable();
	}

}

