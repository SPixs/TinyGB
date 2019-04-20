package com.s2soft.tinygb.gpu;

import com.s2soft.utils.BitUtils;

/**
 * GPU mode 2
 *
 * @author smametz
 */
public class GPUPhaseFetchOAM extends GPUPhase {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private final static int m_oamBaseAddress = 0xFE00;

	//	 =========================== Constructor =============================

	public GPUPhaseFetchOAM(String name, int number) {
		super(name, number);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	protected void enterImpl() {
		byte lcdStatus = getGpu().getLCDStatus();
//		getGpu().setLCDStatus((byte) ((lcdStatus & ~0x03) | 0x02));
		getGpu().setLineStartClock(getEnterClock());
		getGpu().clearVisibleSprites();
		getGpu().getPixelsFifo().clear();
		getGpu().getFetcher().resetState();
		
		// Rise a LCD status interrupt if configured to do so
		if (BitUtils.isSet(getGpu().getLCDStatus(), 5)) {
			getGpu().getMemory().requestInterrupt(1);
		}
	}

	@Override
	protected void stepImpl(long elapsedClockCount) {
		if (elapsedClockCount >= 80) {
			setPhase(GBGPU.PHASE_READ_VRAM);
			getGpu().step();
		}
		else {
			if (getGpu().areSpritesEnabled() && elapsedClockCount % 2 == 0) {
				// update sprite attributes from OAM
				int sprintIndex = (int) (elapsedClockCount / 2);
				final GPUSprite sprite = getGpu().getSprite(sprintIndex);
				sprite.update();
				if (sprite.isVisible(getGpu().getScanLine())) {
					getGpu().addVisibleSprite(sprite);
				}
			}
		}
	}
}

