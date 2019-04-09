package com.s2soft.tinygb.audio;

public interface IAudioDevice {

	void start() throws Exception;
	void putSample(byte leftSample, byte rightSample);
	
}

