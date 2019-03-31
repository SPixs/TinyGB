package com.s2soft.tinygb.gpu;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class GBGPU {

	//   ============================ Constants ==============================
	
	public final static boolean TRACE = false;
	
	public final static GPUPhase PHASE_FETCH_OAM = new GPUPhaseFetchOAM("OAM[mode2]");
	public final static GPUPhase PHASE_READ_VRAM = new GPUPhaseReadVRAM("VRAM[mode3]");
	public final static GPUPhase PHASE_HBLANK = new GPUPhaseHBlank("HBlank[mode0]");
	public final static GPUPhase PHASE_VBLANK = new GPUPhaseVBlank("VBlank[mode1]");

	//	 =========================== Attributes ==============================

	private GameBoy m_gameBoy;
	private GBMemory m_memory;
	
	private byte m_lcdControl;
	private byte m_lcdStatus;

	private GPUPhase m_phase = null;
	
	private int m_scanLine = 0;
	private int m_scrollX = 0;
	private int m_scrollY = 0;

	private long m_lineStartClock;

	private GPUFetcher m_fetcher;
	private GPUFifo m_pixelsFifo;

	//	 =========================== Constructor =============================

	public GBGPU(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
		m_memory = m_gameBoy.getMemory();
		m_fetcher = new GPUFetcher(this);
		m_pixelsFifo = new GPUFifo();
	}

	//	 ========================== Access methods ===========================

	public GBMemory getMemory() {
		return m_memory;
	}

	public void setScanLine(int scanLine) {
		if (TRACE) {
			System.out.println("GPU new line : " + scanLine);
		}
		m_scanLine = scanLine;
	}

	public int getScanLine() {
		return m_scanLine;
	}

	public int getScrollY() {
		return m_scrollY;
	}

	public void setScrollY(int value) {
		System.out.println("Set ScrollY="+value);
		m_scrollY = value;
	}

	public int getScrollX() {
		return m_scrollX;
	}

	public void setScrollX(int value) {
		System.out.println("Set ScrollX="+value);
		m_scrollX = value;
	}

	public void setLCDControl(byte v) {
		if (TRACE) {
			System.out.println("Setting LCD Control : " +
					"BG&Win["+(BitUtils.isSet(v, 0) ? "ON" : "OFF")+"], " +
					"Sprites["+(BitUtils.isSet(v, 1) ? "ON" : "OFF")+"], " +
					"SpritesSize["+(BitUtils.isSet(v, 2) ? "8*16" : "8*8")+"], " +
					"BGTileMap["+(BitUtils.isSet(v, 3) ? "$9C00-$9FFF" : "$9800-$9BFF")+"], " +
					"BG&WinTilesData["+(BitUtils.isSet(v, 4) ? "$8000-$8FFF" : "$8800-$97FF")+"], " +
					"Window["+(BitUtils.isSet(v, 5) ? "ON" : "OFF")+"], " +
					"WindowTileMap["+(BitUtils.isSet(v, 6) ? "$9C00-$9FFF" : "$9800-$9BFF")+"], " +
					"LCD["+(BitUtils.isSet(v, 7) ? "ON" : "OFF")+"]");
		}
		
		byte oldControl = m_lcdControl;
		m_lcdControl = v;
		if (BitUtils.isSet(oldControl, 7) != BitUtils.isSet(v, 7)) {
			setLCDEnable(BitUtils.isSet(v, 7));
		}
	}

	/**
     * 
	 * @return 0 if BGTileMap is at $9800-$9BFF, 1 if BGTileMap is at $9C00-$9FFF
	 */
	public int getBGMapIndex() {
		return BitUtils.isSet(getLCDControl(), 3) ? 1 : 0;
	}
	
	/**
     * 
	 * @return 0 if BGTilesData is at $8800-$97FF, 1 if BGTilesData is at $8000-$8FFF
	 */
	public int getBGTilesAreaIndex() {
		return BitUtils.isSet(getLCDControl(), 4) ? 1 : 0;
	}
	
	public byte getLCDControl() {
		return m_lcdControl;
	}

	public byte getBGPaletteData() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setBGPaletteData(byte v) {
		// TODO Auto-generated method stub
		
	}
	
	public byte getLCDStatus() {
		return m_lcdStatus;
	}

	public void setLCDStatus(byte v) {
		m_lcdStatus = v;
	}

	public long getClockCount() {
		return m_gameBoy.getClockCount();
	}
	
	public void setLCDEnable(boolean set) {
		boolean oldState = isLCDEnabled();
		setLCDStatus(BitUtils.setBit(getLCDStatus(), 7));
		if (!set && (set != oldState)) {
			setScanLine(0);
		}
		else if (set && (set != oldState)) {
			enterPhase(PHASE_FETCH_OAM);
		}
	}

	public boolean isLCDEnabled() {
		return BitUtils.isSet(m_lcdStatus, 7);
	}
	
	public void setLineStartClock(long enterClock) {
		m_lineStartClock = enterClock;
	}
	
	public long getLineStartClock() {
		return m_lineStartClock;
	}

	public GPUFifo getPixelsFifo() {
		return m_pixelsFifo;
	}

	public GPUFetcher getFetcher() {
		return m_fetcher;
	}

	//	 ========================= Treatment methods =========================

	public void enterPhase(GPUPhase phase) {
		if (TRACE) {
			System.out.println("GPU entering phase : " + phase.getName());
		}
		m_phase = phase;
		m_phase.enter(this);
	}
	
	int pixelsCount = 0;
	
	public void step() {
		if (!isLCDEnabled()) { return; } 

		long elapsedClockCountInPhase = m_phase.getElapsedClockCountInPhase();
		
		// Fifo is running at machine clock speed (4.194304Mhz)
		if (m_pixelsFifo.canPull()) {
//			System.out.println("Elapsed clock in ReadVRAM : " + m_phase.getElapsedClockCountInPhase());
			byte pixel = m_pixelsFifo.pullPixel();
//			if (pixel != 0) {
//				System.out.println("Can read pixel " + pixel);
//			}
			pixelsCount++;
		}

		// Each access to VRAM (B, 0, 1, s) takes 2 cycles to occur.  A "cycle" is
		// exactly 1 period of the main input clock to the gameboy CPU chip.  This
		// is nominally 4.19MHz approximately.
		// Skip odd clock to run the GPU at 2.097152Mhz
//		if (((m_gameBoy.getClockCount() - m_phase.getEnterClock()) % 2) == 0) {
		if (elapsedClockCountInPhase % 2 == 0) {
			if (pixelsCount > 0 && pixelsCount % 160 == 0) {
				if (elapsedClockCountInPhase != 172) {
					System.out.println("Elapsed clock in ReadVRAM : " + elapsedClockCountInPhase);
				}
				pixelsCount = 0;
				enterPhase(PHASE_HBLANK);
			}
			m_phase.step();
		}
		
	}

	public void reset() {
		setScanLine(0);
		enterPhase(PHASE_FETCH_OAM);
	}
}

