package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDSPHL extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xF9;
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		Register16AddressingMode readAddressingMode = new Register16AddressingMode(Register16Bits.HL);
		Register16AddressingMode writeAddressingMode = new Register16AddressingMode(Register16Bits.SP);
		writeAddressingMode.setWord(cpu, readAddressingMode.readWord(cpu));

		return 8; 
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		return "LD SP,HL";
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}
