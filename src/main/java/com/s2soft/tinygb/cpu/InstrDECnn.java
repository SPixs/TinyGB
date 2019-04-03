package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrDECnn extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private Register16AddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x0B: return new Register16AddressingMode(Register16Bits.BC);
			case 0x1B: return new Register16AddressingMode(Register16Bits.DE);
			case 0x2B: return new Register16AddressingMode(Register16Bits.HL);
			case 0x3B: return new Register16AddressingMode(Register16Bits.SP);
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 16 bits value
		short value = (short) getAddressingMode(opcode).readWord(cpu);
		getAddressingMode(opcode).setWord(cpu, value-1);
		return 8;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		Register16AddressingMode addressingMode = getAddressingMode(opcode);
		return "DEC " + addressingMode.asText(new byte[0]);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}
