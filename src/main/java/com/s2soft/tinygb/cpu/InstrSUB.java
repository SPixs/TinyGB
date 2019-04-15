package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrSUB extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x97: return RegisterAddressingMode.A;
			case 0x90: return RegisterAddressingMode.B;
			case 0x91: return RegisterAddressingMode.C;
			case 0x92: return RegisterAddressingMode.D;
			case 0x93: return RegisterAddressingMode.E;
			case 0x94: return RegisterAddressingMode.H;
			case 0x95: return RegisterAddressingMode.L;
			case 0x96: return IndirectAddressMode.HL;
			case 0xD6: return ImmediateAddressMode.INSTANCE;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes) & 0x0FF;
		int a = cpu.getA() & 0xFF;
		int result = a - value;
		cpu.setA(BitUtils.toByte(result));
		
		cpu.setFlagZero(result == 0);
		cpu.setFlagSubtract(true);
		cpu.setFlagHalfCarry((value & 0x0F) > (a & 0x0F));
		cpu.setFlagCarry(result < 0);
		
		switch (opcode) {
			case (byte)0x96:
			case (byte)0xD6: return 8;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "SUB " + addressingMode.asText(new byte[] { memory.getByte(address+1) });
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		switch (opcode) {
			case (byte)0xD6: return 2;
			default : return 1;
		}
	}
}
