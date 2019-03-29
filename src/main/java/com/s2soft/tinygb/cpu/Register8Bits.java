package com.s2soft.tinygb.cpu;

public enum Register8Bits {
	
	A {
		byte getValue(GBCpu cpu) { return cpu.getA(); }
		void setValue(GBCpu cpu, byte value) { cpu.setA(value); }
	},
	B {
		byte getValue(GBCpu cpu) { return cpu.getB(); }
		void setValue(GBCpu cpu, byte value) { cpu.setB(value); }
	},
	C {
		byte getValue(GBCpu cpu) { return cpu.getC(); }
		void setValue(GBCpu cpu, byte value) { cpu.setC(value); }
	},
	D {
		byte getValue(GBCpu cpu) { return cpu.getD(); }
		void setValue(GBCpu cpu, byte value) { cpu.setD(value); }
	},
	E {
		byte getValue(GBCpu cpu) { return cpu.getE(); }
		void setValue(GBCpu cpu, byte value) { cpu.setE(value); }
	},
	F {
		byte getValue(GBCpu cpu) { return cpu.getF(); }
		void setValue(GBCpu cpu, byte value) { cpu.setF(value); }
	},
	L {
		byte getValue(GBCpu cpu) { return cpu.getL(); }
		void setValue(GBCpu cpu, byte value) { cpu.setL(value); }
	},
	H {
		byte getValue(GBCpu cpu) { return cpu.getH(); }
		void setValue(GBCpu cpu, byte value) { cpu.setH(value); }
	};
	
	abstract byte getValue(GBCpu cpu);
	abstract void setValue(GBCpu cpu, byte value);

}

