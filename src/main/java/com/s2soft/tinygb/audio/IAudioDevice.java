package com.s2soft.tinygb.audio;

public interface IAudioDevice {

	void start() throws Exception;
	void stop();
	void putSample(double leftChannel, double rightChannel);
	
}

