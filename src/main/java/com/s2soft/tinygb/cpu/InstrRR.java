package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrRR extends Instruction {

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
			case (byte)0x1F: return RegisterAddressingMode.A;
			case (byte)0x18: return RegisterAddressingMode.B;
			case (byte)0x19: return RegisterAddressingMode.C;
			case (byte)0x1A: return RegisterAddressingMode.D;
			case (byte)0x1B: return RegisterAddressingMode.E;
			case (byte)0x1C: return RegisterAddressingMode.H;
			case (byte)0x1D: return RegisterAddressingMode.L;
			case (byte)0x1E: return IndirectAddressMode.HL;
		}
		return null;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "RR " + addressingMode.asText(new byte[0]);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte valueToRotate = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		byte newValue = (byte) (valueToRotate >> 1);
		newValue = BitUtils.setBit(newValue, 7, cpu.getFlagCarry());
		
		getAddressingMode(opcode).setByte(cpu, newValue, additionnalBytes);

		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry((valueToRotate & 0x01) != 0);
		
		if (opcode == (byte)0x1E) return 16;
		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

