package com.s2soft.tinygb;
import java.util.ArrayList;


public class GBCpu {

	private static final boolean TRACE = true;

	private ArrayList<Instruction> m_instructions = new ArrayList<Instruction>();
	private ArrayList<Instruction> m_extraInstructions = new ArrayList<Instruction>();
	
	private GBMemory m_memory = new GBMemory();
	
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

	public GBCpu() {
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
	}

	private void initInstructions() {
		m_instructions.add(new InstrLDImm());
		m_instructions.add(new InstrLDImm16());
		m_instructions.add(new InstrXOR());
		m_instructions.add(new InstrLDD());
		m_instructions.add(new InstrLDI());
		m_instructions.add(new InstrJR());
		m_instructions.add(new InstrJRCond());
		m_instructions.add(new InstrLDAN());
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
	
	public void step() {
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

		String disassembledLine = null;
		if (TRACE) {
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
		
		int executionCycles = instruction.execute(opcode, this, additionalBytes);
		m_cyclesCount += executionCycles;
	}
	
	public void run() {

		m_running = true;
		resetCyclesCounter();
		// CPU is running at 4.194304Mhz (Clock cycles) or 1.048576Mhz (Machine cycles) 
		// Cpu and instruction speed are described in machine cycles
		double cpuClock = 4.194304e6 / 4.0;
		
		m_runningThread = Thread.currentThread();
		while (true && m_running && !m_runningThread.isInterrupted()) {
			step();
			
			if (m_cyclesCount % 1000 == 0) {
				long elapsed = System.currentTimeMillis() - m_runStartTimer;
				
				// Clock speed on DMG GB is 4.19430Mhz
				long wait = (long) (m_cyclesCount * (1000.0/cpuClock) - elapsed);
//				System.out.println(wait);
				if (wait > 0) {
					try { Thread.sleep(wait); } 	
					catch (InterruptedException e) {
						m_runningThread.interrupt();
					}
				}
			}
		}
		
		synchronized (this) {
			m_running = false;
			notify();
		}
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
