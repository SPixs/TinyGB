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
			case (byte)0x37 : return RegisterAddressingMode.A;
			case (byte)0x30 : return RegisterAddressingMode.B;
			case (byte)0x31 : return RegisterAddressingMode.C;
			case (byte)0x32 : return RegisterAddressingMode.D;
			case (byte)0x33 : return RegisterAddressingMode.E;
			case (byte)0x34 : return RegisterAddressingMode.H;
			case (byte)0x35 : return RegisterAddressingMode.L;
			case (byte)0x36 : return IndirectAddressMode.HL;
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
		getAddressingMode(opcode).setByte(cpu, result, additionnalBytes);
		
		cpu.setFlagZero(result == 0);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(false);
		cpu.setFlagCarry(false);
		
		if (opcode == 0x36) return 16;
		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

