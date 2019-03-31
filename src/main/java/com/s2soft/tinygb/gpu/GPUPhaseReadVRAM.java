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

	public GPUPhaseReadVRAM(String name) {
		super(name);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	@Override
	protected void enterImpl() {
		// compute base BG tile address for current line
		int tilesBaseAddress = (getGpu().getBGMapIndex() == 0) ? 0x09C00 : 0x09800;
		int currentLine = getGpu().getScanLine();
		int scrollX = getGpu().getScrollX();
		int scrollY = getGpu().getScrollY();
		int tileX = scrollX / 8;
		int tileY = (currentLine + scrollY) / 8;
		getGpu().getFetcher().setTileAddress(tilesBaseAddress + tileX + tileY * 32);
	}

	@Override
	protected void stepImpl(long elapsedClockCount) {
		// Actually, in this phase VRAM access take from 173.5 to 180.5 cycles.
		if (elapsedClockCount >= 172) {
			setPhase(GBGPU.PHASE_HBLANK);
			getGpu().step();
		}
		else {
			getGpu().getFetcher().step();
		}
	}
}

