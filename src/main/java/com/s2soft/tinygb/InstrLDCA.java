package com.s2soft.tinygb;

public class InstrLDCA extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xE2;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		cpu.getMemory().setByte(0x0FF00 + (cpu.getC() & 0x0FF), (cpu.getA() & 0x0FF));
		return 8;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		return "LD ($FF00+C),A";
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}
