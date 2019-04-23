package com.s2soft.tinygb.mmu;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.gpu.GBGPU;

public final class GBMemoryOAM extends AbstractAddressable {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private byte[] m_oam = new byte[160];

	private GameBoy m_gameBoy;

	//	 =========================== Constructor =============================

	public GBMemoryOAM(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
	}
	
	//	 ========================== Access methods ===========================

	private boolean canAccess(boolean fromCPU) {
		return !fromCPU || !m_gameBoy.getGpu().isLCDEnabled() || 
				m_gameBoy.getGpu().getPhase() == GBGPU.PHASE_HBLANK || 
				m_gameBoy.getGpu().getPhase() == GBGPU.PHASE_VBLANK;
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
	public void setByte(int address, byte b, boolean fromCPU) {
//		throw new IllegalStateException("OAM not implemented. Write at " + Instruction.toHexShort(address));
//		System.out.println("OAM write " + Instruction.toHexByte(b)+ " at " + Instruction.toHexShort(address));
		if (canAccess(fromCPU)) {
			m_oam[address-0xFE00] = b;
		}
		else {
			System.out.println("Writing to OMA during GPU phase " + m_gameBoy.getGpu().getPhase().getName());
		}
	}

	@Override
	public byte getByte(int address, boolean fromCPU) {
//		throw new IllegalStateException("OAM not implemented. Read at " + Instruction.toHexShort(address));
//		System.out.println("Warning : OAM not implemented. Read at " + Instruction.toHexShort(address));
		return (byte) (canAccess(fromCPU) ? m_oam[address-0xFE00] : 0xFF);
	}
}

