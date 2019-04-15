package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrINC extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x3C: return RegisterAddressingMode.A;
			case 0x04: return RegisterAddressingMode.B;
			case 0x0C: return RegisterAddressingMode.C;
			case 0x14: return RegisterAddressingMode.D;
			case 0x1C: return RegisterAddressingMode.E;
			case 0x24: return RegisterAddressingMode.H;
			case 0x2C: return RegisterAddressingMode.L;
			case 0x34: return IndirectAddressMode.HL;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		int newValue = value;
		newValue++;
		
		cpu.setFlagZero((newValue & 0xFF) == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry((value & 0x0F) + 1 > 0x0F);

		getAddressingMode(opcode).setByte(cpu, (byte) (newValue & 0xFF), additionnalBytes);
		
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
		return 1;
	}
}
