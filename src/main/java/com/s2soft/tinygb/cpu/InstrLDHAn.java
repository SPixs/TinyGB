package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDHAn extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xF0;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// sets the 8 bits value
		byte value = cpu.getMemory().getByte(0x0FF00 + (additionnalBytes[0] & 0x0FF));
		cpu.setA(value);
		return 12;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		return "LD A,($FF00+" + toHexByte(memory.getByte(address+1)) + ")";
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 2;
	}
}
