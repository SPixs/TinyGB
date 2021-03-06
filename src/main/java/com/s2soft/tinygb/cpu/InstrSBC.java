package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrSBC extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x9F: return RegisterAddressingMode.A;
			case 0x98: return RegisterAddressingMode.B;
			case 0x99: return RegisterAddressingMode.C;
			case 0x9A: return RegisterAddressingMode.D;
			case 0x9B: return RegisterAddressingMode.E;
			case 0x9C: return RegisterAddressingMode.H;
			case 0x9D: return RegisterAddressingMode.L;
			case 0x9E: return IndirectAddressMode.HL;
			case 0xDE: return ImmediateAddressMode.INSTANCE;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		int value = getAddressingMode(opcode).readByte(cpu, additionnalBytes) & 0xFF;
		int carry = cpu.getFlagCarry() ? 1 : 0;
		int a = cpu.getA() & 0xFF;
		
		int result = a - value - carry;
		cpu.setA(BitUtils.toByte(result));
		
		cpu.setFlagZero((result & 0xFF) == 0);
		cpu.setFlagSubtract(true);
		cpu.setFlagHalfCarry((a & 0x0F) - (value & 0x0F) - carry < 0);
		cpu.setFlagCarry(result < 0);
		
		switch (opcode) {
			case (byte)0x9E:
			case (byte)0xDE: return 8;
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
