package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDAn extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x7F: return RegisterAddressingMode.A;
			case 0x78: return RegisterAddressingMode.B;
			case 0x79: return RegisterAddressingMode.C;
			case 0x7A: return RegisterAddressingMode.D;
			case 0x7B: return RegisterAddressingMode.E;
			case 0x7C: return RegisterAddressingMode.H;
			case 0x7D: return RegisterAddressingMode.L;
			case 0x0A: return IndirectAddressMode.BC;
			case 0x1A: return IndirectAddressMode.DE;
			case 0x7E: return IndirectAddressMode.HL;
			case 0xFA: return AbsoluteAddressingMode.INSTANCE;
			case 0x3E: return ImmediateAddressMode.INSTANCE;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		byte value = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		cpu.setA(value);
		
		switch (opcode) {
			case (byte)0x0A:
			case (byte)0x1A:
			case (byte)0x7E:
			case (byte)0x3E: return 8;
			case (byte)0xFA: return 16;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "LD A," + addressingMode.asText(new byte[] { memory.getByte(address+1), memory.getByte(address+2) });
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
