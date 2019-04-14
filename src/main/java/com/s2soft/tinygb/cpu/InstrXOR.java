package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrXOR extends Instruction {


	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingMode(opcode) != null;
	}

	private IAddressingMode getAddressingMode(byte opcode) {

		switch (opcode & 0xFF) {
			case 0xAF: return new RegisterAddressingMode(Register8Bits.A);
			case 0xA8: return new RegisterAddressingMode(Register8Bits.B);
			case 0xA9: return new RegisterAddressingMode(Register8Bits.C);
			case 0xAA: return new RegisterAddressingMode(Register8Bits.D);
			case 0xAB: return new RegisterAddressingMode(Register8Bits.E);
			case 0xAC: return new RegisterAddressingMode(Register8Bits.H);
			case 0xAD: return new RegisterAddressingMode(Register8Bits.L);
			case 0xAE: return new IndirectAddressMode(Register16Bits.HL);
			case 0xEE: return new ImmediateAddressMode();
		}
		
		return null;
	}
	

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		byte immediateValue = memory.getByte(address+1);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "XOR " + addressingMode.asText(new byte[] { immediateValue });
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		final byte value = (byte)((cpu.getA() ^ getAddressingMode(opcode).readByte(cpu, additionalBytes) & 0x0FF));
		cpu.setA(value);
		cpu.setFlagZero(value == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry(false);
		if ((opcode & 0xFF) == 0xEE) return 8;
		if ((opcode & 0xFF) == 0xAE) return 8;
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		if ((opcode & 0xFF) == 0xEE) return 2;
		return 1;
	}
}

