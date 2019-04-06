package com.s2soft.tinygb.gpu;

import java.util.Arrays;

import com.s2soft.utils.BitUtils;

public class GPUFifo {

	//   ============================ Constants ==============================
	
	//	 =========================== Attributes ==============================
	
	private byte[] m_queue = new byte[16];
	
	private int m_usedSpace = 0;

	private GBGPU m_gpu;

	private boolean m_enabled;
	
	//	 =========================== Constructor =============================
	
	public GPUFifo(GBGPU gbgpu) {
		m_gpu = gbgpu;
	}

	//	 ========================== Access methods ===========================
		
	//	 ========================= Treatment methods =========================

	public int getFreeSpace() {
		return 16 - m_usedSpace;
	}
	
	public int getUsedSpace() {
		return m_usedSpace;
	}
	
	public byte pullPixel() {
		if (!canPull()) {
			throw new IllegalStateException("Cannot pull pixel fifo. Size must be > 8 : " + m_usedSpace);
		}
		byte pixelValue = m_queue[0];
		System.arraycopy(m_queue, 1, m_queue, 0, 15);
		m_usedSpace--;
		
		byte bgPalette = m_gpu.getBGPaletteData(); // fix me multi palette must be handled
		byte coloredPixel = (byte) (bgPalette >> (2 * pixelValue) & 0x3);
		
		return coloredPixel;
	}
	
	/**
	 * @param data1 8 bits for 8 pixels first bitplane
	 * @param data2 8 bits for 8 pixels second bitplane
	 */
	public void putPixels(byte data1, byte data2) {
		if (!canPut()) {
			throw new IllegalStateException("Cannot put pixel fifo. Size must be <= 8 : " + m_usedSpace);
		}
		for (int i=7;i>=0;i--) {
			byte pixelValue = (byte) (BitUtils.isSet(data1, i) ? 0b01 : 0b00) ;
			pixelValue |= BitUtils.isSet(data2, i) ? 0b10 : 0b00;
			m_queue[m_usedSpace++] = pixelValue;
		}
	}

	public boolean canPull() {
		return m_usedSpace > 8;
	}

	public boolean canPut() {
		return m_usedSpace <= 8;
	}

	public boolean canOverlay() {
		return getUsedSpace() >= 8;
	}

	public void clear() {
		Arrays.fill(m_queue, (byte)0); // Not necessary
		m_usedSpace = 0;
	}

	public void setEnabled(boolean b) {
		m_enabled = b;
	}
	
	public boolean isEnabled() {
		return m_enabled;
	}

	public void overlaySprite(GPUSprite scheduledSprite, byte spriteFirstBitplaneData, byte spriteSecondBitplaneData) {
		if (m_usedSpace < 8) {
			throw new IllegalStateException("Cannot overlay sprite with less than 8 pixels in pixel fifo");
		}
		for (int i=7;i>=0;i--) {
//			byte bgPixel = m_queue[i];
			int index = scheduledSprite.getXFlip() ? (7-i) : i;
			byte spritePixel = (byte) (BitUtils.isSet(spriteFirstBitplaneData, index) ? 0b01 : 0b00) ;
			spritePixel |= BitUtils.isSet(spriteSecondBitplaneData, index) ? 0b10 : 0b00;
			if (spritePixel != 0) {
				m_queue[7-i] = spritePixel;
			}
		}
	}
}