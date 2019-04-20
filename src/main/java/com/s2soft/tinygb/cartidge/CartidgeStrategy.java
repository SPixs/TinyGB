package com.s2soft.tinygb.cartidge;


public abstract class CartidgeStrategy {
	
	//  ============================ Constants ==============================
	
	//	 =========================== Attributes ==============================

	private Cartidge m_cartidge;

	//	 =========================== Constructor =============================
	
	public CartidgeStrategy(Cartidge cartidge) {
		m_cartidge = cartidge ;
	}

	//	 ========================== Access methods ===========================
		
	public Cartidge getCartidge() {
		return m_cartidge;
	}

	//	 ========================= Treatment methods =========================

	public void writeROM(int address, byte value) {
		if (address <= 0x1FFF) {
			System.out.println("Cartidge RAM enabled : " + ((value == (byte)0x0A) ? true : false ));
			return;
		}
		if (address >= 0x2000 && address <= 0x3FFF) {
			System.out.println("Writing ROM bank number : " + value);
			return;
		}
		if (address >= 0x4000 && address <= 0x5FFF) {
			System.out.println("Writing RAM bank number or Upper bits of ROM bank number : " + value);
			return;
		}
		if (address >= 0x6000 && address <= 0x7FFF) {
			System.out.println("Writing ROM/RAM Mode select : " + value);
			return;
		}
		throw new IllegalStateException("Cannot write to ROM");
	}

	public byte readROM(int address) {
		return m_cartidge.getROM()[address & 0x7FFF];
	}

	public void writeRAM(int address, byte value) {
		m_cartidge.getRAM()[address] = value;
	}

	public byte readRAM(int address) {
		return m_cartidge.getRAM()[address];
	}
	
	protected byte[] getRAM() {
		return m_cartidge.getRAM();
	}
	
	protected byte[] getROM() {
		return m_cartidge.getROM();
	}
}

