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

	public final static byte toByte(int value) {
		return (byte)(value & 0x0FF);
	}

	public static int toUInt(byte v) {
		return 0x00FF & v;
	}
}
