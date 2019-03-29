package com.s2soft.tinygb.gpu;

/**
 * GPU mode 0
 *
 * @author smametz
 */
public class GPUPhaseHBlank extends GPUPhase {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	public GPUPhaseHBlank(String name) {
		super(name);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	@Override
	protected void enterImpl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void stepImpl(long elapsedClockCount) {
		if (elapsedClockCount >= 204) {
			final int scanLine = getGpu().getScanLine();
			if (scanLine == 143) {
				setPhase(GBGpu.PHASE_VBLANK);
				getGpu().step();
			}
			else {
				getGpu().setScanLine(scanLine+1);
				setPhase(GBGpu.PHASE_FETCH_OAM);
			}
		}
	}
}

