package com.s2soft.tinygb.cartidge;



public class RomOnlyCartidgeStrategy extends CartidgeStrategy {

	//  ============================ Constants ==============================
	
	//	 =========================== Attributes ==============================
	
	//	 =========================== Constructor =============================
	
	public RomOnlyCartidgeStrategy(Cartidge cartidge) {
		super(cartidge);
	}

	//	 ========================== Access methods ===========================
		
	//	 ========================= Treatment methods =========================
	
	@Override
	public void writeRAM(int address, byte value) {
		System.out.println("Warning : trying to write RAM of a ROM only cartidge");
	}
	
	@Override
	public void writeROM(int address, byte value) {
		System.out.println("Warning : trying to write ROM of a ROM only cartidge");
	}
	
}

