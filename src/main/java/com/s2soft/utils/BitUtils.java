package com.s2soft.utils;

public class BitUtils {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	public final static boolean isSet(byte value, int index) {
		return ((value >> index) & 0x01) != 0;
	}

	public final static boolean isSet(int value, int index) {
		return ((value >> index) & 0x01) != 0;
	}

	public final static byte toByte(int value) {
		return (byte)(value & 0xFF);
	}

	public static int toUInt(byte v) {
		return 0x00FF & v;
	}

	public static byte setBit(byte status, int index, boolean value) {
		byte mask = (byte) (1 << index);
		status &= ~mask;
		if (value) status |= mask;
		return status;
	}
	
	public static int setBit(int status, int index, boolean value) {
		int mask = (1 << index);
		status &= ~mask;
		if (value) status |= mask;
		return status;
	}

	public static int toUShort(byte lsb, byte msb) {
		return (lsb & 0x00FF) | msb << 8;
	}
}

