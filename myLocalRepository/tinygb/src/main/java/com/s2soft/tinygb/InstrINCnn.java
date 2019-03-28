package com.s2soft.tinygb;

public class InstrINCnn extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private Register16AddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x03: return new Register16AddressingMode(Register16Bits.BC);
			case 0x13: return new Register16AddressingMode(Register16Bits.DE);
			case 0x23: return new Register16AddressingMode(Register16Bits.HL);
			case 0x33: return new Register16AddressingMode(Register16Bits.SP);
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 16 bits value
		short value = (short) getAddressingMode(opcode).readWord(cpu);
		getAddressingMode(opcode).setWord(cpu, value+1);
		return 8;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		Register16AddressingMode addressingMode = getAddressingMode(opcode);
		return "INC " + addressingMode.asText(new byte[0]);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}
