package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class InstrRES extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================
	
	private IAddressingMode[] m_allModes = new IAddressingMode[] {
			RegisterAddressingMode.B,
			RegisterAddressingMode.C,
			RegisterAddressingMode.D,
			RegisterAddressingMode.E,
			RegisterAddressingMode.H,
			RegisterAddressingMode.L,
			IndirectAddressMode.HL,
			RegisterAddressingMode.A
	};

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return getBitNumber(opcode) != -1;
	}
	
	private int getBitNumber(byte opcode) {
		int lowerNibble = (opcode & 0x0F);
		int upperNibble = ((opcode & 0xF0) >> 4);
		if (upperNibble >= 0x08 && upperNibble < 0x0C) { 
			int bitIndex = 2*(upperNibble-8);
			bitIndex += lowerNibble >= 8 ? 1 : 0;
			return bitIndex;
		}
		return -1;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {
		int lowerNibble = (opcode & 0x0F);
		return m_allModes[lowerNibble & 0x07];
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		return "RES " + getBitNumber(opcode) + "," + getAddressingMode(opcode).asText(new byte[0]);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		int bitNumber = getBitNumber(opcode);
		byte valueToReset = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		valueToReset = BitUtils.setBit(valueToReset, bitNumber, false);
		getAddressingMode(opcode).setByte(cpu, valueToReset, additionnalBytes);
		
		int lowerNibble = (opcode & 0x0F);
		if ((lowerNibble & 0x07) == 0x06) return 16; // Lots of documents tell 16 cycles here
		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

