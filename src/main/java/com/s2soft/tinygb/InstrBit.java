package com.s2soft.tinygb;

public class InstrBit extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

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
		if (upperNibble >= 4 && upperNibble < 8) { 
			int bitIndex = 2*(upperNibble-4);
			bitIndex += lowerNibble >= 8 ? 1 : 0;
			return bitIndex;
		}
		return -1;
	}
	
	private IAddressingMode getAddressingMode(byte opcode) {
		IAddressingMode[] allModes = new IAddressingMode[] {
				new RegisterAddressingMode(Register8Bits.B),
				new RegisterAddressingMode(Register8Bits.C),
				new RegisterAddressingMode(Register8Bits.D),
				new RegisterAddressingMode(Register8Bits.E),
				new RegisterAddressingMode(Register8Bits.H),
				new RegisterAddressingMode(Register8Bits.L),
				new IndirectAddressMode(Register16Bits.HL),
				new RegisterAddressingMode(Register8Bits.A)
		};
		
		int lowerNibble = (opcode & 0x0F);
		return allModes[lowerNibble & 0x07];
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		return "BIT " + getBitNumber(opcode) + "," + getAddressingMode(opcode).asText(new byte[0]);
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		int bitNumber = getBitNumber(opcode);
		byte valueToTest = getAddressingMode(opcode).readByte(cpu, additionnalBytes);
		boolean set = ((valueToTest >> bitNumber) & 0x01) != 0;
		
		cpu.setFlagZero(!set);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry(true);
		
		int lowerNibble = (opcode & 0x0F);
		if ((lowerNibble & 0x07) == 0x06) return 16;
		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}

