package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrAND extends Instruction {

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
		switch (opcode) {
			case (byte)0xA7: return new RegisterAddressingMode(Register8Bits.A);
			case (byte)0xA0: return new RegisterAddressingMode(Register8Bits.B);
			case (byte)0xA1: return new RegisterAddressingMode(Register8Bits.C);
			case (byte)0xA2: return new RegisterAddressingMode(Register8Bits.D);
			case (byte)0xA3: return new RegisterAddressingMode(Register8Bits.E);
			case (byte)0xA4: return new RegisterAddressingMode(Register8Bits.H);
			case (byte)0xA5: return new RegisterAddressingMode(Register8Bits.L);
			case (byte)0xA6: return new IndirectAddressMode(Register16Bits.HL);
			case (byte)0xE6: return new ImmediateAddressMode();
		}
		return null;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "AND " + addressingMode.asText(new byte[] { memory.getByte(address+1), memory.getByte(address+2) });
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte valueToAND = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		byte newValue = (byte) (cpu.getA() & valueToAND);
		cpu.setA(newValue);

		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(true);
		cpu.setFlagCarry(false);
		
		if (opcode == (byte)0xA6) return 8;
		if (opcode == (byte)0xE6) return 8;
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		if (opcode == (byte)0xE6) return 2;
		return 1;
	}
}

