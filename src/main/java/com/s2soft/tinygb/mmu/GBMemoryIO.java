package com.s2soft.tinygb.mmu;

import java.util.HashMap;
import java.util.Map;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.gpu.GBGPU;
import com.s2soft.utils.BitUtils;

/**
 * Memory area : $FF00 - $FF7F
 * 
 * @author smametz
 */
public class GBMemoryIO implements IAddressable {

	//   ============================ Constants ==============================
	
	public final static boolean TRACE = false;

	//	 =========================== Attributes ==============================
	
	private Map<Integer, IORegister> m_registers = new HashMap<Integer, IORegister>();
	
	/**
	 * If 0x01, then the first 256 bytes in memory are the Gameboy boot ROM.
	 * The boot ROM is a bootstrap program which is a 256 bytes big piece of code 
	 * which checks the cartridge header is correct, scrolls the Nintendo bootup 
	 * graphics and plays the "po-ling" sound.
	 */
	private byte m_bootROMLockRegister = 0;
	
	private GBMemory m_gbMemory;
	private GBGPU m_gpu;

	private GameBoy m_gameBoy;

	//	 =========================== Constructor =============================
	
	public GBMemoryIO(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
		addRegister(0xFF00, "JOYP", () -> m_gameBoy.getJoypadHandler().read() , (v) -> { m_gameBoy.getJoypadHandler().write(v); } );
		addRegister(0xFF04, "DIV", () -> m_gameBoy.getTimers().getDividerRegister() , (v) -> { m_gameBoy.getTimers().resetDividerRegister(); } );
		addRegister(0xFF05, "TIMA", () -> m_gameBoy.getTimers().getTimerRegister() , (v) -> { m_gameBoy.getTimers().setTimerRegister(v); } );
		addRegister(0xFF06, "TMA", () -> m_gameBoy.getTimers().getTimerModulo() , (v) -> { m_gameBoy.getTimers().setTimerModulo(v); } );
		addRegister(0xFF07, "TAC", () -> getTimerControl() , (v) -> { setTimerControl(v); } );
		addRegister(0xFF0F, "IF", () -> m_gbMemory.getInterruptFlag() , (v) -> { m_gbMemory.setInterruptFlag(v); } );
		addRegister(0xFF40, "LCDC", () -> m_gpu.getLCDControl() , (v) -> { m_gpu.setLCDControl(v); } );
		addRegister(0xFF41, "STAT", () -> m_gpu.getLCDStatus() , (v) -> { m_gpu.setLCDStatus(v); } );
		addRegister(0xFF42, "SCY", () -> BitUtils.toByte(m_gpu.getScrollY()) , (v) -> { m_gpu.setScrollY(BitUtils.toUInt(v)); } );
		addRegister(0xFF43, "SCX", () -> BitUtils.toByte(m_gpu.getScrollX()) , (v) -> { m_gpu.setScrollX(BitUtils.toUInt(v)); } );
		addRegister(0xFF44, "LY", () -> getLCDY() , (v) -> {} );
		addRegister(0xFF46, "DMA", () -> m_gameBoy.getDMA().getStartPageNumber() , (v) -> {  m_gameBoy.getDMA().start(v); } );
		addRegister(0xFF47, "BGP", () -> m_gpu.getBGPaletteData() , (v) -> { m_gpu.setBGPaletteData(v); });
		addRegister(0xFF48, "OBP0", () -> m_gpu.getOMAPalette1Data() , (v) -> { m_gpu.setOMAPalette1Data(v); });
		addRegister(0xFF49, "0BP1", () -> m_gpu.getOMAPalette2Data() , (v) -> { m_gpu.setOMAPalette2Data(v); });
		addRegister(0xFF50, "BOOTRomLock", () -> m_bootROMLockRegister , (v) -> { setBootRommLockRegister(v); });
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
	public void reset() {
		m_gbMemory = m_gameBoy.getMemory();
		m_gpu = m_gameBoy.getGpu();
		m_bootROMLockRegister = 0;
		m_gbMemory.setBootROMLock(true); 
	}

	private interface IByteSetter { void setValue(byte value); }
	private interface IByteGetter { byte getValue(); }
	
	private class IORegister implements IByteGetter, IByteSetter {
		private String name;
		private int address;
		private IByteSetter setter;
		private IByteGetter getter;

		private IORegister(String name, int address, IByteSetter setter, IByteGetter getter) {
			this.name = name;
			this.address = address;
			this.setter = setter;
			this.getter = getter;
		}

		public void setValue(byte value) { setter.setValue(value); }
		public byte getValue() { return getter.getValue(); }
		public String getName() { return name; }
		public int getAddress() { return address;}
	}

	private void addRegister(int address, String registerName, IByteGetter getter, IByteSetter setter) {
		m_registers.put(address, new IORegister(registerName, address, setter, getter));
	}

	/**
	 * 
	 * @return Current scan line (Read only register)
	 */
	private byte getLCDY() {
		return ((byte)(m_gpu.getScanLine() & 0x0FF));
	}

	/**
	 * Bit  2   - Timer Enable
	 * Bits 1-0 - Input Clock Select
	 * 00: CPU Clock / 1024 (DMG, CGB:   4096 Hz, SGB:   ~4194 Hz)
	 * 01: CPU Clock / 16   (DMG, CGB: 262144 Hz, SGB: ~268400 Hz)
	 * 10: CPU Clock / 64   (DMG, CGB:  65536 Hz, SGB:  ~67110 Hz)
	 * 11: CPU Clock / 256  (DMG, CGB:  16384 Hz, SGB:  ~16780 Hz)
	 * 
	 * Note: The "Timer Enable" bit only affects the timer, the divider is ALWAYS counting.
	 * 
	 * @param v
	 */
	private void setTimerControl(byte v) {
		m_gameBoy.getTimers().setTimerEnabled(BitUtils.isSet(v, 2));
		m_gameBoy.getTimers().setInputClock((byte)(v & 0x03));
	}

	private byte getTimerControl() {
		byte timerControl = m_gameBoy.getTimers().getInputClock();
		timerControl = BitUtils.setBit(timerControl, 3, m_gameBoy.getTimers().isTimerEnabled());
		return timerControl;
	}

	/**
	 * BOOT_OFF can only transition from 0b0 to 0b1, so once 0b1 has been written, the boot ROM is
	 * permanently disabled until the next system reset. Writing 0b0 when BOOT_OFF is 0b0 has no
	 * effect and doesn�t lock the boot ROM
	 * @param value
	 */
	private void setBootRommLockRegister(byte value) {
		if (m_bootROMLockRegister == 0) { 
			m_bootROMLockRegister = value; 
			if (value == 1) { m_gbMemory.setBootROMLock(false); }
		}
	}

	@Override
	public void setByte(int address, byte value) {
		IORegister ioRegister = m_registers.get(address);
		if (ioRegister != null) {
			if (TRACE) {
				System.out.println("Writing to I/O register " + ioRegister.getName()+ ", value " + Instruction.toHexByte(value) + " at " + Instruction.toHexShort(address));
			}
			ioRegister.setValue(value);
		}
		else {
			if (TRACE) {
				System.out.println("Writing to I/O Memory : " + Instruction.toHexByte(value) + " at " + Instruction.toHexShort(address));
			}
		}
	}

	@Override
	public byte getByte(int address) {
		IORegister ioRegister = m_registers.get(address);
		if (ioRegister != null) {
			if (TRACE) {
				System.out.println("Reading from I/O register " + ioRegister.getName()+ ", at " + Instruction.toHexShort(address));
			}
			return ioRegister.getValue();
		}
		else {
			if (TRACE) {
				System.out.println("Reading from I/O Memory at " + Instruction.toHexShort(address));
			}
			return 0;
		}
	}
}

