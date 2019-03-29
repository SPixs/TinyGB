package com.s2soft.tinygb.mmu;

public interface IAddressable {

	public void setByte(int address, byte b);
	
	public byte getByte(int address);
}

