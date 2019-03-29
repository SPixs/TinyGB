package com.s2soft.tinygb.gpu;

import javax.swing.plaf.synth.SynthSpinnerUI;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.utils.BitUtils;

public class GBGpu {

	//   ============================ Constants ==============================
	
	public final static boolean TRACE = false;
	
	public final static GPUPhase PHASE_FETCH_OAM = new GPUPhaseFetchOAM("OAM[mode2]");
	public final static GPUPhase PHASE_READ_VRAM = new GPUPhaseReadVRAM("VRAM[mode3]");
	public final static GPUPhase PHASE_HBLANK = new GPUPhaseHBlank("HBlank[mode0]");
	public final static GPUPhase PHASE_VBLANK = new GPUPhaseVBlank("VBlank[mode1]");

	//	 =========================== Attributes ==============================

	private GameBoy m_gameBoy;

	private byte m_lcdControl;
	private byte m_lcdStatus;

	private GPUPhase m_phase = null;
	
	private int m_scanLine = 0;
	private int m_scrollY = 0;

	//	 =========================== Constructor =============================

	public GBGpu(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
	}

	//	 ========================== Access methods ===========================

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
		System.out.println(value);
		m_scrollY = value;
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
		m_lcdControl = v;
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

	//	 ========================= Treatment methods =========================

	public void enterPhase(GPUPhase phase) {
		if (phase == PHASE_FETCH_OAM) {
			Thread.yield();
		}
		if (TRACE) {
			System.out.println("GPU entering phase : " + phase.getName());
		}
		m_phase = phase;
		m_phase.enter(this);
	}
	
	public void step() {
		m_phase.step();
	}

	public void reset() {
		setScanLine(0);
		enterPhase(PHASE_FETCH_OAM);
	}
}

