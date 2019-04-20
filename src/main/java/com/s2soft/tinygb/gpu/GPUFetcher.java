package com.s2soft.tinygb.gpu;

import com.s2soft.tinygb.cpu.Instruction;

public class GPUFetcher {

	//   ============================ Constants ==============================
	
	//	 =========================== Attributes ==============================
	
	private GBGPU m_gpu;

//	private GPUFetcherState m_stateReadTileID;
//	private GPUFetcherState m_stateReadFirstBitplane;
//	private GPUFetcherState m_stateReadSecondBitplane;
//	private GPUFetcherState m_stateIdel;

	private int m_tileAddress;

	private enum STATE {
		READ_TILE_ID,
		READ_FIRST_BITPLANE,
		READ_SECOND_BITPLANE,
		IDLE,
		
		READ_SPRITE_TILE_ID,
		READ_SPRITE_FLAGS,
		READ_SPRITE_DATA_1,
		READ_SPRITE_DATA_2,
		PUSH_SPRITE
	}
	
	private STATE m_state;

	private int m_tileIndex;

	private byte m_firstBitplaneData;
	private byte m_secondBitplaneData;

	private byte m_spriteFirstBitplaneData;
	private byte m_spriteSecondBitplaneData;

	private GPUSprite m_scheduledSprite;


	//	 =========================== Constructor =============================
	
	//	 ========================== Access methods ===========================
		
	public GPUFetcher(GBGPU gbgpu) {
		m_gpu = gbgpu;
		resetState();
	}

	public void resetState() {
		m_state = STATE.READ_TILE_ID;
	}

	public void setTileAddress(int tileAddress) {
		if (tileAddress < 0x9800 || tileAddress >= 0xA000) {
			throw new IllegalArgumentException("Tile map start address of fetcher out of bounds : " +  Integer.toHexString(tileAddress));
		}
		m_tileAddress = tileAddress;
	}

	public int getTileAddress() {
		return m_tileAddress;
	}
	
	public void step() {
		switch (m_state) {
			case READ_TILE_ID:
				m_tileIndex = m_gpu.getMemory().getByte(m_tileAddress) & 0x0FF;
				m_state = STATE.READ_FIRST_BITPLANE;
				break;
			case READ_FIRST_BITPLANE:
				m_firstBitplaneData = readBitPlane(0);
				m_state = STATE.READ_SECOND_BITPLANE;
				break;
			case READ_SECOND_BITPLANE:
				m_secondBitplaneData = readBitPlane(1);
				m_state = STATE.IDLE;
				break;
			case IDLE:
				if (m_gpu.getPixelsFifo().canPut()) {
					m_gpu.getPixelsFifo().putPixels(m_firstBitplaneData, m_secondBitplaneData);
					m_tileAddress++;
					resetState();
					step();
				}
				break;
		   case READ_SPRITE_TILE_ID:
			   	m_state = STATE.READ_SPRITE_FLAGS;
			   	break;
		   case READ_SPRITE_FLAGS:
			   	m_state = STATE.READ_SPRITE_DATA_1;
                break;
           case READ_SPRITE_DATA_1:
        	   	m_spriteFirstBitplaneData = readSpriteBitPlane(m_scheduledSprite, 0);
        	   	m_state = STATE.READ_SPRITE_DATA_2;
                break;
           case READ_SPRITE_DATA_2:
        	   	m_spriteSecondBitplaneData = readSpriteBitPlane(m_scheduledSprite, 1);
        	   	m_state = STATE.PUSH_SPRITE;
        	   	break;
           case PUSH_SPRITE:
        	   	m_gpu.getPixelsFifo().overlaySprite(m_scheduledSprite, m_spriteFirstBitplaneData, m_spriteSecondBitplaneData);
        	   	m_scheduledSprite = null;
        	   	m_state = STATE.READ_TILE_ID;
		}
	}

	private byte readBitPlane(int bitPlaneIndex) {
		int lineInTile = (m_gpu.getScanLine() + m_gpu.getScrollY()) % 8;
		int bgTilesAreaIndex = m_gpu.getBGTilesAreaIndex();
		if (bgTilesAreaIndex == 0) {
			int tileOffsetInData = 16 * (byte)(m_tileIndex & 0xFF);
			return m_gpu.getMemory().getByte(0x9000 + tileOffsetInData + 2 * lineInTile + bitPlaneIndex);
		}
		else {
			int tileOffsetInData = 16 * (int)(m_tileIndex & 0x0FF);
			return m_gpu.getMemory().getByte(0x8000 + tileOffsetInData + 2 * lineInTile + bitPlaneIndex);
		}
	}
	
	private byte readSpriteBitPlane(GPUSprite sprite, int bitPlaneIndex) {
		int lineInTile = (m_gpu.getScanLine() - (sprite.getY() - 16)) % 16;
		if (sprite.getYFlip()) {
			lineInTile = sprite.getHeight() - lineInTile - 1;
		}
		int tileOffsetInData = 16 * (sprite.getTileIndex() & 0x0FF);
		return m_gpu.getMemory().getByte(0x8000 + tileOffsetInData + 2 * lineInTile + bitPlaneIndex);
	}

	public boolean hasScheduledSprite() {
		return m_scheduledSprite != null;
	}

	public void scheduleSprite(GPUSprite sprite) {
		m_scheduledSprite = sprite;
		m_state = STATE.READ_SPRITE_TILE_ID;
	}
}