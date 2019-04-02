package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDrr extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingModeSource(opcode) != null;
	}
	
	private IAddressingMode getAddressingModeSource(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x7F: return new RegisterAddressingMode(Register8Bits.A);
			case 0x78: return new RegisterAddressingMode(Register8Bits.B);
			case 0x79: return new RegisterAddressingMode(Register8Bits.C);
			case 0x7A: return new RegisterAddressingMode(Register8Bits.D);
			case 0x7B: return new RegisterAddressingMode(Register8Bits.E);
			case 0x7C: return new RegisterAddressingMode(Register8Bits.H);
			case 0x7D: return new RegisterAddressingMode(Register8Bits.L);
			case 0x7E: return new IndirectAddressMode(Register16Bits.HL);

			case 0x40: return new RegisterAddressingMode(Register8Bits.B);
			case 0x41: return new RegisterAddressingMode(Register8Bits.C);
			case 0x42: return new RegisterAddressingMode(Register8Bits.D);
			case 0x43: return new RegisterAddressingMode(Register8Bits.E);
			case 0x44: return new RegisterAddressingMode(Register8Bits.H);
			case 0x45: return new RegisterAddressingMode(Register8Bits.L);
			case 0x46: return new IndirectAddressMode(Register16Bits.HL);
			
			case 0x48: return new RegisterAddressingMode(Register8Bits.B);
			case 0x49: return new RegisterAddressingMode(Register8Bits.C);
			case 0x4A: return new RegisterAddressingMode(Register8Bits.D);
			case 0x4B: return new RegisterAddressingMode(Register8Bits.E);
			case 0x4C: return new RegisterAddressingMode(Register8Bits.H);
			case 0x4D: return new RegisterAddressingMode(Register8Bits.L);
			case 0x4E: return new IndirectAddressMode(Register16Bits.HL);

			case 0x50: return new RegisterAddressingMode(Register8Bits.B);
			case 0x51: return new RegisterAddressingMode(Register8Bits.C);
			case 0x52: return new RegisterAddressingMode(Register8Bits.D);
			case 0x53: return new RegisterAddressingMode(Register8Bits.E);
			case 0x54: return new RegisterAddressingMode(Register8Bits.H);
			case 0x55: return new RegisterAddressingMode(Register8Bits.L);
			case 0x56: return new IndirectAddressMode(Register16Bits.HL);

			case 0x58: return new RegisterAddressingMode(Register8Bits.B);
			case 0x59: return new RegisterAddressingMode(Register8Bits.C);
			case 0x5A: return new RegisterAddressingMode(Register8Bits.D);
			case 0x5B: return new RegisterAddressingMode(Register8Bits.E);
			case 0x5C: return new RegisterAddressingMode(Register8Bits.H);
			case 0x5D: return new RegisterAddressingMode(Register8Bits.L);
			case 0x5E: return new IndirectAddressMode(Register16Bits.HL);

			case 0x60: return new RegisterAddressingMode(Register8Bits.B);
			case 0x61: return new RegisterAddressingMode(Register8Bits.C);
			case 0x62: return new RegisterAddressingMode(Register8Bits.D);
			case 0x63: return new RegisterAddressingMode(Register8Bits.E);
			case 0x64: return new RegisterAddressingMode(Register8Bits.H);
			case 0x65: return new RegisterAddressingMode(Register8Bits.L);
			case 0x66: return new IndirectAddressMode(Register16Bits.HL);

			case 0x68: return new RegisterAddressingMode(Register8Bits.B);
			case 0x69: return new RegisterAddressingMode(Register8Bits.C);
			case 0x6A: return new RegisterAddressingMode(Register8Bits.D);
			case 0x6B: return new RegisterAddressingMode(Register8Bits.E);
			case 0x6C: return new RegisterAddressingMode(Register8Bits.H);
			case 0x6D: return new RegisterAddressingMode(Register8Bits.L);
			case 0x6E: return new IndirectAddressMode(Register16Bits.HL);
			
			case 0x70: return new RegisterAddressingMode(Register8Bits.B);
			case 0x71: return new RegisterAddressingMode(Register8Bits.C);
			case 0x72: return new RegisterAddressingMode(Register8Bits.D);
			case 0x73: return new RegisterAddressingMode(Register8Bits.E);
			case 0x74: return new RegisterAddressingMode(Register8Bits.H);
			case 0x75: return new RegisterAddressingMode(Register8Bits.L);
			
			case 0x36: return new ImmediateAddressMode();
		}
		
		return null;
	}
	
	private IAddressingMode getAddressingModeDest(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x7F: 
			case 0x78: 
			case 0x79: 
			case 0x7A: 
			case 0x7B: 
			case 0x7C: 
			case 0x7D: 
			case 0x7E: return new RegisterAddressingMode(Register8Bits.A);
	
			case 0x40: 
			case 0x41: 
			case 0x42: 
			case 0x43: 
			case 0x44: 
			case 0x45: 
			case 0x46: return new RegisterAddressingMode(Register8Bits.B);
			
			case 0x48: 
			case 0x49: 
			case 0x4A: 
			case 0x4B: 
			case 0x4C: 
			case 0x4D: 
			case 0x4E: return new RegisterAddressingMode(Register8Bits.C);
	
			case 0x50: 
			case 0x51: 
			case 0x52:
			case 0x53: 
			case 0x54: 
			case 0x55: 
			case 0x56: return new RegisterAddressingMode(Register8Bits.D);
	
			case 0x58:
			case 0x59: 
			case 0x5A:
			case 0x5B: 
			case 0x5C: 
			case 0x5D: 
			case 0x5E: return new RegisterAddressingMode(Register8Bits.E);
	
			case 0x60: 
			case 0x61: 
			case 0x62: 
			case 0x63: 
			case 0x64: 
			case 0x65: 
			case 0x66: return new RegisterAddressingMode(Register8Bits.H);
	
			case 0x68: 
			case 0x69: 
			case 0x6A: 
			case 0x6B: 
			case 0x6C: 
			case 0x6D: 
			case 0x6E: return new RegisterAddressingMode(Register8Bits.L);
			
			case 0x70: 
			case 0x71: 
			case 0x72: 
			case 0x73: 
			case 0x74: 
			case 0x75: 
			case 0x36: return new IndirectAddressMode(Register16Bits.HL);
		}
		
		return null;
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		// read the 8 bits value
		byte value = getAddressingModeSource(opcode).readByte(cpu, additionnalBytes);
		getAddressingModeDest(opcode).setByte(cpu, value, additionnalBytes);
		
		switch ((int)opcode) {
			case 0x7E:
			case 0x46:
			case 0x4E:
			case 0x56: 
			case 0x5E: 
			case 0x66:
			case 0x6E:
			case 0x70: 
			case 0x71: 
			case 0x72: 
			case 0x73: 
			case 0x74: 
			case 0x75: return 8;
			case 0x36: return 12;
			default : return 4;
		}
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte opcode = memory.getByte(address);
		IAddressingMode addressingModeDest = getAddressingModeDest(opcode);
		IAddressingMode addressingModeSource = getAddressingModeSource(opcode);
		final byte[] additionnalBytes = new byte[] { memory.getByte(address+1), memory.getByte(address+2) };
		return "LD " + addressingModeDest.asText(additionnalBytes) + "," + addressingModeSource.asText(additionnalBytes);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		switch (opcode) {
			case (byte)0x36: return 2;
			default : return 1;
		}
	}
}
