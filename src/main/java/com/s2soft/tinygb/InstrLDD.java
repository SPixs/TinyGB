package com.s2soft.tinygb;

public class InstrLDD extends Instruction {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)(0x3A) || opcode == (byte)(0x32);
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		return opcode == (byte)(0x32) ? "LD (HL-),A" : "LD A,(HL-)";
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionalBytes) {
		IndirectAddressMode addressingModeIndirect = new IndirectAddressMode(Register16Bits.HL);
		RegisterAddressingMode addressingModeA = new RegisterAddressingMode(Register8Bits.A);
		IAddressingMode src = (opcode == (byte)(0x32)) ? addressingModeA : addressingModeIndirect;
		IAddressingMode dest = (opcode == (byte)(0x32)) ? addressingModeIndirect: addressingModeA;
		dest.setByte(cpu, src.readByte(cpu, additionalBytes), additionalBytes);
		Register16Bits.HL.setValue(cpu, Register16Bits.HL.getValue(cpu) - 1);
		return 8;
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 1;
	}
}
