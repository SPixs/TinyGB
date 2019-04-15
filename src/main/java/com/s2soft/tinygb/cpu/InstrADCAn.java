package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrADCAn extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x8F: return RegisterAddressingMode.A;
			case 0x88: return RegisterAddressingMode.B;
			case 0x89: return RegisterAddressingMode.C;
			case 0x8A: return RegisterAddressingMode.D;
			case 0x8B: return RegisterAddressingMode.E;
			case 0x8C: return RegisterAddressingMode.H;
			case 0x8D: return RegisterAddressingMode.L;
			case 0x8E: return IndirectAddressMode.HL;
			case 0xCE: return ImmediateAddressMode.INSTANCE;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes) & 0x0FF;
		int regA = BitUtils.toUInt(cpu.getA());
		int carry = cpu.getFlagCarry() ? 1 : 0;
		int result = regA + value + carry;

		cpu.setA(BitUtils.toByte(result));
		
		cpu.setFlagZero((result & 0xFF) == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry((regA & 0x0F) + (value & 0x0F) + carry > 0x0F);
		cpu.setFlagCarry(result > 0x0FF);
		
		switch (opcode) {
			case (byte)0xCE:
			case (byte)0x8E: return 8;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "ADC A," + addressingMode.asText(new byte[] { memory.getByte(address+1) });
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		switch (opcode) {
			case (byte)0xCE: return 2;
			default : return 1;
		}
	}
}
