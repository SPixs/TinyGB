package com.s2soft.tinygb.gpu;

/**
 * GPU mode 3
 * 
 * @author smametz
 */
public class GPUPhaseReadVRAM extends GPUPhase {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	public GPUPhaseReadVRAM(String name, int number) {
		super(name, number);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	@Override
	protected void enterImpl() {  
//		byte lcdStatus = getGpu().getLCDStatus();
//		getGpu().setLCDStatus((byte) ((lcdStatus & ~0x03) | 0x03));

		// compute base BG tile address for current line
		int tilesBaseAddress = (getGpu().getBGMapIndex() == 0) ? 0x09800 : 0x09C00;
		int currentLine = getGpu().getScanLine();
		int scrollX = getGpu().getScrollX();
		int scrollY = getGpu().getScrollY();
		int tileX = (scrollX / 8) % 32;
		int tileY = ((currentLine + scrollY) / 8) % 32;
		getGpu().getFetcher().setTileAddress(tilesBaseAddress + tileY * 32, tileX, scrollY % 8);
		getGpu().setRenderingWindow(false);
	}

	@Override
	protected void stepImpl(long elapsedClockCount) {
		// Actually, in this phase VRAM access take from 173.5 to 180.5 cycles.
//		if (elapsedClockCount >= 172) {
//			setPhase(GBGPU.PHASE_HBLANK);
//			getGpu().step();
//		}
//		else {
		
		int currentLine = getGpu().getScanLine();
//		System.out.println(((getGpu().getWindowX() & 0xFF) - 7) + " " + (getGpu().getWindowY() & 0xFF) + " " + getGpu().getCurrentX());
		if (getGpu().isWindowEnabled() && !getGpu().isRenderingWindow() && getGpu().getCurrentX() >= (getGpu().getWindowX() - 7) && currentLine >= getGpu().getWindowY()) {
			int windowTilesBaseAddress = (getGpu().getWindowMapIndex() == 0) ? 0x09800 : 0x09C00;
			getGpu().getPixelsFifo().clear();
			int tileX = ((getGpu().getCurrentX() + 7 - getGpu().getWindowX()) / 8);
			int tileY = ((currentLine - getGpu().getWindowY()) / 8);
			getGpu().getFetcher().setTileAddress(windowTilesBaseAddress + tileY * 32, tileX, 0);
			getGpu().setRenderingWindow(true);
		}
		
			getGpu().getFetcher().step();
//		}
	}
}

