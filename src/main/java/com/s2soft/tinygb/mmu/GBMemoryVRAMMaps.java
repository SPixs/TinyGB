package com.s2soft.tinygb.mmu;

import java.util.Arrays;

import com.s2soft.tinygb.cpu.Instruction;

public class GBMemoryVRAMMaps implements IAddressable {

	//   ============================ Constants ==============================

	private final static boolean TRACE = false;
	
	private static final int BASE_ADDRESS = 0x9800;

	//	 =========================== Attributes ==============================
	
	private byte[] m_vram = new byte[0x800];

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================


	public void reset() {
		Arrays.fill(m_vram, (byte)0);
	}

	//	 ========================= Treatment methods =========================

	@Override
	public void setByte(int address, byte value) {
		int offset = address - BASE_ADDRESS;
		int mapIndex = offset / 0x400;
		int tileX = (offset % 0x400) % 32;
		int tileY = (offset % 0x400) / 32;
		if (TRACE) {
			System.out.println("Writing to VRAM Map[" + mapIndex + "] tileX=" + tileX + "  tileY=" + tileY +" , value=" +
					Instruction.toHexByte(value));
		}
		m_vram[offset] = value;
	}

	@Override
	public byte getByte(int address) {
		int offset = address - BASE_ADDRESS;
		int mapIndex = offset / 0x400;
		int tileX = (offset % 0x400) % 32;
		int tileY = (offset % 0x400) / 32;
//		System.out.println("Reading from VRAM Map[" + mapIndex + "] tileX=" + tileX + "  tileY=" + tileY);
		return m_vram[offset];
	}
}

