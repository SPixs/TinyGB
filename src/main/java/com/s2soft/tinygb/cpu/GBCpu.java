package com.s2soft.tinygb.cpu;
import java.util.ArrayList;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;


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
	
	/**
	 * The master interrupt enabled flag
	 */
	private boolean m_ime = true;

	private long m_runStartTimer;

	private long m_cyclesCount;

	private boolean m_running;

	private Thread m_runningThread;

	/**
	 * Counter to delay the master interrupt enable flag modification
	 */
	private int m_delayedInterruptCount = -1;
	
	/**
	 * Delayed state of the master interrupt enable flag
	 */
	private boolean m_delayedInterruptState = false;
	
	private boolean m_halted;
	
	private boolean startTrace = false;

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
	
	public boolean isInterruptEnabled() {
		return m_ime;
	}

	public void setInterruptEnabled(boolean enabled, boolean delay) {
		if (delay) {
			m_delayedInterruptCount = 1;
			m_delayedInterruptState = enabled;
		}
		else {
			m_ime = enabled;
		}
	}

	// ============================== threatment methods ================================
	
	public void reset() {
		m_sp = 0; // fix me !
		m_pc = 0;
		m_ime = true;
		m_halted = false;
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
		m_instructions.add(new InstrDECnn());
		m_instructions.add(new InstrINCnn());
		m_instructions.add(new InstrLDHnA());
		m_instructions.add(new InstrLDHAn());
		m_instructions.add(new InstrCALL());
		m_instructions.add(new InstrPUSH());
		m_instructions.add(new InstrPOP());
		m_instructions.add(new InstrRET());
		m_instructions.add(new InstrRETI());
		m_instructions.add(new InstrCP());
		m_instructions.add(new InstrSUB());
		m_instructions.add(new InstrSBC());
		m_instructions.add(new InstrADD());
		m_instructions.add(new InstrADDAn());
		m_instructions.add(new InstrRLA());
		m_instructions.add(new InstrRLCA());
		m_instructions.add(new InstrNOP());
		m_instructions.add(new InstrAND());
		m_instructions.add(new InstrOR());
		m_instructions.add(new InstrDAA());
		m_instructions.add(new InstrCPL());
		m_instructions.add(new InstrADCAn());
		m_instructions.add(new InstrRETcc());
		m_instructions.add(new InstrRETcc());
		m_instructions.add(new InstrEI());
		m_instructions.add(new InstrDI());
		m_instructions.add(new InstrADDHL());
		m_instructions.add(new InstrRST());
		m_instructions.add(new InstrJPHL());
		m_instructions.add(new InstrJPCond());
		m_instructions.add(new InstrHALT());
		
		m_extraInstructions.add(new InstrBIT());
		m_extraInstructions.add(new InstrRES());
		m_extraInstructions.add(new InstrSET());
		m_extraInstructions.add(new InstrRL());
		m_extraInstructions.add(new InstrRR());
		m_extraInstructions.add(new InstrSRL());
		m_extraInstructions.add(new InstrSLA());
		m_extraInstructions.add(new InstrSRA());
		m_extraInstructions.add(new InstrSWAP());
		
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
	
	public void halt() {
		m_halted = true;
	}
	
	public int step() {
		
		if (m_halted & !getMemory().isInterruptRequested()) {
			return 1;
		}

		m_halted = false;
		
		processInterrupts();
		
		startTrace = !m_memory.isBootROMLock() && getPC() >= 0x1A05 && getPC() <= 0x1A06;
//		startTrace = true;
		
//		if (getPC() == 0x0028) {
//			System.out.println(">>> Tetris main loop");
//		}
//		if (getPC() == 0x0033) {
//			System.out.println(">>> Tetris executing machine state at " + Instruction.toHexShort(Register16Bits.HL.getValue(this)));
//		}
		
		// Check for interrupt
		if (isInterruptEnabled() && getMemory().isInterruptRequested()) {
			// keep interrupts that are not masked
			byte interruptsToHandle = (byte) (getMemory().getInterruptEnable() &  getMemory().getInterruptFlag());
			
			// For the moment, only handle VBL interrupts
			for (int i=0;i<=4;i++) {
				if (BitUtils.isSet(interruptsToHandle, i)) {
					// Clear this interrupt flag
					getMemory().setInterruptFlag(BitUtils.setBit(getMemory().getInterruptFlag(), i, false));
					
					// Disable further interrupts
		    	    setInterruptEnabled(false, false);
		    	    pushShort(getPC());
		    	    setPC(0x0040+i*8);
		    	    return 12; // 12 cycles to process interrupt flag
				}
			}
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
//		startTrace = getPc() == 0x0226;
		if (TRACE || startTrace)  {
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
			Thread.yield();
		}
		
		setPC(getPC() + 1);
		int lengthInBytes = instruction.getLengthInBytes(opcode);
		byte[] additionalBytes = new byte[lengthInBytes - 1];
		for (int i=0;i<lengthInBytes-1;i++) {
			additionalBytes[i] = getMemory().getByte(getPC());
			setPC(getPC() + 1);
		}
		
		int executionCycles = 0;
		
		try {
			executionCycles = instruction.execute(opcode, this, additionalBytes);
			m_cyclesCount += executionCycles;
			if (instruction instanceof InstrRETI) {
				Thread.yield();
			}
		}
		catch (Exception ex) {
			System.out.println("------------------------");
			System.out.println("----- FATAL ERROR ------");
			System.out.println("- Last cpu instruction -");
			new Disassembler(this, m_memory).disassemble((short)previousPC, (short)previousPC);
			System.out.println("------------------------");
			throw ex;
		}

		if (m_delayedInterruptCount >= 0 && m_delayedInterruptCount-- == 0) {
			setInterruptEnabled(m_delayedInterruptState, false);
		}
		
		return executionCycles;
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
	
	@Override
	public String toString() {
		String cpuStatus = "PC=" + Disassembler.shortToHex(getPC()) + " ";
		for (Register8Bits reg8 : Register8Bits.values()) {
			cpuStatus += " " + reg8.name()+"="+Instruction.toHexByte(reg8.getValue(this));
		}
		for (Register16Bits reg16 : Register16Bits.values()) {
			cpuStatus += " " + reg16.name()+"="+Instruction.toHexShort(reg16.getValue(this));
		}
		cpuStatus += " Cycles=" + m_cyclesCount;
		
		return cpuStatus;
	}

}
