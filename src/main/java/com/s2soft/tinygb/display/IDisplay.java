package com.s2soft.tinygb.display;

public interface IDisplay {

	public void setEnable(boolean enabled);
	
	/**
	 * Put a pixel in display buffer. Pixel is encoded in a byte with the two
	 * least significant bits.
	 * 
	 * 0b00000000 white
	 * 0b00000001 light
	 * 0b00000010 dark
	 * 0b00000011 black
	 * 
	 * @param pixel
	 */
	public void putPixel(byte pixel);
	
	public void refresh();
	
}

