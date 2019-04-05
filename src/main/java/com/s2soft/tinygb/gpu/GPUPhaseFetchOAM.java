package com.s2soft.tinygb.gpu;

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

	public GPUPhaseFetchOAM(String name) {
		super(name);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	protected void enterImpl() {
		getGpu().setLineStartClock(getEnterClock());
		getGpu().clearVisibleSprites();
		getGpu().getPixelsFifo().clear();
		getGpu().getFetcher().resetState();
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

