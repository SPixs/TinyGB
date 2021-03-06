package com.s2soft.tinygb.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;

public class JoypadHandler implements IJoypadButtonListener {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private GameBoy m_gameBoy;
	
	private Set<JoypadButton> m_pressedButton = new HashSet<JoypadButton>();
	
	private byte m_matrixColumnSelection = 0;

	//	 =========================== Constructor =============================

	public JoypadHandler(GameBoy gameBoy, IJoypad joypad) {
		m_gameBoy = gameBoy;
		joypad.addButtonListener(this);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public synchronized byte read() {
		byte result = (byte) 0xFF;//(byte)(0b11001111 | m_matrixColumnSelection);
		for (JoypadButton pressedButton : m_pressedButton) {
			if ((pressedButton.getJ() &  m_matrixColumnSelection) == 0) {
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
		m_gameBoy.getGpu().getMemory().requestInterrupt(4); // Joypad interrupt request
	}

	@Override
	public synchronized void buttonReleased(JoypadButton button) {
		m_pressedButton.remove(button);
	}
}

