package com.s2soft.tinygb.gpu;

import java.util.ArrayList;
import java.util.List;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Disassembler;
import com.s2soft.tinygb.display.IDisplay;
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

	private byte m_bgPalette;
	private byte m_oamPalette2;
	private byte m_oamPalette1;
	
	private GPUSprite[] m_sprites;
	private ArrayList<GPUSprite> m_visibleSprites = new ArrayList<GPUSprite>(10);

	//	 =========================== Constructor =============================

	public GBGPU(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
		m_memory = m_gameBoy.getMemory();
		m_fetcher = new GPUFetcher(this);
		m_pixelsFifo = new GPUFifo(this);
		
		m_sprites = new GPUSprite[40];
		for (int i=0;i<40;i++) {
			m_sprites[i] = new GPUSprite(this, i);
		}
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
//		System.out.println("Set ScrollY="+value);
		m_scrollY = value;
	}

	public int getScrollX() {
		return m_scrollX;
	}

	public void setScrollX(int value) {
//		System.out.println("Set ScrollX="+value);
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
			new Disassembler(m_gameBoy).disassemble((short)m_gameBoy.getCpu().getPC(), (short)m_gameBoy.getCpu().getPC());
		}
		
		byte oldControl = m_lcdControl;
		m_lcdControl = v;
		if (BitUtils.isSet(oldControl, 7) != BitUtils.isSet(m_lcdControl, 7)) {
			if (!BitUtils.isSet(m_lcdControl, 7)) {
				setScanLine(0);
				getDisplay().setEnable(false);
			}
			else {
				enterPhase(PHASE_FETCH_OAM);
				getDisplay().setEnable(true);
			}
		}
	}
	
	public GPUSprite getSprite(int index) {
		return m_sprites[index];
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
		return m_bgPalette;
	}

	public void setBGPaletteData(byte v) {
		m_bgPalette = v;
	}
	
	public byte getOMAPalette1Data() {
		return m_oamPalette1;
	}

	public void setOMAPalette1Data(byte v) {
		m_oamPalette2 = v;
	}

	public void setOMAPalette2Data(byte v) {
		m_oamPalette2 = v;
	}

	public byte getOMAPalette2Data() {
		return m_oamPalette2;
	}

	public byte getLCDStatus() {
		return (byte) (m_lcdStatus | 0b10000000);
	}

	public void setLCDStatus(byte v) {
		m_lcdStatus = v;
	}

	public long getClockCount() {
		return m_gameBoy.getClockCount();
	}
	
	public IDisplay getDisplay() {
		return m_gameBoy.getDisplay();
	}
	
	public void setLCDEnable(boolean set) {
		boolean oldState = isLCDEnabled();
		if (set != oldState) {
			setLCDControl(BitUtils.setBit(getLCDControl(), 7, set));
		}
	}

	public boolean isLCDEnabled() {
		return BitUtils.isSet(m_lcdControl, 7);
	}
	
	public boolean areSpritesEnabled() {
		return BitUtils.isSet(getLCDControl(), 1);
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
	int pixelsTrashed = 0;

	public void step() {
		if (!isLCDEnabled()) { 
			return; 
		} 

		long elapsedClockCountInPhase = m_phase.getElapsedClockCountInPhase();
		
		if (areSpritesEnabled() && m_pixelsFifo.canOverlay()) {
			GPUSprite visibleSprite = null;
			for (int i=0;i<m_visibleSprites.size();i++) {
				GPUSprite sprite = m_visibleSprites.get(i);
				if (!m_fetcher.hasScheduledSprite() && sprite.getX() - 8 == pixelsCount) { // TODO : handle offscreen sprites with partial visibility
					m_fetcher.scheduleSprite(sprite);
					visibleSprite = sprite;
				}
			}
			m_visibleSprites.remove(visibleSprite);
		}
		
		m_pixelsFifo.setEnabled(!m_fetcher.hasScheduledSprite());
		
		// Fifo is running at machine clock speed (4.194304Mhz)
		if (m_pixelsFifo.isEnabled() && m_pixelsFifo.canPull()) {
//			System.out.println("Elapsed clock in ReadVRAM : " + m_phase.getElapsedClockCountInPhase());
			byte pixel = m_pixelsFifo.pullPixel();
			if (pixelsTrashed < getScrollX()) {
				pixelsTrashed++;
			}
			else {
				getDisplay().putPixel(pixel);
				pixelsCount++;
			}
		}

		// Each access to VRAM (B, 0, 1, s) takes 2 cycles to occur.  A "cycle" is
		// exactly 1 period of the main input clock to the gameboy CPU chip.  This
		// is nominally 4.19MHz approximately.
		// Skip odd clock to run the GPU at 2.097152Mhz
//		if (((m_gameBoy.getClockCount() - m_phase.getEnterClock()) % 2) == 0) {
		if (elapsedClockCountInPhase % 2 == 0) {
			if (pixelsCount > 0 && pixelsCount % 160 == 0) {
				if (TRACE & elapsedClockCountInPhase != 172) {
					System.out.println("Elapsed clock in ReadVRAM : " + elapsedClockCountInPhase);
				}
				pixelsCount = 0;
				pixelsTrashed = 0;
				enterPhase(PHASE_HBLANK);
			}
			m_phase.step();
		}
	}

	/**
	 * Add up to 10 sprites to the list of visible sprites on current raster line
	 * @param sprite
	 */
	public void addVisibleSprite(GPUSprite sprite) {
//		System.out.println("Adding visible sprite " + sprite.getIndex());
		if (m_visibleSprites.size() < 10) {
			m_visibleSprites.add(sprite);
		}
	}

	public void clearVisibleSprites() {
		m_visibleSprites.clear();
	}

	public void reset() {
		m_pixelsFifo.setEnabled(true);
		pixelsCount = 0;
		setScanLine(0);
		enterPhase(PHASE_FETCH_OAM);
	}

}

