package com.s2soft.tinygb.timer;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class Timers {

	//   ============================ Constants ==============================
	
	private final static boolean TRACE = false;
	
//	final static int[] COUNTER_RESET_VALUES = new int[] { 0x3FF, 0x0F, 0x3F, 0xFF };
	
	private int[] MULTIPLEXER_ENTRIES = new int[] { 9, 3, 5, 7 };

	//	 =========================== Attributes ==============================
	
//	private int m_dividerRegister = 0;

	private int m_timerRegister = 0;
	private int m_timerModulo = 0;
	
//	/**
//	 * The divider register is incremented at rate of 16384Hz.
//	 * To achieve this, we divide the T-clock by 256.
//	 */
//	private int m_timerCounter = 0x00; 
	
//	/**
//	 * The divider register is incremented at rate of 16384Hz.
//	 * To achieve this, we divide the T-clock by 256.
//	 */
//	private int m_dividerCounter = 0xFF;
	
	private boolean m_timerEnabled;
	private byte m_clockSelect;

	private GBMemory m_memory;
	
	private boolean m_overflowOccured;
	private int m_cyclesSinceOverflow;
	
	// a 16 bits system counter used to store divider and clock counter 
	// (see http://gbdev.gg8.se/wiki/articles/Timer_Obscure_Behaviour)
	private short m_systemCounter = 0;

	private boolean m_previousBitState; 
	
	//	 =========================== Constructor =============================

	public Timers(GameBoy gameBoy) {
		m_memory = gameBoy.getMemory();
		reset();
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
		return (byte)(m_timerModulo & 0xFF);
	}

	public void setTimerModulo(byte timerModulo) {
		if (TRACE) {
			System.out.println("Setting timer modulo : " + timerModulo);
		}
		m_timerModulo = timerModulo;
	}

	public byte getTimerRegister() {
		if (TRACE) {
			System.out.println("Reading from timer : " + Instruction.toHexByte((byte) (m_timerRegister & 0xFF)));
			if ((m_timerRegister & 0xFF) == 0x0D) {
				Thread.yield();
			}
		}
		return (byte) (m_timerRegister & 0xFF);
	}

	public void setTimerRegister(byte timerRegister) {
		if (TRACE) {
			System.out.println("Writing to timer : " + timerRegister);
		}
		
		 if (m_cyclesSinceOverflow < 5) {
			 m_timerRegister = timerRegister;
             m_overflowOccured = false;
             m_cyclesSinceOverflow = 0;
         }
	}

	public byte getDividerRegister() {
		if (TRACE) {
			System.out.println("Reading divider register : " + Instruction.toHexByte((byte) ((m_systemCounter >> 8) & 0xFF)));
		}
		return (byte) ((m_systemCounter >> 8) & 0xFF);
	}
	
	private void setSystemCounter(short value) {
		m_systemCounter = value;
		int multiplexerBitIndex = MULTIPLEXER_ENTRIES[m_clockSelect];
		boolean bitState = BitUtils.isSet(m_systemCounter, multiplexerBitIndex);
		bitState &= isTimerEnabled();
		if (m_previousBitState && !bitState) {
			m_timerRegister = (m_timerRegister + 1) & 0xFF;
			
			// When TIMA overflows, the value from TMA is loaded and IF timer flag is set to 1, but this doesn't happen immediately.
			if (m_timerRegister == 0) {
				m_overflowOccured = true;
				m_cyclesSinceOverflow = 0;
			}
		}
		m_previousBitState = bitState;
	}

	//	 ========================= Treatment methods =========================
	
	/**
	 * Called at T-clock frequency ((4.194304Mhz)
	 */
	public void step() {
		
		setSystemCounter((short)((m_systemCounter + 1) & 0xFFFF));
		
//		if (m_dividerCounter-- == 0) {
//			m_dividerCounter = 255;
//			m_dividerRegister = (m_dividerRegister + 1) & 0xFF;
////			System.out.println(m_dividerRegister);
//		}
		
		if (m_overflowOccured) {
			m_cyclesSinceOverflow++;
			// Timer interrupt is delayed 1 cpu cycle (4 clocks) from the TIMA overflow. 
			// The TMA reload to TIMA is also delayed. For one cycle, after overflowing TIMA, the value in TIMA is 00h, not TMA.
			if (m_cyclesSinceOverflow == 4) {
				m_memory.requestInterrupt(2);
				m_timerRegister = 0;
			}
//			if (m_cyclesSinceOverflow > 4) {
//				m_timerRegister = m_timerModulo;
//				m_overflowOccured = false;
//			}
			if (m_cyclesSinceOverflow == 5) {
				m_timerRegister = m_timerModulo;
			}
			if (m_cyclesSinceOverflow == 6) {
				m_timerRegister = m_timerModulo;
				m_overflowOccured = false;
				m_cyclesSinceOverflow = 0;
			}
		}
		
//		if (m_timerEnabled) {
//			if (m_timerCounter-- == 0) {
//				resetTimerCounter();
//				m_timerRegister++;
//				
//				// When TIMA overflows, the value from TMA is loaded and IF timer flag is set to 1, but this doesn't happen immediately.
//				if ((m_timerRegister & 0xFF) == 0) {
//					m_overflowOccured = true;
//					m_cyclesSinceOverflow = 0;
//				}
//			}
//		}
	}
	
	public void reset() {
		m_timerEnabled = false;
		m_clockSelect = 0;
//		resetTimerCounter();
		m_systemCounter = 0;
		m_timerModulo = 0;
		m_overflowOccured = false;
	}

	public void resetDividerRegister() {
		if (TRACE) {
			System.out.println("Reset divider register");
		}
		setSystemCounter((short) 0);
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
//		resetTimerCounter();
	}
	
	public byte getInputClock() {
		return m_clockSelect;
	}

//	private void resetTimerCounter() {
//		m_timerCounter = COUNTER_RESET_VALUES[m_clockSelect];
//	}
}

