package com.s2soft.tinygb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.TreeSet;

import com.s2soft.tinygb.apu.GBAPU;
import com.s2soft.tinygb.audio.IAudioDevice;
import com.s2soft.tinygb.cartidge.Cartidge;
import com.s2soft.tinygb.control.IJoypad;
import com.s2soft.tinygb.control.JoypadHandler;
import com.s2soft.tinygb.cpu.GBCpu;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.display.IDisplay;
import com.s2soft.tinygb.dma.DMA;
import com.s2soft.tinygb.gpu.GBGPU;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.tinygb.timer.Timers;
import com.s2soft.utils.StreamCopier;

public class GameBoy {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private GBMemory m_memory;
	private GBCpu m_cpu;
	private GBGPU m_gpu;
	private long m_clockCount;
	private IDisplay m_display;
	private IAudioDevice m_audioDevice;
	private JoypadHandler m_joypadHandler;
	private Timers m_timers;
	private DMA m_dma;

	private IConfiguration m_configuration;
	private GBAPU m_apu;
	private long m_emulationSyncShift;
	
	private File m_saveFolder;
	private Cartidge m_cartidge;

	//	 =========================== Constructor =============================

	public GameBoy(IConfiguration configuration, IDisplay display, IAudioDevice audioDevice, IJoypad joypad) {
		m_display = display;
		m_audioDevice = audioDevice;
		m_memory = new GBMemory(this);
		m_gpu = new GBGPU(this);
		m_apu = new GBAPU(this);
		m_cpu = new GBCpu(this);
		m_dma = new DMA(this);
		m_configuration = configuration;
		m_joypadHandler = new JoypadHandler(this, joypad);
		m_timers = new Timers(this);
		
		m_saveFolder = new File("sav");
		if (!m_saveFolder.exists()) { m_saveFolder.mkdir(); }
		
		reset();
	}

	//	 ========================== Access methods ===========================

	public GBMemory getMemory() {
		return m_memory;
	}

	public GBCpu getCpu() {
		return m_cpu;
	}

	public GBGPU getGpu() {
		return m_gpu;
	}
	
	public GBAPU getApu() {
		return m_apu;
	}

	public long getClockCount() {
		return m_clockCount;
	}
	
	public IDisplay getDisplay() {
		return m_display;
	}
	
	public IAudioDevice getAudioDevice() {
		return m_audioDevice;
	}

	public JoypadHandler getJoypadHandler() {
		return m_joypadHandler;
	}

	public Timers getTimers() {
		return m_timers;
	}


	public DMA getDMA() {
		return m_dma;
	}
	
	private void setEmulationSyncShift(long shift) {
		if (shift < 0 && shift != m_emulationSyncShift) {
//			System.out.println("Warning : emulation too SLOW " + shift + "ms");
		}
		m_emulationSyncShift = shift;
	}

	public long getEmulationSyncShift() {
		return m_emulationSyncShift;
	}

	//	 ========================= Treatment methods =========================

	public void setCartidge(Cartidge cartidge) {
		m_cartidge = cartidge;
		m_memory.setCartidge(cartidge);
	}
	
	public void reset() {
		m_memory.reset();
		m_clockCount = 0;
		m_cpu.reset();
		m_gpu.reset();
		m_apu.stopSound();
		
		if (!m_configuration.useBootRom()) {
			m_memory.setBootROMLock(false);
			m_cpu.setPC(0x0100);
			m_gpu.setLCDEnable(true);
			m_cpu.setSp(0xFFFE);
		}
	}


	public void start() {
		
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		// CPU is running at 4.194304Mhz (Clock cycles) or 1.048576Mhz (Machine cycles) 
		// Cpu and instruction speed are described in machine cycles
		long machineClock = 4194304;
		long runStartTimer = System.currentTimeMillis();
		long cpuClockCount = 0;
		
		while (true) {
			
			
//			System.out.println("GPU clock : " + m_clockCount + " CPU clock : " + cpuClockCount);
			
			// CPU is running at 4.194304Mhz but instructions take a multiple of 4 to be executed.
			// Hence, the CPU behaves as clocked at 1.048576Mhz
			cpuClockCount += m_cpu.step();
			
			while(m_clockCount < cpuClockCount) {
				m_gpu.step();
				m_timers.step();
				m_dma.step();
				m_apu.step();

				if (m_clockCount % 8000000 == 0) {
					m_cartidge.saveRAM();
				}
				
				// Ensure emulation is synchronized with real time
				if (m_clockCount % 100 == 0 || m_emulationSyncShift < 0) {
					long elapsed = System.currentTimeMillis() - runStartTimer;
					
					// Clock speed on DMG GB is 4.19430Mhz
					long wait = (long) (m_clockCount * (1000.0/machineClock) - elapsed);
					setEmulationSyncShift(wait);
					if (wait > 0) {
						try { Thread.sleep(wait); } 	
						catch (InterruptedException e) {}
						elapsed = System.currentTimeMillis() - runStartTimer;
					}
					
	//				System.out.println("CPU freq : " + (1000.0 * cpuClockCount / elapsed));
	//				System.out.println("GPU freq : " + (1000.0 * m_clockCount / elapsed));
				}
				
				m_clockCount++;
			}
		}
	}

	public byte[] getLastRAMSave(String cartidgeID) {
		try {
			FileInputStream input = new FileInputStream(new File(m_saveFolder, cartidgeID+".sav"));
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			StreamCopier.copy(input, byteArrayOutputStream);
			input.close();
			return byteArrayOutputStream.toByteArray();
		}
		catch (Exception e) {
			return null;
		}
	}

	public void save(Cartidge cartidge, String cartidgeID) {
		try {
			System.out.println("Save cartidge RAM");
			FileOutputStream output = new FileOutputStream(new File(m_saveFolder, cartidgeID+".sav"));
			output.write(cartidge.getRAM());
			output.flush();
			output.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
	}
}

