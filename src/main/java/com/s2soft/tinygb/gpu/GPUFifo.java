package com.s2soft.tinygb.gpu;

import java.util.Arrays;

import com.s2soft.utils.BitUtils;

public class GPUFifo {

	//   ============================ Constants ==============================
	
	//	 =========================== Attributes ==============================
	
	protected class FifoEntry {
		
		protected int m_pixelColorIndex;
		protected int m_palette;
		protected boolean m_isBackground;
		protected boolean m_processed;
		
	}
	
	private FifoEntry[] m_queue = new FifoEntry[16];
//	private byte[] m_bgQueue = new byte[16];
//	private boolean[] m_isSpr
//	
//	private byte[] m_spritesQueue = new byte[16];
//	private byte[] m_spritePalette = new byte[16];
//	private byte[] m_queue = new byte[16];
	
	private int m_usedSpace = 0;

	private GBGPU m_gpu;

	private boolean m_enabled;
	
	//	 =========================== Constructor =============================
	
	public GPUFifo(GBGPU gbgpu) {
		m_gpu = gbgpu;
		for (int i=0;i<m_queue.length;i++) {
			m_queue[i] = new FifoEntry();
		}
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
		
		FifoEntry entry = m_queue[0];
		System.arraycopy(m_queue, 1, m_queue, 0, 15);
		m_queue[15] = entry;

		byte coloredPixel = (byte) (entry.m_palette >> (2 * entry.m_pixelColorIndex) & 0x3);
		
//		byte bgPixelValue = m_bgQueue[0];
//		System.arraycopy(m_bgQueue, 1, m_bgQueue, 0, 15);
//		
//		byte bgPalette = m_gpu.getBGPaletteData(); // fix me multi palette must be handled
//		byte coloredPixel = (byte) (bgPalette >> (2 * bgPixelValue) & 0x3);
//		
//		FifoSpriteEntry entry = m_spritesQueue[0];
//		System.arraycopy(m_spritesQueue, 1, m_spritesQueue, 0, 15);
//		m_spritesQueue[15] = entry;
//		int spritePixelValue = entry.getPixelColorIndex();
//		
//		if (spritePixelValue != 0) {
//			byte spritePalette = entry.getPaletteIndex() == 1 ? m_gpu.getOMAPalette2Data() : m_gpu.getOMAPalette1Data(); 
//			// Sprite above BG ?
//			if (entry.getPriority() == 0 || bgPixelValue == 0) {
//				coloredPixel = (byte) (spritePalette >> (2 * spritePixelValue) & 0x3);
//			}
//		}
		m_usedSpace--;

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
			m_queue[m_usedSpace].m_pixelColorIndex = pixelValue;
			m_queue[m_usedSpace].m_isBackground = true;
			m_queue[m_usedSpace].m_palette = m_gpu.getBGPaletteData();
			m_queue[m_usedSpace].m_processed = false;
			m_usedSpace++;
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
//		Arrays.fill(m_bgQueue, (byte)0); // Not necessary
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
			int index = scheduledSprite.getXFlip() ? (7-i) : i;
			byte spritePixel = (byte) (BitUtils.isSet(spriteFirstBitplaneData, index) ? 0b01 : 0b00) ;
			spritePixel |= BitUtils.isSet(spriteSecondBitplaneData, index) ? 0b10 : 0b00;
			FifoEntry entry = m_queue[7-i];
			
			if (entry.m_isBackground && !entry.m_processed) {
				boolean writeSprite = (!scheduledSprite.getPriority() && spritePixel != 0) || (scheduledSprite.getPriority() && entry.m_pixelColorIndex == 0);
				if (writeSprite) {
					entry.m_isBackground = false;
					entry.m_palette = scheduledSprite.getPalette() ? m_gpu.getOMAPalette2Data() : m_gpu.getOMAPalette1Data();
					entry.m_pixelColorIndex = spritePixel;
					entry.m_processed = true;
				}
			}
			
			// TODO : check this rule :
			// The priority calculation between sprites disregards OBJ-to-BG Priority (attribute bit 7).
			// Only the highest-priority nonzero sprite pixel at any given point is compared against the background. 
			// Thus if a sprite with a higher priority (based on OAM index) but with OBJ-to-BG Priority turned on overlaps a sprite 
			// with a lower priority and a nonzero background pixel, the background pixel is displayed regardless of the lower-priority sprite's OBJ-to-BG Priority.
		}
	}
}