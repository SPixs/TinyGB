package com.s2soft.tinygb.cpu;
import java.util.ArrayList;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.mmu.GBMemory;


public class GBCpu {

	private static final boolean TRACE = false;

	private ArrayList<Instruction> m_instructions = new ArrayList<Instruction>();
	private ArrayList<Instruction> m_extraInstructions = new ArrayList<Instruction>();
	
	private GBMemory m_memory;
	
	// registers
	byte m_a;
	byte m_b;
	byte m_c;
	byte m_d;
	byte m_e;
	byte m_h;
	byte m_l;
	byte m_f;
	
	/**
	 * Programm counter register
	 */
	int m_pc;
	
	/**
	 * Stack pointer
	 */
	int m_sp;

	private long m_runStartTimer;

	private long m_cyclesCount;

	private boolean m_running;

	private Thread m_runningThread;

	public GBCpu(GameBoy gameBoy) {
		m_memory = gameBoy.getMemory();
		initInstructions();
		reset();
	}
	
	// ================================ access methods ==================================
	
	public byte getA() {
		return m_a;
	}

	public void setA(byte a) {
		m_a = a;
	}

	public byte getB() {
		return m_b;
	}

	public void setB(byte b) {
		m_b = b;
	}

	public byte getC() {
		return m_c;
	}

	public void setC(byte c) {
		m_c = c;
	}

	public byte getD() {
		return m_d;
	}

	public void setD(byte d) {
		m_d = d;
	}

	public byte getE() {
		return m_e;
	}

	public void setE(byte e) {
		m_e = e;
	}

	public byte getH() {
		return m_h;
	}

	public void setH(byte h) {
		m_h = h;
	}

	public byte getL() {
		return m_l;
	}

	public void setL(byte l) {
		m_l = l;
	}

	public byte getF() {
		return m_f;
	}

	public void setF(byte f) {
		m_f = f;
	}

	public int getPc() {
		return m_pc;
	}

	public void setPc(int pc) {
		m_pc = pc;
	}

	public int getSp() {
		return m_sp & 0x0FFFF;
	}

	public void setSp(int sp) {
		m_sp = sp & 0x0FFFF;
	}
	
	// ============================== threatment methods ================================
	
	public void reset() {
		m_sp = 0; // fix me !
		m_pc = 0;
		resetCyclesCounter();
	}

	private void initInstructions() {
		m_instructions.add(new InstrLDImm());
		m_instructions.add(new InstrLDImm16());
		m_instructions.add(new InstrXOR());
		m_instructions.add(new InstrLDD());
		m_instructions.add(new InstrLDI());
		m_instructions.add(new InstrJR());
		m_instructions.add(new InstrJRCond());
		m_instructions.add(new InstrJPImm());
		m_instructions.add(new InstrLDrr());
		m_instructions.add(new InstrLDAn());
		m_instructions.add(new InstrLDNA());
		m_instructions.add(new InstrLDAC());
		m_instructions.add(new InstrLDCA());
		m_instructions.add(new InstrINC());
		m_instructions.add(new InstrDEC());
		m_instructions.add(new InstrINCnn());
		m_instructions.add(new InstrLDHnA());
		m_instructions.add(new InstrLDHAn());
		m_instructions.add(new InstrCALL());
		m_instructions.add(new InstrPUSH());
		m_instructions.add(new InstrPOP());
		m_instructions.add(new InstrRET());
		m_instructions.add(new InstrCP());
		m_instructions.add(new InstrSUB());
		m_instructions.add(new InstrADD());
		m_instructions.add(new InstrRLA());
		m_instructions.add(new InstrNOP());
		m_instructions.add(new InstrAND());
		m_instructions.add(new InstrDAA());
		m_instructions.add(new InstrADCAn());
		m_instructions.add(new InstrRETcc());
		m_instructions.add(new InstrDI());
		m_instructions.add(new InstrEI());
		
		m_extraInstructions.add(new InstrBit());
		m_extraInstructions.add(new InstrRL());
		
//		m_instructions.add(new InstrRET());
//		m_instructions.add(new InstrSYS());
//		m_instructions.add(new InstrJP());
//		m_instructions.add(new InstrCall());
//		m_instructions.add(new InstrSEImm());
//		m_instructions.add(new InstrSNEImm());
//		m_instructions.add(new InstrSEReg());
//		m_instructions.add(new InstrLDImm());
//		m_instructions.add(new InstrADDImm());
//		m_instructions.add(new InstrLDReg());
//		m_instructions.add(new InstrORReg());
//		m_instructions.add(new InstrAndReg());
//		m_instructions.add(new InstrXORReg());
//		m_instructions.add(new InstrADDReg());
//		m_instructions.add(new InstrSUBReg());
//		m_instructions.add(new InstrSHR());
//		m_instructions.add(new InstrSUBNReg());
//		m_instructions.add(new InstrSHL());
//		m_instructions.add(new InstrSNEReg());
//		m_instructions.add(new InstrLDI());
//		m_instructions.add(new InstrJPIndexed());
//		m_instructions.add(new InstrRND());
//		m_instructions.add(new InstrDRW());
//		m_instructions.add(new InstrSKP());
//		m_instructions.add(new InstrSKNP());
//		m_instructions.add(new InstrLDFromTimer());
//		m_instructions.add(new InstrLDKey());
//		m_instructions.add(new InstrLDToTimer());
//		m_instructions.add(new InstrLDToSound());
//		m_instructions.add(new InstrADDI());
//		m_instructions.add(new InstrLDISprite());
//		m_instructions.add(new InstrLDBcd());
//		m_instructions.add(new InstrLDIFromReg());
//		m_instructions.add(new InstrLDRegFromI());
	}
	
