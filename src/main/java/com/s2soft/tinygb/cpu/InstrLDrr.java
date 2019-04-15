package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDrr extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return getAddressingModeSource(opcode) != null;
	}
	
	private IAddressingMode getAddressingModeSource(byte opcode) {

		switch (opcode & 0xFF) {
			case 0x7F: return RegisterAddressingMode.A;
			case 0x78: return RegisterAddressingMode.B;
			case 0x79: return RegisterAddressingMode.C;
			case 0x7A: return RegisterAddressingMode.D;
			case 0x7B: return RegisterAddressingMode.E;
			case 0x7C: return RegisterAddressingMode.H;
			case 0x7D: return RegisterAddressingMode.L;
			case 0x7E: return IndirectAddressMode.HL;

			case 0x40: return RegisterAddressingMode.B;
			case 0x41: return RegisterAddressingMode.C;
			case 0x42: return RegisterAddressingMode.D;
			case 0x43: return RegisterAddressingMode.E;
			case 0x44: return RegisterAddressingMode.H;
			case 0x45: return RegisterAddressingMode.L;
			case 0x46: return IndirectAddressMode.HL;
			
			case 0x48: return RegisterAddressingMode.B;
			case 0x49: return RegisterAddressingMode.C;
			case 0x4A: return RegisterAddressingMode.D;
			case 0x4B: return RegisterAddressingMode.E;
			case 0x4C: return RegisterAddressingMode.H;
			case 0x4D: return RegisterAddressingMode.L;
			case 0x4E: return IndirectAddressMode.HL;

			case 0x50: return RegisterAddressingMode.B;
			case 0x51: return RegisterAddressingMode.C;
			case 0x52: return RegisterAddressingMode.D;
			case 0x53: return RegisterAddressingMode.E;
			case 0x54: return RegisterAddressingMode.H;
			case 0x55: return RegisterAddressingMode.L;
			case 0x56: return IndirectAddressMode.HL;

			case 0x58: return RegisterAddressingMode.B;
			case 0x59: return RegisterAddressingMode.C;
			case 0x5A: return RegisterAddressingMode.D;
			case 0x5B: return RegisterAddressingMode.E;
			case 0x5C: return RegisterAddressingMode.H;
			case 0x5D: return RegisterAddressingMode.L;
			case 0x5E: return IndirectAddressMode.HL;

			case 0x60: return RegisterAddressingMode.B;
			case 0x61: return RegisterAddressingMode.C;
			case 0x62: return RegisterAddressingMode.D;
			case 0x63: return RegisterAddressingMode.E;
			case 0x64: return RegisterAddressingMode.H;
			case 0x65: return RegisterAddressingMode.L;
			case 0x66: return IndirectAddressMode.HL;

			case 0x68: return RegisterAddressingMode.B;
			case 0x69: return RegisterAddressingMode.C;
			case 0x6A: return RegisterAddressingMode.D;
			case 0x6B: return RegisterAddressingMode.E;
			case 0x6C: return RegisterAddressingMode.H;
			case 0x6D: return RegisterAddressingMode.L;
			case 0x6E: return IndirectAddressMode.HL;
			
			case 0x70: return RegisterAddressingMode.B;
			case 0x71: return RegisterAddressingMode.C;
			case 0x72: return RegisterAddressingMode.D;
			case 0x73: return RegisterAddressingMode.E;
			case 0x74: return RegisterAddressingMode.H;
			case 0x75: return RegisterAddressingMode.L;
			
			case 0x36: return ImmediateAddressMode.INSTANCE;
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
			case 0x7E: return RegisterAddressingMode.A;
	
			case 0x40: 
			case 0x41: 
			case 0x42: 
			case 0x43: 
			case 0x44: 
			case 0x45: 
			case 0x46: return RegisterAddressingMode.B;
			
			case 0x48: 
			case 0x49: 
			case 0x4A: 
			case 0x4B: 
			case 0x4C: 
			case 0x4D: 
			case 0x4E: return RegisterAddressingMode.C;
	
			case 0x50: 
			case 0x51: 
			case 0x52:
			case 0x53: 
			case 0x54: 
			case 0x55: 
			case 0x56: return RegisterAddressingMode.D;
	
			case 0x58:
			case 0x59: 
			case 0x5A:
			case 0x5B: 
			case 0x5C: 
			case 0x5D: 
			case 0x5E: return RegisterAddressingMode.E;
	
			case 0x60: 
			case 0x61: 
			case 0x62: 
			case 0x63: 
			case 0x64: 
			case 0x65: 
			case 0x66: return RegisterAddressingMode.H;
	
			case 0x68: 
			case 0x69: 
			case 0x6A: 
			case 0x6B: 
			case 0x6C: 
			case 0x6D: 
			case 0x6E: return RegisterAddressingMode.L;
			
			case 0x70: 
			case 0x71: 
			case 0x72: 
			case 0x73: 
			case 0x74: 
			case 0x75: 
			case 0x36: return IndirectAddressMode.HL;
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
