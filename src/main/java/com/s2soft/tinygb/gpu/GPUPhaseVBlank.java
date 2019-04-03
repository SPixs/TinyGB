package com.s2soft.tinygb.gpu;

/**
 * GPU mode 1
 *
 * @author smametz
 */
public class GPUPhaseVBlank extends GPUPhase {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	public GPUPhaseVBlank(String name) {
		super(name);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	@Override
	protected void enterImpl() {
		getGpu().getMemory().requestInterrupt(0); // VBlank interrupt request
		getGpu().getDisplay().refresh();
	}
	
	@Override
	protected void stepImpl(long elapsedClockCount) {
		if (elapsedClockCount > 0 && (elapsedClockCount % 456) == 0) {
			final int scanLine = getGpu().getScanLine();
			getGpu().setScanLine(scanLine+1);
		}
		if (elapsedClockCount >= 4560) {
			getGpu().setScanLine(0);
			setPhase(GBGPU.PHASE_FETCH_OAM);
		}
	}
}

