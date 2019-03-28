package com.s2soft.tinygb;

public interface IAddressingMode {
	
	public byte readByte(GBCpu cpu, byte[] additionalBytes);
	public void setByte(GBCpu cpu, byte value, byte[] additionnalBytes);
	
	public String asText(byte[] bs);

}

