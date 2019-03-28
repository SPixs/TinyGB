package com.s2soft.tinygb;

public class InstrLDImm16 extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getRegister(opcode) != null;
	}

	private Register16Bits getRegister(byte opcode) {

		switch (opcode) {
			case 0x01: return Register16Bits.BC;
			case 0x11: return Register16Bits.DE;
			case 0x21: return Register16Bits.HL;
			case 0x31: return Register16Bits.SP;
		}
		
		return null;
	}
	
	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the immediate 8 bits value
		int immediate = (additionnalBytes[0] & 0x0FF) |  ((additionnalBytes[1] & 0x0FF) << 8);
		
		Register16Bits register = getRegister(opcode);
		register.setValue(cpu, immediate);
		
		return 12; // 12 cycles for each version
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		int immediateValue = (memory.getByte(address+1) & 0x00FF) | memory.getByte(address+2) << 8;
		Register16Bits register = getRegister(opcode);
		return "LD " + register.name() + "," + toHexShort(immediateValue);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 3;
	}
}
