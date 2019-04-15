package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrDEC extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x3D: return RegisterAddressingMode.A;
			case 0x05: return RegisterAddressingMode.B;
			case 0x0D: return RegisterAddressingMode.C;
			case 0x15: return RegisterAddressingMode.D;
			case 0x1D: return RegisterAddressingMode.E;
			case 0x25: return RegisterAddressingMode.H;
			case 0x2D: return RegisterAddressingMode.L;
			case 0x35: return IndirectAddressMode.HL;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		byte value = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		byte newValue = value;
		newValue--;
		
		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(true);
//		cpu.setFlagHalfCarry((newValue & 0x10) == (value & 0x10));
		cpu.setFlagHalfCarry((value & 0x0F) == 0);

		getAddressingMode(opcode).setByte(cpu, newValue, additionnalBytes);
		
		switch ((int)opcode) {
			case 0x35: return 12;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "DEC " + addressingMode.asText(new byte[0]);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}
