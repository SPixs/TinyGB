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
	}
	
	private STATE m_state;

	private int m_tileIndex;

	private byte m_firstBitplaneData;
	private byte m_secondBitplaneData;


	//	 =========================== Constructor =============================
	
	//	 ========================== Access methods ===========================
		
	public GPUFetcher(GBGPU gbgpu) {
		m_gpu = gbgpu;
		reset();
	}

	public void reset() {
		m_state = STATE.READ_TILE_ID;
	}

	public void setTileAddress(int tileAddress) {
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
					reset();
					step();
				}
				break;
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
}