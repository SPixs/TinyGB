package com.s2soft.tinygb;

public class InstrPUSH extends Instruction {

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
			case 0xF5: return new Register16AddressingMode(Register16Bits.AF);
			case 0xC5: return new Register16AddressingMode(Register16Bits.BC);
			case 0xD5: return new Register16AddressingMode(Register16Bits.DE);
			case 0xE5: return new Register16AddressingMode(Register16Bits.HL);
		}
		
		return null;
	}	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		return "PUSH " + getAddressingMode(opcode).asText(new byte[0]);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		Register16AddressingMode addressingMode = getAddressingMode(opcode);
		int value = addressingMode.readWord(cpu);
		cpu.pushShort(value);

		return 16;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

