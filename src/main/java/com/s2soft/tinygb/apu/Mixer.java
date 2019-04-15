package com.s2soft.tinygb.apu;

import java.util.ArrayList;
import java.util.List;

public class Mixer implements ISoundProvider {

	//   ============================ Constants ==============================

	private static final boolean TRACE = false;

	//	 =========================== Attributes ==============================

//	private List<ISoundProvider> m_inputs = new ArrayList<ISoundProvider>();
	private ArrayList<Switch> m_inputs = new ArrayList<Switch>();

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
		if (GBAPU.TRACE && TRACE) {
			System.out.println("Setting volume in mixer " + m_name +" = " + volume);
		}
	}

	public int getVolume() {
		return m_volume;
	}

	//	 ========================= Treatment methods =========================
	
//	public void addInput(ISoundProvider provider) {
//		m_inputs.add(provider);
//	}
	
	public void addInput(Switch switchInput) {
		m_inputs.add(switchInput);
	}

	public void removeAllInput() {
		m_inputs.clear();
	}
	
	@Override
	public int getValue() {
		int result = 0;
		for (int i=0;i<m_inputs.size();i++) {
			result += m_inputs.get(i).getValue();
		}
		result *= (m_volume + 1);
		return result;
	}
}

