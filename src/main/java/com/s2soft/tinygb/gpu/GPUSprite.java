package com.s2soft.tinygb.gpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class GPUSprite {

	//   ============================ Constants ==============================

	private final static int OAMBaseAddress = 0xFE00;
	
	//	 =========================== Attributes ==============================

	private GBGPU m_gpu;
	private GBMemory m_memory;

	private int m_index;

	private int m_x;
	private int m_y;

	private int m_tileIndex;

	private boolean m_priority;
	private boolean m_yFlip;
	private boolean m_xFlip;
	private boolean m_palette;

	private int m_height; // either 8 or 16 pixels

	//	 =========================== Constructor =============================

	public GPUSprite(GBGPU gpu, int index) {
		m_gpu = gpu;
		m_memory = gpu.getMemory();
		m_index = index;
	}

	//	 ========================== Access methods ===========================
	
	public int getIndex() {
		return m_index;
	}
	
	public int getOAMAddress() {
		return OAMBaseAddress + 4 * m_index;
	}
	
	public int getX() {
		return m_x;
	}

	public int getY() {
		return m_y;
	}
	
	public int getTileIndex() {
		return m_tileIndex;
	}

	/**
	 * @return false if above background, true if below background (except colour 0)
	 */
	public boolean getPriority() {
		return m_priority;
	}
	
	/**
	 * @return true if vertically flipped, else false
	 */
	public boolean getYFlip() {
		return m_yFlip;
	}
	
	/**
	 * @return true if horizontally flipped, else false
	 */
	public boolean getXFlip() {
		return m_xFlip;
	}
	
	/**
	 * @return false if sprite uses palette 0, true if uses palette 1
	 */
	public boolean getPalette() {
		return m_palette;
	}

	//	 ========================= Treatment methods =========================
	
	public void update() {
		m_y = m_memory.getByte(getOAMAddress()) & 0xFF;
		m_x = m_memory.getByte(getOAMAddress() + 1) & 0xFF;
		m_tileIndex = m_memory.getByte(getOAMAddress() + 2) & 0xFF;
		byte options = m_memory.getByte(getOAMAddress() + 3);
		m_priority = BitUtils.isSet(options, 7);
		m_yFlip = BitUtils.isSet(options, 6);
		m_xFlip = BitUtils.isSet(options, 5);
		m_palette = BitUtils.isSet(options, 4);
		m_height = BitUtils.isSet(m_gpu.getLCDControl(), 2) ? 16 : 8;
	}

	public boolean isVisible(int scanLine) {
		return ((getY() - 16) <= scanLine) && ((getY() - 16 + m_height) > scanLine);
	}
}

