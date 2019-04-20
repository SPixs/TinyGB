package com.s2soft.tinygb.gpu;

import com.s2soft.utils.BitUtils;

/**
 * GPU mode 1
 *
 * @author smametz
 */
public class GPUPhaseVBlank extends GPUPhase {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	public GPUPhaseVBlank(String name, int number) {
		super(name, number);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	@Override
	protected void enterImpl() {
//		byte lcdStatus = getGpu().getLCDStatus();
//		getGpu().setLCDStatus((byte) ((lcdStatus & ~0x03) | 0x01));
		getGpu().getMemory().requestInterrupt(0); // VBlank interrupt request

		// Rise a LCD status interrupt if configured to do so
		if (BitUtils.isSet(getGpu().getLCDStatus(), 4)) {
			getGpu().getMemory().requestInterrupt(1);
		}
		
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
			getGpu().step();
		}
	}
}

