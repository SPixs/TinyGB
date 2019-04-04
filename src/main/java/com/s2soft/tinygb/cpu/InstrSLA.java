package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrSLA extends Instruction {

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
			case (byte)0x27: return new RegisterAddressingMode(Register8Bits.A);
			case (byte)0x20: return new RegisterAddressingMode(Register8Bits.B);
			case (byte)0x21: return new RegisterAddressingMode(Register8Bits.C);
			case (byte)0x22: return new RegisterAddressingMode(Register8Bits.D);
			case (byte)0x23: return new RegisterAddressingMode(Register8Bits.E);
			case (byte)0x24: return new RegisterAddressingMode(Register8Bits.H);
			case (byte)0x25: return new RegisterAddressingMode(Register8Bits.L);
			case (byte)0x26: return new IndirectAddressMode(Register16Bits.HL);
		}
		return null;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "SLA " + addressingMode.asText(new byte[0]);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte valueToRotate = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		byte newValue = (byte) (valueToRotate << 1);
		newValue &= 0xFE;
		
		getAddressingMode(opcode).setByte(cpu, newValue, additionnalBytes);

		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry((valueToRotate & 0x80) != 0);
		
		if (opcode == (byte)0x26) return 16;
		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

