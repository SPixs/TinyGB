package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrOR extends Instruction {

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
			case (byte)0xB7: return RegisterAddressingMode.A;
			case (byte)0xB0: return RegisterAddressingMode.B;
			case (byte)0xB1: return RegisterAddressingMode.C;
			case (byte)0xB2: return RegisterAddressingMode.D;
			case (byte)0xB3: return RegisterAddressingMode.E;
			case (byte)0xB4: return RegisterAddressingMode.H;
			case (byte)0xB5: return RegisterAddressingMode.L;
			case (byte)0xB6: return IndirectAddressMode.HL;
			case (byte)0xF6: return ImmediateAddressMode.INSTANCE;
		}
		return null;
	}
	
	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingMode = getAddressingMode(opcode);
		return "OR " + addressingMode.asText(new byte[] { memory.getByte(address+1), memory.getByte(address+2) });
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte valueToAND = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		byte newValue = (byte) (cpu.getA() | valueToAND);
		cpu.setA(newValue);

		cpu.setFlagZero(newValue == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry(false);
		
		if (opcode == (byte)0xB6) return 8;
		if (opcode == (byte)0xF6) return 8;
		return 4;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		if (opcode == (byte)0xF6) return 2;
		return 1;
	}
}

