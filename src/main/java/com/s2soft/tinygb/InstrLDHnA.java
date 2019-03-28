package com.s2soft.tinygb;

public class InstrLDHnA extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xE0;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// sets the 8 bits value
		cpu.getMemory().setByte(0x0FF00 + (additionnalBytes[0] & 0x0FF), (cpu.getA() & 0x0FF));
		return 12;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		return "LD ($FF00+" + toHexByte(memory.getByte(address+1)) + "),A";
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 2;
	}
}
