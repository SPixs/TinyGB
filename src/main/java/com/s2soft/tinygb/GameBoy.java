package com.s2soft.tinygb;

import com.s2soft.tinygb.cpu.GBCpu;
import com.s2soft.tinygb.gpu.GBGpu;
import com.s2soft.tinygb.mmu.GBMemory;

public class GameBoy {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	private GBMemory m_memory;
	private GBCpu m_cpu;
	private GBGpu m_gpu;
	private int m_clockCount;

	public GameBoy() {
		m_gpu = new GBGpu(this);
		m_memory = new GBMemory(this);
		m_cpu = new GBCpu(this);
		reset();
	}

	//	 ========================== Access methods ===========================

	public GBMemory getMemory() {
		return m_memory;
	}

	public GBCpu getCpu() {
		return m_cpu;
	}

	public GBGpu getGpu() {
		return m_gpu;
	}
	
	public long getClockCount() {
		return m_clockCount;
	}
	
	//	 ========================= Treatment methods =========================

	public void reset() {
		m_memory.reset();
		m_clockCount = 0;
		m_cpu.reset();
		m_gpu.reset();
	}


	public void start() {
		
		// CPU is running at 4.194304Mhz (Clock cycles) or 1.048576Mhz (Machine cycles) 
		// Cpu and instruction speed are described in machine cycles
		double machineClock = 4.194304e6;
		long runStartTimer = System.currentTimeMillis();
		long cpuClockCount = 0;
		
		while (true) {
			
//			System.out.println("GPU clock : " + m_clockCount + " CPU clock : " + cpuClockCount);
			
			while(m_clockCount >= cpuClockCount * 4) {
				cpuClockCount += m_cpu.step();
			}
			m_gpu.step();
			
			if (m_clockCount % 1000 == 0) {
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

