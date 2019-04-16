package com.s2soft.tinygb.audio;

public class NullAudioDevice implements IAudioDevice {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public void start() throws Exception {
	}

	@Override
	public void stop() {
	}

	@Override
	public void putSample(double leftSample, double rightSample) {
	}
}

