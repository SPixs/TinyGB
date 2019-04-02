package com.s2soft.tinygb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.s2soft.utils.StreamCopier;

public class Cartidge {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private byte[] m_rom;

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public void read(InputStream input) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamCopier.copy(input, outputStream);
		m_rom = outputStream.toByteArray();
	}
	
	public byte getROMByte(int address) {
		return m_rom == null ? (byte) 0xFF : m_rom[address];
	}

	public void setRAMByte(int address) {
	}

	public byte getRAMByte(int address) {
		return (byte) 0xFF;
	}
}

