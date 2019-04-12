package com.s2soft.tinygb.cpu;

public enum Register16Bits {
	
	AF(Register8Bits.A, Register8Bits.F),
	BC(Register8Bits.B, Register8Bits.C),
	DE(Register8Bits.D, Register8Bits.E),
	HL(Register8Bits.H, Register8Bits.L),
	SP(null, null) {
		int getValue(GBCpu cpu) { return cpu.getSp(); }
		void setValue(GBCpu cpu, int value) { cpu.setSp(value); }
	},
	PC(null, null) {
		int getValue(GBCpu cpu) { return cpu.getPC(); }
		void setValue(GBCpu cpu, int value) { cpu.setPC(value); }
	};
	
	private Register8Bits m_high;
	private Register8Bits m_low;

	Register16Bits(Register8Bits high, Register8Bits low) {
		m_high = high;
		m_low = low;
	}
	
	int getValue(GBCpu cpu) {
		return (short)(m_high.getValue(cpu) << 8 | (m_low.getValue(cpu) & 0x00FF));
	}
	
	void setValue(GBCpu cpu, int value) {
		m_low.setValue(cpu, (byte)(value & 0x0FF));
		m_high.setValue(cpu, (byte)((value >> 8) & 0x0FF));
	}
}

