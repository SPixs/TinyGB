package com.s2soft.tinygb.control;

import java.util.HashSet;
import java.util.Set;

public class JoypadHandler implements IJoypadButtonListener {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private Set<JoypadButton> m_pressedButton = new HashSet<JoypadButton>();
	
	private byte m_matrixColumnSelection = 0;
	
	//	 =========================== Constructor =============================

	public JoypadHandler(IJoypad joypad) {
		joypad.addButtonListener(this);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public synchronized byte read() {
		byte result = (byte) 0xEF;
		for (JoypadButton pressedButton : m_pressedButton) {
			if (pressedButton.getJ() == m_matrixColumnSelection) {
				result &= ~pressedButton.getI();
			}
		}
		return result;
	}

	public void write(byte value) {
		m_matrixColumnSelection = (byte) (value & 0x30);
	}

	@Override
	public synchronized void buttonPressed(JoypadButton button) {
		// @TODO We should also generate an interrupt here
		m_pressedButton.add(button);
	}

	@Override
	public synchronized void buttonReleased(JoypadButton button) {
		m_pressedButton.remove(button);
	}
}
