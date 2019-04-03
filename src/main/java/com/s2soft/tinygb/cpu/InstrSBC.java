package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrSBC extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x9F: return new RegisterAddressingMode(Register8Bits.A);
			case 0x98: return new RegisterAddressingMode(Register8Bits.B);
			case 0x99: return new RegisterAddressingMode(Register8Bits.C);
			case 0x9A: return new RegisterAddressingMode(Register8Bits.D);
			case 0x9B: return new RegisterAddressingMode(Register8Bits.E);
			case 0x9C: return new RegisterAddressingMode(Register8Bits.H);
			case 0x9D: return new RegisterAddressingMode(Register8Bits.L);
			case 0x9E: return new IndirectAddressMode(Register16Bits.HL);
			case 0xDE: return new ImmediateAddressMode();
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes) & 0x0FF;
		byte result = (byte)(((cpu.getA() & 0x0FF) - value - (cpu.getFlagCarry() ? 1 : 0)) & 0x0FF);
		cpu.setA(result);
		
		cpu.setFlagZero(result == 0);
		cpu.setFlagSubtract(true);
		cpu.setFlagHalfCarry((value & 0x0F) > (cpu.getA() & 0x0F));
		cpu.setFlagCarry(value > (cpu.getA() & 0x0FF));
		
		switch ((int)opcode) {
			case 0x9E:
			case 0xDE: return 8;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "SBC A," + addressingMode.asText(new byte[] { memory.getByte(address+1) });
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		switch (opcode) {
			case (byte)0xDE: return 2;
			default : return 1;
		}
	}
}