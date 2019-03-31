package com.s2soft.tinygb.mmu;

import java.util.Arrays;

import com.s2soft.tinygb.cpu.Instruction;

public class GBMemoryVRAMTiles implements IAddressable {

	//   ============================ Constants ==============================

	private static final int BASE_ADDRESS = 0x8000;

	//	 =========================== Attributes ==============================
	
	private byte[] m_vram = new byte[0x1800];

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================


	public void reset() {
		Arrays.fill(m_vram, (byte)0);
	}

	//	 ========================= Treatment methods =========================

	@Override
	public void setByte(int address, byte value) {
		int offset = address - BASE_ADDRESS;
		int blockIndex = offset / 0x800;
		int tileIndex = offset / 0x10;
		System.out.println("Writing to VRAM Tiles, block " + blockIndex + ", tile " + tileIndex + ", value = " +
				Instruction.toHexByte(value));
		m_vram[offset] = value;
	}

	@Override
	public byte getByte(int address) {
		int offset = address - BASE_ADDRESS;
		int blockIndex = offset / 0x800;
		int tileIndex = offset / 0x10;
//		System.out.println("Reading from VRAM Tiles, block " + blockIndex + ", tile " + tileIndex);
		return m_vram[offset];
	}
}

