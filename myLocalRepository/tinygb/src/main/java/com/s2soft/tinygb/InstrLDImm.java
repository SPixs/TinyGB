package com.s2soft.tinygb;

public class InstrLDImm extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x06: return new RegisterAddressingMode(Register8Bits.B);
			case 0x0E: return new RegisterAddressingMode(Register8Bits.C);
			case 0x16: return new RegisterAddressingMode(Register8Bits.D);
			case 0x1E: return new RegisterAddressingMode(Register8Bits.E);
			case 0x26: return new RegisterAddressingMode(Register8Bits.H);
			case 0x2E: return new RegisterAddressingMode(Register8Bits.L);
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the immediate 8 bits value
		byte immediate = additionnalBytes[0];
		
		IAddressingMode addressingMode = getAddressingMode(opcode);
		addressingMode.setByte(cpu, immediate, additionnalBytes);
		
		return 8; // 8 cycles for each version
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		byte immediateValue = memory.getByte(address+1);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "LD " + addressingMode.asText(new byte[0]) + "," + toHexByte(immediateValue);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 2;
	}
}
