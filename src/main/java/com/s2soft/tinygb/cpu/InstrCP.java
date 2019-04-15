package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrCP extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0xBF: return RegisterAddressingMode.A;
			case 0xB8: return RegisterAddressingMode.B;
			case 0xB9: return RegisterAddressingMode.C;
			case 0xBA: return RegisterAddressingMode.D;
			case 0xBB: return RegisterAddressingMode.E;
			case 0xBC: return RegisterAddressingMode.H;
			case 0xBD: return RegisterAddressingMode.L;
			case 0xBE: return IndirectAddressMode.HL;
			case 0xFE: return ImmediateAddressMode.INSTANCE;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes) & 0x0FF;
		int result = (cpu.getA() & 0x0FF) - value;
		
		cpu.setFlagZero(result == 0);
		cpu.setFlagSubtract(true);
		cpu.setFlagHalfCarry((value & 0x0F) > (cpu.getA() & 0x0F));
		cpu.setFlagCarry(value > (cpu.getA() & 0x0FF));
		
		switch (opcode) {
			case (byte)0xBE:
			case (byte)0xFE: return 8;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "CP " + addressingMode.asText(new byte[] { memory.getByte(address+1) });
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		switch (opcode) {
			case (byte)0xFE: return 2;
			default : return 1;
		}
	}
}
