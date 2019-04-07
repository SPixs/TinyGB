package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrADDHL extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private Register16AddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x09: return new Register16AddressingMode(Register16Bits.BC);
			case 0x19: return new Register16AddressingMode(Register16Bits.DE);
			case 0x29: return new Register16AddressingMode(Register16Bits.HL);
			case 0x39: return new Register16AddressingMode(Register16Bits.SP);
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readWord(cpu);
		int hl = Register16Bits.HL.getValue(cpu);
		int newValue = hl + value;
		Register16Bits.HL.setValue(cpu, newValue);
		
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry((value & 0x0F) + (hl & 0x0F) > 0x0F);
		cpu.setFlagCarry(newValue > 0xFFFF);
		
		return 8;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		Register16AddressingMode addressingMode = getAddressingMode(opcode);
		return "ADD HL," + addressingMode.asText(new byte[0]);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}
