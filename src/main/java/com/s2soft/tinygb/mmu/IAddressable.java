package com.s2soft.tinygb.mmu;

public interface IAddressable {

	public void setByte(int address, byte b);
	public void setByte(int address, byte b, boolean fromCPU);
	
	public byte getByte(int address);
	public byte getByte(int address, boolean fromCPU);
}

