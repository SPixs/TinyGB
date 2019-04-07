package com.s2soft.tinygb;

import com.s2soft.tinygb.control.IJoypad;
import com.s2soft.tinygb.control.JoypadHandler;
import com.s2soft.tinygb.cpu.GBCpu;
import com.s2soft.tinygb.display.IDisplay;
import com.s2soft.tinygb.dma.DMA;
import com.s2soft.tinygb.gpu.GBGPU;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.tinygb.timer.Timers;

public class GameBoy {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private GBMemory m_memory;
	private GBCpu m_cpu;
	private GBGPU m_gpu;
	private long m_clockCount;
	private IDisplay m_display;
	private JoypadHandler m_joypadHandler;
	private Timers m_timers;
	private DMA m_dma;

	private IConfiguration m_configuration;

	//	 =========================== Constructor =============================

	public GameBoy(IConfiguration configuration, IDisplay display, IJoypad joypad) {
		m_display = display;
		m_memory = new GBMemory(this);
		m_gpu = new GBGPU(this);
		m_cpu = new GBCpu(this);
		m_dma = new DMA(this);
		m_configuration = configuration;
		m_joypadHandler = new JoypadHandler(joypad);
		m_timers = new Timers(this);
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
	
	public long getClockCount() {
		return m_clockCount;
	}
	
	public IDisplay getDisplay() {
		return m_display;
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

	//	 ========================= Treatment methods =========================

	public void setCartidge(Cartidge cartidge) {
		m_memory.setCartidge(cartidge);
	}
	
	public void reset() {
		m_memory.reset();
		m_clockCount = 0;
		m_cpu.reset();
		m_gpu.reset();
		
		if (!m_configuration.useBootRom()) {
			m_memory.setBootROMLock(false);
			m_cpu.setPc(0x0100);
			m_gpu.setLCDEnable(true);
			m_cpu.setSp(0xFFFE);
		}
	}


	public void start() {
		
		// CPU is running at 4.194304Mhz (Clock cycles) or 1.048576Mhz (Machine cycles) 
		// Cpu and instruction speed are described in machine cycles
		long machineClock = 4194304;
		long runStartTimer = System.currentTimeMillis();
		long cpuClockCount = 0;
		
		while (true) {
			
			
//			System.out.println("GPU clock : " + m_clockCount + " CPU clock : " + cpuClockCount);
			
			// CPU is running at 4.194304Mhz but instructions take a multiple of 4 to be executed.
			// Hence, the CPU behaves as clocked at 1.048576Mhz
			while(m_clockCount >= cpuClockCount) {
				cpuClockCount += m_cpu.step();
			}
			m_gpu.step();
			m_timers.step();
			m_dma.step();
			
			// Ensure emulation is synchronized with real time
			if (m_clockCount % 10000 == 0) {
				long elapsed = System.currentTimeMillis() - runStartTimer;
				
				// Clock speed on DMG GB is 4.19430Mhz
				long wait = (long) (m_clockCount * (1000.0/machineClock) - elapsed);
				if (wait > 0) {
					try { Thread.sleep(wait); } 	
					catch (InterruptedException e) {}
				}
				
//				System.out.println("CPU freq : " + (1000.0 * cpuClockCount / elapsed));
//				System.out.println("GPU freq : " + (1000.0 * m_clockCount / elapsed));
			}
			
			m_clockCount++;
		}
	}
}

