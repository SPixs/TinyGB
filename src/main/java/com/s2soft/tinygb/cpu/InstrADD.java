package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrADD extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x97: return new RegisterAddressingMode(Register8Bits.A);
			case 0x80: return new RegisterAddressingMode(Register8Bits.B);
			case 0x81: return new RegisterAddressingMode(Register8Bits.C);
			case 0x82: return new RegisterAddressingMode(Register8Bits.D);
			case 0x83: return new RegisterAddressingMode(Register8Bits.E);
			case 0x84: return new RegisterAddressingMode(Register8Bits.H);
			case 0x85: return new RegisterAddressingMode(Register8Bits.L);
			case 0x86: return new IndirectAddressMode(Register16Bits.HL);
			case 0xC6: return new ImmediateAddressMode();
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes) & 0x0FF;
		int result = (cpu.getA() & 0x0FF) + value;
		cpu.setA((byte)(result & 0x0FF));
		
		cpu.setFlagZero(result == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(((value & 0x08) == 1) && ((result & 0x08) == 0));
		cpu.setFlagCarry(((value & 0x80) == 1) && ((result & 0x80) == 0));
		
		switch ((int)opcode) {
			case 0x86:
			case 0xC6: return 8;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "ADD " + addressingMode.asText(new byte[] { memory.getByte(address+1) });
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		switch (opcode) {
			case (byte)0xD6: return 2;
			default : return 1;
		}
	}
}