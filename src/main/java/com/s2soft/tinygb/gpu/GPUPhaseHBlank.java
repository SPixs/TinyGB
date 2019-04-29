package com.s2soft.tinygb.gpu;

import java.util.HashMap;
import java.util.Map;

import com.s2soft.utils.BitUtils;

/**
 * GPU mode 0
 *
 * @author smametz
 */
public class GPUPhaseHBlank extends GPUPhase {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	public long m_elapsedClockCount;

	public GPUPhaseHBlank(String name, int number) {
		super(name, number);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	@Override
	protected void enterImpl() {
//		byte lcdStatus = getGpu().getLCDStatus();
//		getGpu().setLCDStatus((byte) ((lcdStatus & ~0x03) | 0x00));
		
		// Rise a LCD status interrupt if configured to do so
		if (BitUtils.isSet(getGpu().getLCDStatus(), 3)) {
			getGpu().getMemory().requestInterrupt(1);
		}
	}

//	Map<Integer, Integer> cycles = new HashMap<Integer, Integer>();
	
	@Override
	protected void stepImpl(long elapsedClockCount) {
		m_elapsedClockCount = elapsedClockCount;
//		System.out.println("Elapsed clocks in HBlank " + elapsedClockCount);
		if (getGpu().getClockCount() - getGpu().getLineStartClock() >= 456) {
			
//			cycles.put(getGpu().getScrollX(), (int) elapsedClockCount/4);
//			System.out.println(cycles);
			
//		if (elapsedClockCount >= 204) {
//			System.out.println(getGpu().getClockCount()-getGpu().getLineStartClock());
			final int scanLine = getGpu().getScanLine();
			if (scanLine == 143) {
//				System.out.println(elapsedClockCount);
				getGpu().setScanLine(scanLine+1);
				setPhase(GBGPU.PHASE_VBLANK);
				GBGPU.PHASE_VBLANK.step();
			}
			else {
				getGpu().setScanLine(scanLine+1);
				setPhase(GBGPU.PHASE_FETCH_OAM);
				GBGPU.PHASE_FETCH_OAM.step();
			}
		}
	}
}

