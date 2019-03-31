package com.s2soft.tinygb.gpu;

import java.util.Arrays;

import com.s2soft.utils.BitUtils;

public class GPUFifo {

	//   ============================ Constants ==============================
	
	//	 =========================== Attributes ==============================
	
	private byte[] m_queue = new byte[16];
	
	private int m_usedSpace = 0;
	
	//	 =========================== Constructor =============================
	
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
		System.arraycopy(m_queue, 1, m_queue, 0, 7);
		m_usedSpace--;
		return pixelValue;
	}
	
	/**
	 * @param data1 8 bits for 8 pixels first bitplane
	 * @param data2 8 bits for 8 pixels second bitplane
	 */
	public void putPixels(byte data1, byte data2) {
		if (!canPut()) {
			throw new IllegalStateException("Cannot put pixel fifo. Size must be <= 8 : " + m_usedSpace);
		}
		for (int i=0;i<8;i++) {
			byte pixelValue = (byte) (BitUtils.isSet(data2, i) ? 0x10 : 0x00) ;
			pixelValue |= BitUtils.isSet(data1, i) ? 0x01 : 0x0;
			m_queue[m_usedSpace++] = pixelValue;
		}
	}

	public boolean canPull() {
		return m_usedSpace > 8;
	}

	public boolean canPut() {
		return m_usedSpace <= 8;
	}

	public void clear() {
		Arrays.fill(m_queue, (byte)0); // Not necessary
		m_usedSpace = 0;
	}
}