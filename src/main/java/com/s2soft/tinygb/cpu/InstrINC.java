package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrINC extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x3C: return new RegisterAddressingMode(Register8Bits.A);
			case 0x04: return new RegisterAddressingMode(Register8Bits.B);
			case 0x0C: return new RegisterAddressingMode(Register8Bits.C);
			case 0x14: return new RegisterAddressingMode(Register8Bits.D);
			case 0x1C: return new RegisterAddressingMode(Register8Bits.E);
			case 0x24: return new RegisterAddressingMode(Register8Bits.H);
			case 0x2C: return new RegisterAddressingMode(Register8Bits.L);
			case 0x34: return new IndirectAddressMode(Register16Bits.HL);
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		byte value = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		byte newValue = value;
		newValue++;
		
		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(((newValue & 0x10) != 0) && ((value & 0x10) == 0));

		getAddressingMode(opcode).setByte(cpu, newValue, additionnalBytes);
		
		switch ((int)opcode) {
			case 0x34: return 12;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "INC " + addressingMode.asText(new byte[0]);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		switch (opcode) {
			case (byte)0x3E: return 2;
			case (byte)0xFA: return 3;
			default : return 1;
		}
	}
}
