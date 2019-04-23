package com.s2soft.tinygb.mmu;

import java.util.Arrays;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.gpu.GBGPU;

public final class GBMemoryVRAMMaps extends AbstractAddressable {

	//   ============================ Constants ==============================

	private final static boolean TRACE = false;
	
	private static final int BASE_ADDRESS = 0x9800;

	//	 =========================== Attributes ==============================
	
	private byte[] m_vram = new byte[0x800];

	private GameBoy m_gameBoy;

	//	 =========================== Constructor =============================

	public GBMemoryVRAMMaps(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
	}

	//	 ========================== Access methods ===========================


	public void reset() {
		Arrays.fill(m_vram, (byte)0);
	}

	//	 ========================= Treatment methods =========================

	private boolean canAccess(boolean fromCPU) {
		return !fromCPU || !m_gameBoy.getGpu().isLCDEnabled() || 
				m_gameBoy.getGpu().getPhase() == GBGPU.PHASE_HBLANK || 
				m_gameBoy.getGpu().getPhase() == GBGPU.PHASE_VBLANK ||
				m_gameBoy.getGpu().getPhase() == GBGPU.PHASE_FETCH_OAM;
	}
	
	@Override
	public void setByte(int address, byte b) {
		setByte(address, b, true);
	}
	
	@Override
	public byte getByte(int address) {
		return getByte(address, true);
	}
	
	@Override
	public void setByte(int address, byte value, boolean fromCPU) {
		if (canAccess(fromCPU)) {
			int offset = address - BASE_ADDRESS;
			if (TRACE) {
				int mapIndex = offset / 0x400;
				int tileX = (offset % 0x400) % 32;
				int tileY = (offset % 0x400) / 32;
				System.out.println("Writing to VRAM Map[" + mapIndex + "] tileX=" + tileX + "  tileY=" + tileY +" , value=" +
						Instruction.toHexByte(value));
			}
			m_vram[offset] = value;
		}
		else {
			if (TRACE) {
				System.out.println("Warning ! Writing to VRAM (maps) during GPU phase " + m_gameBoy.getGpu().getPhase().getName());
			}
		}

	}

	@Override
	public byte getByte(int address, boolean fromCPU) {
		if (canAccess(fromCPU)) {
			int offset = address - BASE_ADDRESS;
			if (TRACE) {
				int mapIndex = offset / 0x400;
				int tileX = (offset % 0x400) % 32;
				int tileY = (offset % 0x400) / 32;
				System.out.println("Reading from VRAM Map[" + mapIndex + "] tileX=" + tileX + "  tileY=" + tileY);
			}
			return m_vram[offset];
		}
		else {
			if (TRACE) {
				System.out.println("Warning ! Reading from VRAM (maps) during GPU phase " + m_gameBoy.getGpu().getPhase().getName());
			}
			return (byte) 0xFF;
		}
	}
}

