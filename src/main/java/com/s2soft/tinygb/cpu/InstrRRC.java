package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrRRC extends Instruction {

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
			case (byte)0x0F: return new RegisterAddressingMode(Register8Bits.A);
			case (byte)0x08: return new RegisterAddressingMode(Register8Bits.B);
			case (byte)0x09: return new RegisterAddressingMode(Register8Bits.C);
			case (byte)0x0A: return new RegisterAddressingMode(Register8Bits.D);
			case (byte)0x0B: return new RegisterAddressingMode(Register8Bits.E);
			case (byte)0x0C: return new RegisterAddressingMode(Register8Bits.H);
			case (byte)0x0D: return new RegisterAddressingMode(Register8Bits.L);
			case (byte)0x0E: return new IndirectAddressMode(Register16Bits.HL);
		}
		return null;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "RRC " + addressingMode.asText(new byte[0]);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte valueToRotate = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		byte newValue = (byte) (valueToRotate >> 1);
		newValue = BitUtils.setBit(newValue, 7, (valueToRotate & 0x01) != 0);
		
		getAddressingMode(opcode).setByte(cpu, newValue, additionnalBytes);

		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry((valueToRotate & 0x01) != 0);
		
		if (opcode == (byte)0x0E) return 16;
		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

