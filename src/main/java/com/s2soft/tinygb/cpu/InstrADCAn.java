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
			case 0x8F: return new RegisterAddressingMode(Register8Bits.A);
			case 0x88: return new RegisterAddressingMode(Register8Bits.B);
			case 0x89: return new RegisterAddressingMode(Register8Bits.C);
			case 0x8A: return new RegisterAddressingMode(Register8Bits.D);
			case 0x8B: return new RegisterAddressingMode(Register8Bits.E);
			case 0x8C: return new RegisterAddressingMode(Register8Bits.H);
			case 0x8D: return new RegisterAddressingMode(Register8Bits.L);
			case 0x8E: return new IndirectAddressMode(Register16Bits.HL);
			case 0xCE: return new ImmediateAddressMode();
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes) & 0x0FF;
		int result = BitUtils.toUInt(cpu.getA()) + value + (cpu.getFlagCarry() ? 1 : 0);

		cpu.setA(BitUtils.toByte(result));
		
		cpu.setFlagZero(result == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry((cpu.getA() & 0x0F) + (value & 0x0F) + (cpu.getFlagCarry() ? 1 : 0) > 0x0F);
		cpu.setFlagCarry(result > 0x0FF);
		
		switch ((int)opcode) {
			case 0x8D:
			case 0xCE: return 8;
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
