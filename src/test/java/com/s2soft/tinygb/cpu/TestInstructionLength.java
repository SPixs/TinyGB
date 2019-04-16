package com.s2soft.tinygb.cpu;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.IConfiguration;
import com.s2soft.tinygb.audio.IAudioDevice;
import com.s2soft.tinygb.audio.NullAudioDevice;
import com.s2soft.tinygb.control.IJoypad;
import com.s2soft.tinygb.control.IJoypadButtonListener;
import com.s2soft.tinygb.display.IDisplay;
import com.s2soft.tinygb.display.NullDisplay;

import junit.framework.TestCase;


public class TestInstructionLength extends TestCase {

	// Instruction lengths of opcodes.
	// 0 for instructions not timed.
	private byte[][] length = new byte[][] {
			 { 1,3,1,1,1,1,2,1,3,1,1,1,1,1,2,1 }, // 0
			 { 0,3,1,1,1,1,2,1,2,1,1,1,1,1,2,1 }, // 1
			 { 2,3,1,1,1,1,2,1,2,1,1,1,1,1,2,1 }, // 2
		     { 2,3,1,1,1,1,2,1,2,1,1,1,1,1,2,1 }, // 3
		     { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, // 4
		     { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, // 5
		     { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, // 6
		     { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, // 7
		     { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, // 8
		     { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, // 9
		     { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, // A
		     { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }, // B
		     { 1,1,3,3,3,1,2,1,1,1,3,0,3,3,2,1 }, // C
		     { 1,1,3,0,3,1,2,1,1,1,3,0,3,0,2,1 }, // D
		     { 2,1,1,0,0,1,2,1,2,1,3,0,0,0,2,1 }, // E
		     { 2,1,1,1,0,1,2,1,2,1,3,1,0,0,2,1 }  // F
	};
	
	private byte[][] cyclesNoBranchTaken = new byte[][] {
			{ 1,3,2,2,1,1,2,1,5,2,2,2,1,1,2,1 }, // 0
		    { 0,3,2,2,1,1,2,1,3,2,2,2,1,1,2,1 }, // 1
		    { 2,3,2,2,1,1,2,1,2,2,2,2,1,1,2,1 }, // 2
		    { 2,3,2,2,3,3,3,1,2,2,2,2,1,1,2,1 }, // 3
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 4
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 5
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 6
		    { 2,2,2,2,2,2,0,2,1,1,1,1,1,1,2,1 }, // 7
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 8
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 9
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // A
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // B
		    { 2,3,3,4,3,4,2,4,2,4,3,0,3,6,2,4 }, // C
		    { 2,3,3,0,3,4,2,4,2,4,3,0,3,0,2,4 }, // D
		    { 3,3,2,0,0,4,2,4,4,1,4,0,0,0,2,4 }, // E
		    { 3,3,2,1,0,4,2,4,3,2,4,1,0,0,2,4 }  // F
	};
	
	private byte[][] cyclesBranchTaken = new byte[][] {
			{ 1,3,2,2,1,1,2,1,5,2,2,2,1,1,2,1 }, // 0
		    { 0,3,2,2,1,1,2,1,3,2,2,2,1,1,2,1 }, // 1
		    { 3,3,2,2,1,1,2,1,3,2,2,2,1,1,2,1 }, // 2
		    { 3,3,2,2,3,3,3,1,3,2,2,2,1,1,2,1 }, // 3
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 4
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 5
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 6
		    { 2,2,2,2,2,2,0,2,1,1,1,1,1,1,2,1 }, // 7
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 8
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // 9
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // A
		    { 1,1,1,1,1,1,2,1,1,1,1,1,1,1,2,1 }, // B
		    { 5,3,4,4,6,4,2,4,5,4,4,0,6,6,2,4 }, // C
		    { 5,3,4,0,6,4,2,4,5,4,4,0,6,0,2,4 }, // D
		    { 3,3,2,0,0,4,2,4,4,1,4,0,0,0,2,4 }, // E
		    { 3,3,2,1,0,4,2,4,3,2,4,1,0,0,2,4 }  // F
	};
	
	private byte[][] cyclesExtraOpcode = new byte[][] {
			{ 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // 0
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // 1
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // 2	
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // 3
		    { 2,2,2,2,2,2,3,2,2,2,2,2,2,2,3,2 }, // 4
		    { 2,2,2,2,2,2,3,2,2,2,2,2,2,2,3,2 }, // 5
		    { 2,2,2,2,2,2,3,2,2,2,2,2,2,2,3,2 }, // 6
		    { 2,2,2,2,2,2,3,2,2,2,2,2,2,2,3,2 }, // 7
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // 8
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // 9
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // A
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // B
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // C
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // D
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // E
		    { 2,2,2,2,2,2,4,2,2,2,2,2,2,2,4,2 }, // F
	};
	
	
	public void testLength() {
		
		GameBoy gameboy = new GameBoy(getNullConfiguration(), getNullDisplay(), getNullAudioDevice(), getNullJoypad());
		GBCpu cpu = new GBCpu(gameboy);
		
		for (int i=0;i<0x100;i++) {
			byte opcode = (byte)(i & 0xFF);
			Instruction intruction = cpu.getIntruction(opcode);
			byte expectedLength = length[(opcode & 0xF0) >> 4][(opcode & 0x0F)];
			assertTrue("Instruction not implemented for opcode " +  Instruction.toHexByte(opcode), expectedLength == 0 || intruction != null);
			assertTrue("Invalid opcode " + Instruction.toHexByte(opcode) + " must have a length of 0", (expectedLength == 0 && intruction == null) || (expectedLength > 0));
			
			if (intruction != null) {
				assertEquals("Invalid length for opcode " + Instruction.toHexByte(opcode) + ", instruction " + 
						intruction.getClass().getName(), expectedLength, intruction.getLengthInBytes(opcode));
			}
		}
	}
	
	public void testCyclesNoBranchTaken() {
		testCycles(false);
	}
	
	public void testCyclesBranchTaken() {
		testCycles(true);
	}
	
	public void testCycles(boolean takeBranch) {
		GameBoy gameboy = new GameBoy(getNullConfiguration(), getNullDisplay(), getNullAudioDevice(), getNullJoypad());
		GBCpu cpu = new GBCpu(gameboy);
		
		for (int i=0;i<0x100;i++) {
			byte opcode = (byte)(i & 0xFF);

			byte[][] cyclesArray = takeBranch ? cyclesBranchTaken : cyclesNoBranchTaken;
			int expectedCycles = 4 * cyclesArray[(opcode & 0xF0) >> 4][(opcode & 0x0F)];

			Instruction intruction = cpu.getIntruction(opcode);
			if (intruction != null && expectedCycles != 0) {
				cpu.reset();
				cpu.setSp(0xFFFE);
				// status flag = 00 if lower nible of opcode >= 8 (reverse if take branch)
				// status flag = F0 if upper nible of opcode < 8 (reverse if take branch)
				cpu.setF((byte) ((((opcode & 0x08) != 0) ^ takeBranch) ? 0x00 : 0xF0));
				cpu.getMemory().setByte(0xC000, opcode);
				cpu.setPC(0xC000);
				
				assertEquals("CPU start cycle must be 0", 0, cpu.getCyclesCount());
				
				cpu.step();
				
				assertEquals("Incorrect cycles count for opcode " + Instruction.toHexByte(opcode) + ", instruction " + 
						intruction.getClass().getName(), expectedCycles, cpu.getCyclesCount());
			}
		}
	}

	public void testLengthCB() {
		
		GameBoy gameboy = new GameBoy(getNullConfiguration(), getNullDisplay(), getNullAudioDevice(), getNullJoypad());
		GBCpu cpu = new GBCpu(gameboy);
		
		for (int i=0;i<0x100;i++) {
			byte opcode = (byte)(i & 0xFF);
			Instruction intruction = cpu.getExtraIntruction(opcode);
			byte expectedLength = 1;
			assertTrue("Extra instruction (prefix CB) not implemented for opcode " +  Instruction.toHexByte(opcode), expectedLength == 0 || intruction != null);
			
			if (intruction != null) {
				assertEquals("Invalid length for extra intruction opcode " + Instruction.toHexByte(opcode) + ", instruction " + 
						intruction.getClass().getName(), expectedLength, intruction.getLengthInBytes(opcode));
			}
		}
	}
	
	public void testCyclesCB() {
		GameBoy gameboy = new GameBoy(getNullConfiguration(), getNullDisplay(), getNullAudioDevice(), getNullJoypad());
		GBCpu cpu = new GBCpu(gameboy);
		
		for (int i=0;i<0x100;i++) {
			byte opcode = (byte)(i & 0xFF);

			int expectedCycles = 4 * cyclesExtraOpcode[(opcode & 0xF0) >> 4][(opcode & 0x0F)];

			Instruction intruction = cpu.getExtraIntruction(opcode);
			if (intruction != null && expectedCycles != 0) {

				// Test with flags clear
				cpu.reset();
				cpu.setSp(0xFFFE);
				cpu.setF((byte) 0x00);
				cpu.getMemory().setByte(0xC000, (byte)0xCB);
				cpu.getMemory().setByte(0xC001, opcode);
				cpu.setPC(0xC000);
				
				assertEquals("CPU start cycle must be 0", 0, cpu.getCyclesCount());
				
				cpu.step();
				
				assertEquals("Incorrect cycles count for extra opcode " + Instruction.toHexByte(opcode) + " with flags clears, instruction " + 
						intruction.getClass().getName(), expectedCycles, cpu.getCyclesCount());

				// Test with flags set
				cpu.reset();
				cpu.setSp(0xFFFE);
				cpu.setF((byte) 0xF0);
				cpu.getMemory().setByte(0xC000, (byte)0xCB);
				cpu.getMemory().setByte(0xC001, opcode);
				cpu.setPC(0xC000);
				
				assertEquals("CPU start cycle must be 0", 0, cpu.getCyclesCount());

				cpu.step();
				
				assertEquals("Incorrect cycles count for extra opcode " + Instruction.toHexByte(opcode) + " with flags clears, instruction " + 
						intruction.getClass().getName(), expectedCycles, cpu.getCyclesCount());
			}
		}
	}


	private IJoypad getNullJoypad() {
		return new IJoypad() {
			@Override
			public void addButtonListener(IJoypadButtonListener listener) {
			}
		};
	}

	private IAudioDevice getNullAudioDevice() {
		return new NullAudioDevice();
	}

	private IDisplay getNullDisplay() {
		return new NullDisplay();
	}

	private IConfiguration getNullConfiguration() {
		return new IConfiguration() {
			
			@Override
			public boolean useBootRom() {
				return false;
			}
		};
	}
	
}
