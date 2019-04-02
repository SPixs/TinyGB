package com.s2soft.tinygb.gpu;

/**
 * GPU mode 2
 *
 * @author smametz
 */
public class GPUPhaseFetchOAM extends GPUPhase {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	public GPUPhaseFetchOAM(String name) {
		super(name);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	protected void enterImpl() {
		getGpu().setLineStartClock(getEnterClock());
		getGpu().getPixelsFifo().clear();
		getGpu().getFetcher().reset();
	}

	@Override
	protected void stepImpl(long elapsedClockCount) {
		if (elapsedClockCount >= 80) {
			setPhase(GBGPU.PHASE_READ_VRAM);
			getGpu().step();
		}
	}
}

