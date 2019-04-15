package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDnA extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x7F: return RegisterAddressingMode.A;
			case 0x47: return RegisterAddressingMode.B;
			case 0x4F: return RegisterAddressingMode.C;
			case 0x57: return RegisterAddressingMode.D;
			case 0x5F: return RegisterAddressingMode.E;
			case 0x67: return RegisterAddressingMode.H;
			case 0x6F: return RegisterAddressingMode.L;
			case 0x02: return IndirectAddressMode.BC;
			case 0x12: return IndirectAddressMode.DE;
			case 0x77: return IndirectAddressMode.HL;
			case 0xEA: return AbsoluteAddressingMode.INSTANCE;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		byte value = cpu.getA();
		getAddressingMode(opcode).setByte(cpu, value, additionnalBytes);
		
		switch (opcode) {
			case (byte)0x02:
			case (byte)0x12:
			case (byte)0x77: return 8;
			case (byte)0xEA: return 16;
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
