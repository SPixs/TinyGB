package com.s2soft.utils;

public class StringUtils {

	// ============================ Constants ==============================

	// =========================== Attributes ==============================

	// =========================== Constructor =============================

	// ========================== Access methods ===========================

	// ========================= Treatment methods =========================

	public static String padLeft(String string, char paddingChar, int size) {
		return String.format("%"+size+"s", string).replace(' ', paddingChar);
		
	}

	public static String padRight(String string, char paddingChar, int size) {
		return String.format("%-"+size+"s", string).replace(' ', paddingChar);
	}
}
