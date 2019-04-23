package com.s2soft.tinygb.dma;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class DMA {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private GBMemory m_memory;
	private int m_startAddress;
	private boolean m_transfertInProgress;
	private int m_elpasedTicks;
	private GameBoy m_gameBoy;

	//	 =========================== Constructor =============================

	public DMA(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
		m_memory = gameBoy.getMemory();
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public void step() {
		if (m_transfertInProgress) {
			if (m_elpasedTicks % 4 == 0) { // 4 machine clocks for 1 DMA transfert step
				int offset = m_elpasedTicks >> 2;
				int sourceAddress = m_startAddress + offset;
				if (sourceAddress >= 0xE000) {
					sourceAddress &= ~0x2000;
				}
				byte value = m_memory.getByte(sourceAddress, true);
				m_memory.setByte(0xFE00 + offset, value, true);
//				System.out.println("DMA copy from " + Instruction.toHexShort(sourceAddress) + " to " +
//						Instruction.toHexShort(0xFE00 + offset) + ", value " + Instruction.toHexByte(value));
			}
			m_elpasedTicks++;
			if (m_elpasedTicks == 640) {
				m_transfertInProgress = false;
			}
		}
	}

	public void start(byte startPageIndex) {
//		System.out.println("Starting DMA at line " + m_gameBoy.getGpu().getScanLine());
		m_startAddress = (startPageIndex << 8) & 0xFFFF;
		m_transfertInProgress = true;
		m_elpasedTicks = 0;
	}

	public byte getStartPageNumber() {
		return BitUtils.toByte(m_startAddress >> 8);
	}
}

