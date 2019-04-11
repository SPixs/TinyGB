package com.s2soft.tinygb.apu;

public interface IVolumeEnveloppeVoice {
	
	void setEnvelopeIncrease(boolean increase);
	boolean isEnvelopeIncrease();

	void setInitialEnvelopeVolume(int volume);
	int getInitialEnvelopeVolume();

	void setEnvelopeSweep(int envelopeSweep);
	int getEnvelopeSweep();

}

