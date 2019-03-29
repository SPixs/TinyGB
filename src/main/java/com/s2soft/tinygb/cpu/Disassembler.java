package com.s2soft.tinygb.cpu;

import java.io.File;
import java.io.IOException;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.mmu.GBMemory;


public class Disassembler {

	private GBCpu m_cpu;
	private GBMemory m_memory;

	public Disassembler(GameBoy gameBoy) {
		m_cpu = gameBoy.getCpu();
		m_memory = gameBoy.getMemory();
	}

	public void disassemble(short startAddress, short endAddress) {
		while (startAddress <= endAddress) {
			byte opcode = m_memory.getByte(startAddress);
			
			Instruction instruction = null;
			String line = "$"+shortToHex(startAddress);

			if (opcode == (byte)0xCB) {
				line += " CB";
				startAddress++;
				opcode = m_memory.getByte(startAddress);
				instruction = m_cpu.getExtraIntruction(opcode);
			}			
			else {
				instruction = m_cpu.getIntruction(opcode);
			}
			
			int count = 1;
			if (instruction != null) { 
				count = instruction.getLengthInBytes(opcode);
			}
			for (int i=0;i<count;i++) {
				line += (" " + byteToHex(m_memory.getByte(startAddress+i)));
			};
			while (line.length() < 16) { line += " "; }
			System.out.println(line + (instruction == null ? "???" : instruction.disassemble(m_memory, startAddress)));
			startAddress += count;
		}
	}

	public static String shortToHex(int value) {
		String result = Integer.toHexString(value & 0x0FFFF).toUpperCase();
		while (result.length() < 4) {
			result = '0' + result;
		}
		return result;
	}

	public static String byteToHex(byte value) {
		String result = Integer.toHexString(value & 0x0FF).toUpperCase();
		while (result.length() < 2) {
			result = '0' + result;
		}
		return result;
	}

//	/**
//	 * @param args
//	 * @throws IOException 
//	 */
//	public static void main(String[] args) throws IOException {
//		GBMemory memory = new GBMemory();
//		memory.loadFromFile(new File("roms/blinky.ch8"), (short) 0x200);
//		Disassembler disassembler = new Disassembler(memory);
//		disassembler.disassemble((short)0x0200, (short)0x0FFF);
//	}
}
