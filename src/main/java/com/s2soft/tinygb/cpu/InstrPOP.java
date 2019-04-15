package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrPOP extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private Register16AddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0xF1: return Register16AddressingMode.AF;
			case 0xC1: return Register16AddressingMode.BC;
			case 0xD1: return Register16AddressingMode.DE;
			case 0xE1: return Register16AddressingMode.HL;
		}
		
		return null;
	}	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		return "POP " + getAddressingMode(opcode).asText(new byte[0]);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		int pulledValue = cpu.pullValue();
		Register16AddressingMode addressingMode = getAddressingMode(opcode);
		addressingMode.setWord(cpu, pulledValue);

		return 12;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