	public int step() {

		processInterrupts();
		
		if (getPC() == 0x0028) {
			Thread.yield();
		}
		
		byte opcode = getMemory().getByte(getPC());
		
		Instruction instruction = null;
		if (opcode == (byte)0xCB) {
			setPC(getPC() + 1);
			opcode = getMemory().getByte(getPC());
			instruction = getExtraIntruction(opcode);
			if (instruction == null) {
				throw new IllegalArgumentException("Illegal extra opcode : " + Instruction.toHexByte(opcode) + " at " + Instruction.toHexShort(getPC()));
			}
		}
		else {
			instruction = getIntruction(opcode);
			if (instruction == null) {
				throw new IllegalArgumentException("Illegal opcode : " + Instruction.toHexByte(opcode) + " at " + Instruction.toHexShort(getPC()));
			}
		}
		
//		if (m_cyclesCount % 1000 == 0) {
//			System.out.println("PC = " + Instruction.toHexShort(getPC()));
//		}

		int previousPC = getPC();
		
		String disassembledLine = null;
		if (TRACE)  {
			disassembledLine = ">$" + Disassembler.shortToHex(getPC()) + " " + instruction.disassemble(m_memory, getPC());
			while (disassembledLine.length() < 24) disassembledLine += " ";
			for (Register8Bits reg8 : Register8Bits.values()) {
				disassembledLine += " " + reg8.name()+"="+Instruction.toHexByte(reg8.getValue(this));
			}
			for (Register16Bits reg16 : Register16Bits.values()) {
				disassembledLine += " " + reg16.name()+"="+Instruction.toHexShort(reg16.getValue(this));
			}
			disassembledLine += " Cycles=" + m_cyclesCount;
			System.out.println(disassembledLine);
		}
		
		setPC(getPC() + 1);
		int lengthInBytes = instruction.getLengthInBytes(opcode);
		byte[] additionalBytes = new byte[lengthInBytes - 1];
		for (int i=0;i<lengthInBytes-1;i++) {
			additionalBytes[i] = getMemory().getByte(getPC());
			setPC(getPC() + 1);
		}
		
		try {
			int executionCycles = instruction.execute(opcode, this, additionalBytes);
			m_cyclesCount += executionCycles;
			return executionCycles;
		}
		catch (Exception ex) {
			System.out.println("------------------------");
			System.out.println("----- FATAL ERROR ------");
			System.out.println("- Last cpu instruction -");
			new Disassembler(this, m_memory).disassemble((short)previousPC, (short)previousPC);
			System.out.println("------------------------");
			throw ex;
		}
	}
	
	private void processInterrupts() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		synchronized (this) {
			if (m_running) {
				m_runningThread.interrupt();
				try { wait(); } 
				catch (InterruptedException e) {}
			}
		}
	}
	
	public boolean isRunning() {
		return m_running;
	}
	
	public void resetCyclesCounter() {
		m_runStartTimer = System.currentTimeMillis();
		m_cyclesCount = 0;
	}
	
	public Instruction getIntruction(byte opcode) {
		for (Instruction instruction : m_instructions) {
			if (instruction.matchOpcode(opcode)) {
				return instruction;
			}
		}
		return null;
	}
	
	public Instruction getExtraIntruction(byte opcode) {
		for (Instruction instruction : m_extraInstructions) {
			if (instruction.matchOpcode(opcode)) {
				return instruction;
			}
		}
		return null;
	}
	
	/**
	 * Push a 16bits value onto the stack
	 * 
	 * @param value
	 */
	public void pushShort(int value) {
//		throw new RuntimeException("Not implemented");
//		m_stack[--m_sp] = value & 0x0FFFF;
		setSp(getSp()-1);
		m_memory.setByte(getSp(), (byte)((value >> 8) & 0x0FF));
		setSp(getSp()-1);
		m_memory.setByte(getSp(), (byte)((value & 0x0FF)));
	}

	/**
	 * Pull a 16bits value from the stack
	 * 
	 * @return
	 */
	public int pullValue() {
		byte lsb = m_memory.getByte(getSp());
		setSp(getSp()+1);
		byte msb = m_memory.getByte(getSp());
		setSp(getSp()+1);
		return (msb << 8) | (lsb & 0x0FF);
	}
	
	/**	
	 * @return the program counter value
	 */
	public int getPC() {
		return m_pc & 0x0FFFF;
	}

	/**
	 * Set the program counter value
	 * @param pc
	 */
	public void setPC(int pc) {
		m_pc = (short) (pc & 0x0FFFF);
	}

	public GBMemory getMemory() {
		return m_memory;
	}
	
	public void setMemory(GBMemory memory) {
		m_memory = memory;
	}

	// Flag register helper methods
	
	public void setFlagZero(boolean value) {
		m_f = (byte) ((m_f & ~0x80) | (value ? 0x80 : 0));
	}

	public boolean getFlagZero() {
		return (m_f & 0x80) != 0;
	}
	
	public void setFlagSubtract(boolean value) {
		m_f = (byte) ((m_f & ~0x40) | (value ? 0x40 : 0));
	}

	public boolean getFlagSubtract() {
		return (m_f & 0x40) != 0;
	}
	
	public void setFlagHalfCarry(boolean value) {
		m_f = (byte) ((m_f & ~0x20) | (value ? 0x20 : 0));
	}

	public boolean getFlagHalfCarry() {
		return (m_f & 0x20) != 0;
	}
	
	public void setFlagCarry(boolean value) {
		m_f = (byte) ((m_f & ~0x10) | (value ? 0x10 : 0));
	}
	
	public boolean getFlagCarry() {
		return (m_f & 0x10) != 0;
	}
}
