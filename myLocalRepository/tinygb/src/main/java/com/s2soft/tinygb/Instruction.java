package com.s2soft.tinygb;

import com.s2soft.utils.StringUtils;

public abstract class Instruction {

	public abstract boolean matchOpcode(byte opcode);
	
	public char getOpcodeNibbleAt(int index, byte opcode) {
		return Integer.toHexString(((opcode >> (index * 4)) & 0x0F)).toUpperCase().charAt(0);
	}

	public int getNibbleValueAt(int index, byte opcode) {
		return ((opcode >> (index * 4)) & 0x0F);
	}
	
//	public String getOpcodeNibblesAt(int startIndex, int endIndex, byte opcode) {
//		short[] mask = new short[] { (short)0xFFFF, 0x0FFF, 0x00FF, 0x000F };
//		opcode = (short)(opcode >> (startIndex * 4));
//		opcode = (short)(opcode & mask[endIndex-startIndex]);
//		return Integer.toHexString(opcode).toUpperCase();
//	}
	
	public abstract String disassemble(GBMemory memory, int address);
	
	public static String toHexShort(int i) {
		return toHexShort(i, true);
	}

	public static String toHexShort(int i, boolean padding) {
		String result = Integer.toHexString(i & 0x0000FFFF).toUpperCase();
		return "$" + (padding ? StringUtils.padLeft(result, '0', 4) : result);
	}

	public static String toHexByte(byte value) {
		return toHexByte(value, true);
	}
	
	public static String toHexByte(int value, boolean padding) {
		String result = Integer.toHexString(value & 0x00FF).toUpperCase();
		return "$" + (padding ? StringUtils.padLeft(result, '0', 2) : result);
	}
	
//	public static String toHexByteSigned(int value, boolean padding) {
//		String result = Integer.toHexString(value & 0x00FF).toUpperCase();
//		result = "$" + (padding ? StringUtils.padLeft(result, '0', 2) : result);
//		result
//	}

	/**
	 * @param opcode
	 * @param cpu
	 * @param additionalBytes 
	 * @return the number of execution CPU cycles
	 */
	public abstract int execute(byte opcode, GBCpu cpu, byte[] additionalBytes);

	public abstract int getLengthInBytes(byte opcode);
	
}
