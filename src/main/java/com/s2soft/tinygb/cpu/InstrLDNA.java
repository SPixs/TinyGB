package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDNA extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x7F: return new RegisterAddressingMode(Register8Bits.A);
			case 0x47: return new RegisterAddressingMode(Register8Bits.B);
			case 0x4F: return new RegisterAddressingMode(Register8Bits.C);
			case 0x57: return new RegisterAddressingMode(Register8Bits.D);
			case 0x5F: return new RegisterAddressingMode(Register8Bits.E);
			case 0x67: return new RegisterAddressingMode(Register8Bits.H);
			case 0x6F: return new RegisterAddressingMode(Register8Bits.L);
			case 0x02: return new IndirectAddressMode(Register16Bits.BC);
			case 0x12: return new IndirectAddressMode(Register16Bits.DE);
			case 0x77: return new IndirectAddressMode(Register16Bits.HL);
			case 0xEA: return new AbsoluteAddressingMode();
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		byte value = cpu.getA();
		getAddressingMode(opcode).setByte(cpu, value, additionnalBytes);
		
		switch ((int)opcode) {
			case 0x02:
			case 0x12:
			case 0x77: return 8;
			case 0xEA: return 16;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "LD " + addressingMode.asText(new byte[] { memory.getByte(address+1), memory.getByte(address+2) }) + ",A";
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		switch (opcode) {
			case (byte)0xEA: return 3;
			default : return 1;
		}
	}
}
