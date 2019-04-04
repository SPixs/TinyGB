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

	//	 =========================== Constructor =============================

	public DMA(GameBoy gameBoy) {
		m_memory = gameBoy.getMemory();
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public void step() {
		if (m_transfertInProgress) {
			if (m_elpasedTicks % 4 == 0) {
				int offset = m_elpasedTicks >> 2;
				byte value = m_memory.getByte(m_startAddress + offset);
				m_memory.setByte(0xFE00 + offset, value);
//				System.out.println("DMA copy from " + Instruction.toHexShort(m_startAddress + offset) + " to " +
//						Instruction.toHexShort(0xFE00 + offset) + ", value " + Instruction.toHexByte(value));
			}
			m_elpasedTicks++;
			if (m_elpasedTicks == 640) {
				m_transfertInProgress = false;
			}
		}
	}

	public void start(byte startPageIndex) {
		m_startAddress = (startPageIndex << 8) & 0xFFFF;
		m_transfertInProgress = true;
		m_elpasedTicks = 0;
	}

	public byte getStartPageNumber() {
		return BitUtils.toByte(m_startAddress >> 8);
	}
}

