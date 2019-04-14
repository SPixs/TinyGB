package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.mmu.GBMemory;

public class InstrLDHLSPn extends Instruction {

	@Override
	public boolean matchOpcode(byte opcode) {
		return opcode == (byte)0xF8;
	}

	@Override
	public int execute(byte opcode, GBCpu cpu, byte[] additionnalBytes) {
		byte offset = additionnalBytes[0];
		int sp = cpu.getSp();
		int effectiveAddress = sp + offset; // offset is signed
		Register16Bits.HL.setValue(cpu, effectiveAddress & 0xFFFF);

		cpu.setFlagZero(false);
		cpu.setFlagSubtract(false);
		cpu.setFlagHalfCarry((sp & 0x0F) + (offset & 0x0F) > 0x0F);
		cpu.setFlagCarry((sp & 0xFF) + (offset & 0xFF) > 0xFF);
		
		return 12; 
	}

	@Override
	public String disassemble(GBMemory memory, int address) {
		byte offset = memory.getByte(address+1);
		return "LD HL,SP+"+Instruction.toHexByte(offset);
	}

	@Override
	public int getLengthInBytes(byte opcode) {
		return 2;
	}
}
