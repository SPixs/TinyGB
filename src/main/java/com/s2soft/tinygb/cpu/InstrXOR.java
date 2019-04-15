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
			case 0xAF: return RegisterAddressingMode.A;
			case 0xA8: return RegisterAddressingMode.B;
			case 0xA9: return RegisterAddressingMode.C;
			case 0xAA: return RegisterAddressingMode.D;
			case 0xAB: return RegisterAddressingMode.E;
			case 0xAC: return RegisterAddressingMode.H;
			case 0xAD: return RegisterAddressingMode.L;
			case 0xAE: return IndirectAddressMode.HL;
			case 0xEE: return ImmediateAddressMode.INSTANCE;
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

