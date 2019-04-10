package com.s2soft.tinygb.apu;

import java.util.ArrayList;
import java.util.List;

public class Mixer implements ISoundProvider {

	//   ============================ Constants ==============================

	private static final boolean TRACE = true;

	//	 =========================== Attributes ==============================

	private List<ISoundProvider> m_inputs = new ArrayList<ISoundProvider>();

	private String m_name;

	private int m_volume;
	
	//	 =========================== Constructor =============================

	public Mixer(String name) {
		m_name = name;
	}

	//	 ========================== Access methods ===========================

	/**
	 * @param volume 0 (lowest but not muted) to 7 (loudest)
	 */
	public void setVolume(int volume) {
		m_volume = volume;
		if (TRACE) {
			System.out.println("Setting volume in mixer " + m_name +" = " + volume);
		}
	}

	public int getVolume() {
		return m_volume;
	}

	//	 ========================= Treatment methods =========================
	
	public void addInput(ISoundProvider provider) {
		m_inputs.add(provider);
	}
	
	public void removeAllInput() {
		m_inputs.clear();
	}
	
	@Override
	public int getValue() {
		int result = 0;
		for (ISoundProvider provider : m_inputs) {
			final int value = provider.getValue();
			result += value;
		}
		result *= (m_volume + 1);
		return result;
	}
}

