package com.s2soft.tinygb.gpu;

public abstract class GPUPhase {

	private GBGpu m_gpu;
	
	private long m_enterClock;

	private String m_name;

	public GPUPhase(String name) {
		m_name = name;
	}

	public String getName() {
		return m_name;
	}

	protected GBGpu getGpu() {
		return m_gpu;
	}

	public void enter(GBGpu gbGpu) {
		m_gpu = gbGpu;
		m_enterClock = m_gpu.getClockCount();
		enterImpl();
	}
	
	public void setPhase(GPUPhase phase) {
		m_gpu.enterPhase(phase);
	}

	protected abstract void enterImpl();
	
	protected void step() {
		long elapsedClockCount = m_gpu.getClockCount() - m_enterClock;
		stepImpl(elapsedClockCount);
	}

	protected abstract void stepImpl(long elapsedClockCount);

}

