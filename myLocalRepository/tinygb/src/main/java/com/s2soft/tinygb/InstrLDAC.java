package com.s2soft.tinygb;

public class InstrLDAC extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xF2;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		byte value = cpu.getMemory().getByte(0x0FF00 + (cpu.getC() & 0x0FF));
		cpu.setA(value);
		return 8;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		return "LD A,($FF00+C)";
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}
