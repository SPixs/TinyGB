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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void stepImpl(long elapsedClockCount) {
		if (elapsedClockCount >= 172) {
			setPhase(GBGpu.PHASE_HBLANK);
			getGpu().step();
		}
	}
}

