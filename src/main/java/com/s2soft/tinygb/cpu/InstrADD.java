package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrADD extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x87: return RegisterAddressingMode.A;
			case 0x80: return RegisterAddressingMode.B;
			case 0x81: return RegisterAddressingMode.C;
			case 0x82: return RegisterAddressingMode.D;
			case 0x83: return RegisterAddressingMode.E;
			case 0x84: return RegisterAddressingMode.H;
			case 0x85: return RegisterAddressingMode.L;
			case 0x86: return IndirectAddressMode.HL;
			case 0xC6: return ImmediateAddressMode.INSTANCE;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes) & 0xFF;
		int a = cpu.getA() & 0xFF;
		byte result = (byte) ((a + value) & 0x0FF);
		cpu.setA(result);
		
		cpu.setFlagZero(result == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry((value & 0x0F) + (a & 0x0F) > 0x0F);
		cpu.setFlagCarry(a + value > 0xFF);
		
		switch (opcode) {
			case (byte)0x86:
			case (byte)0xC6: return 8;
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
			case (byte)0xC6: return 2;
			default : return 1;
		}
	}
}
