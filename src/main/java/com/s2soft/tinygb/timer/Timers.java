package com.s2soft.tinygb.timer;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.mmu.GBMemory;

public class Timers {

	//   ============================ Constants ==============================
	
	private final static boolean TRACE = false;
	
	//	 =========================== Attributes ==============================
	
	private byte m_dividerRegister = 0;
	private byte m_timerRegister = 0;
	private byte m_timerModulo = 0;
	
	/**
	 * The divider register is incremented at rate of 16384Hz.
	 * To achieve this, we divide the T-clock by 1024.
	 */
	private int m_timerCounter = 0x3FF; // 1023
	
	/**
	 * The divider register is incremented at rate of 16384Hz.
	 * To achieve this, we divide the T-clock by 256.
	 */
	private int m_dividerCounter = 0xFF;
	
	private boolean m_timerEnabled = true;
	private byte m_clockSelect;

	private GBMemory m_memory;
	
	//	 =========================== Constructor =============================

	public Timers(GameBoy gameBoy) {
		m_memory = gameBoy.getMemory();
	}

	//	 ========================== Access methods ===========================
		
	public void setTimerEnabled(boolean enabled) {
		if (TRACE) {
			System.out.println("Setting timer enabled : " + enabled);
		}
		m_timerEnabled = enabled;
	}
	
	public boolean isTimerEnabled() {
		return m_timerEnabled;
	}

	public byte getTimerModulo() {
		return m_timerModulo;
	}

	public void setTimerModulo(byte timerModulo) {
		if (TRACE) {
			System.out.println("Setting timer modulo : " + timerModulo);
		}
		m_timerModulo = timerModulo;
	}

	public byte getTimerRegister() {
		if (TRACE) {
			System.out.println("Reading from timer : " + Instruction.toHexByte(m_timerRegister));
		}
		return m_timerRegister;
	}

	public void setTimerRegister(byte timerRegister) {
		if (TRACE) {
			System.out.println("Writing to timer : " + timerRegister);
		}
		m_timerRegister = timerRegister;
	}

	public byte getDividerRegister() {
		if (TRACE) {
			System.out.println("Reading divider register : " + Instruction.toHexByte(m_dividerRegister));
		}
		return m_dividerRegister;
	}

	//	 ========================= Treatment methods =========================
	
	/**
	 * Called at T-clock frequency ((4.194304Mhz)
	 */
	public void step() {
		if (m_dividerCounter-- == 0) {
			m_dividerCounter = 255;
			m_dividerRegister++;
		}
		
		if (m_timerEnabled) {
			if (m_timerCounter-- == 0) {
				resetTimerCounter();
				m_timerRegister++;
				if (m_timerRegister == 0) {
					m_memory.requestInterrupt(2);
					m_timerRegister = m_timerModulo;
				}
			}
		}
	}
	
	public void reset() {
		m_clockSelect = 0;
		resetTimerCounter();
		m_timerCounter = 1024;
		m_dividerCounter = 255;
	}

	public void resetDividerRegister() {
		if (TRACE) {
			System.out.println("Reset divider register");
		}
		m_dividerRegister = 0;
	}
	
	/**
	 * 2-bits encoded clock value for timer :
	 * 
	 * 00: CPU Clock / 1024 (DMG, CGB:   4096 Hz, SGB:   ~4194 Hz)
	 * 01: CPU Clock / 16   (DMG, CGB: 262144 Hz, SGB: ~268400 Hz)
	 * 10: CPU Clock / 64   (DMG, CGB:  65536 Hz, SGB:  ~67110 Hz)
	 * 11: CPU Clock / 256  (DMG, CGB:  16384 Hz, SGB:  ~16780 Hz)
	 * 
	 * @param clockSelect
	 */
	public void setInputClock(byte clockSelect) {
		if (TRACE) {
			System.out.println("Setting input clock : " + Instruction.toHexByte(clockSelect));
		}
		m_clockSelect = clockSelect;
		resetTimerCounter();
	}
	
	public byte getInputClock() {
		return m_clockSelect;
	}

	private void resetTimerCounter() {
		final int[] counter = new int[] { 0x3FF, 0x0F, 0x3F, 0xFF };
		m_timerCounter = counter[m_clockSelect];
	}
}

