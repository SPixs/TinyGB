package com.s2soft.tinygb;

import java.io.IOException;

public class Main {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public static void main(String[] args) throws IOException {
		GBMemory memory = new GBMemory();
//		Chipset chipset = new Chipset();
		GBCpu cpu = new GBCpu();
		cpu.setMemory(memory);
//		cpu.setChipset(chipset);

//		new Disassembler(memory).disassemble((short)0, (short)0xFF);
		
		cpu.run();
	}
}

