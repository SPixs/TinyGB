package com.s2soft.tinygb.gpu;

public abstract class GPUPhase {

	private GBGPU m_gpu;
	
	private long m_enterClock;

	private String m_name;

	public GPUPhase(String name) {
		m_name = name;
	}

	public String getName() {
		return m_name;
	}

	protected GBGPU getGpu() {
		return m_gpu;
	}

	public void enter(GBGPU gbGpu) {
		m_gpu = gbGpu;
		m_enterClock = m_gpu.getClockCount();
		enterImpl();
	}
	
	public long getEnterClock() {
		return m_enterClock;
	}

	public void setPhase(GPUPhase phase) {
		m_gpu.enterPhase(phase);
	}

	protected abstract void enterImpl();
	
	protected void step() {
		stepImpl(getElapsedClockCountInPhase());
	}

	public long getElapsedClockCountInPhase() {
		return m_gpu.getClockCount() - m_enterClock;
	}
	
	protected abstract void stepImpl(long elapsedClockCount);

}

