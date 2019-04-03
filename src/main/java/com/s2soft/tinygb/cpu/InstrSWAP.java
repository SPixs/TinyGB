package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrSWAP extends Instruction {

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
		switch(opcode) {
			case (byte)0x37 : return new RegisterAddressingMode(Register8Bits.A);
			case (byte)0x30 : return new RegisterAddressingMode(Register8Bits.B);
			case (byte)0x31 : return new RegisterAddressingMode(Register8Bits.C);
			case (byte)0x32 : return new RegisterAddressingMode(Register8Bits.D);
			case (byte)0x33 : return new RegisterAddressingMode(Register8Bits.E);
			case (byte)0x34 : return new RegisterAddressingMode(Register8Bits.H);
			case (byte)0x35 : return new RegisterAddressingMode(Register8Bits.L);
			case (byte)0x36 : return new IndirectAddressMode(Register16Bits.HL);
		}
		
		return null;
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		return "SWAP " + getAddressingMode(opcode).asText(new byte[0]);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte value = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		byte result = (byte) ((value & 0x0F) << 4 | (value & 0x0F0) >> 4);
		
		cpu.setFlagZero(result == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(true);
		cpu.setFlagCarry(false);
		
		if (opcode == 0x36) return 16;
		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

