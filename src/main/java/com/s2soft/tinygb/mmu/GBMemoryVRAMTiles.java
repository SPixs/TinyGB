package com.s2soft.tinygb.mmu;

import java.util.Arrays;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.gpu.GBGPU;

public final class GBMemoryVRAMTiles extends AbstractAddressable {

	//   ============================ Constants ==============================

	private final static boolean TRACE = false;
	
	private static final int BASE_ADDRESS = 0x8000;

	//	 =========================== Attributes ==============================
	
	private byte[] m_vram = new byte[0x1800];

	private GameBoy m_gameBoy;

	//	 =========================== Constructor =============================

	public GBMemoryVRAMTiles(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
	}
	
	//	 ========================== Access methods ===========================

	public void reset() {
		Arrays.fill(m_vram, (byte)0);
	}

	private boolean canAccess(boolean cpuAccess) {
		return !cpuAccess || !m_gameBoy.getGpu().isLCDEnabled() || 
				m_gameBoy.getGpu().getPhase() == GBGPU.PHASE_HBLANK || 
				m_gameBoy.getGpu().getPhase() == GBGPU.PHASE_VBLANK ||
				m_gameBoy.getGpu().getPhase() == GBGPU.PHASE_FETCH_OAM;
	}
	
	//	 ========================= Treatment methods =========================
	
	@Override
	public void setByte(int address, byte b) {
		setByte(address, b, true);
	}
	
	@Override
	public byte getByte(int address) {
		return getByte(address, true);
	}

	@Override
	public void setByte(int address, byte value, boolean cpuAccess) {
		if (canAccess(cpuAccess)) {
			int offset = address - BASE_ADDRESS;
			int blockIndex = offset / 0x800;
			int tileIndex = offset / 0x10;
			if (TRACE) {
				System.out.println("Writing to VRAM Tiles, block " + blockIndex + ", tile " + tileIndex + ", value = " +
						Instruction.toHexByte(value));
			}
			m_vram[offset] = value;
		}
		else {
			if (TRACE) {
				System.out.println("Warning ! Writing to VRAM (tiles) during GPU phase " + m_gameBoy.getGpu().getPhase().getName());
			}
		}
	}

	@Override
	public byte getByte(int address, boolean cpuAccess) {
		if (canAccess(cpuAccess)) {
			int offset = address - BASE_ADDRESS;
			if (TRACE) {
				int blockIndex = offset / 0x800;
				int tileIndex = offset / 0x10;
				System.out.println("Reading from VRAM Tiles, block " + blockIndex + ", tile " + tileIndex);
			}
			return m_vram[offset];
		}
		else {
			if (TRACE) {
				System.out.println("Warning ! Writing to VRAM (tiles) during GPU phase " + m_gameBoy.getGpu().getPhase().getName());
			}
			return (byte) 0xFF;
		}
	}
}

