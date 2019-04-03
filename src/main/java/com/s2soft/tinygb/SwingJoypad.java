package com.s2soft.tinygb;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import com.s2soft.tinygb.control.IJoypad;
import com.s2soft.tinygb.control.IJoypadButtonListener;
import com.s2soft.tinygb.control.JoypadButton;

public class SwingJoypad implements IJoypad, KeyListener {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================
	
	private List<IJoypadButtonListener> m_listeners = new ArrayList<IJoypadButtonListener>();

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public void keyPressed(KeyEvent event) {
		JoypadButton button = getMappedButton(event);
		if (button != null) {
			for (IJoypadButtonListener listener : m_listeners) {
				listener.buttonPressed(button);
			}
		}
	}

	private JoypadButton getMappedButton(KeyEvent event) {
		int keyCode = event.getKeyCode();
		switch (keyCode) {
			case KeyEvent.VK_UP: return JoypadButton.UP;
			case KeyEvent.VK_DOWN: return JoypadButton.DOWN;
			case KeyEvent.VK_LEFT: return JoypadButton.LEFT;
			case KeyEvent.VK_RIGHT: return JoypadButton.RIGHT;
			case KeyEvent.VK_S: return JoypadButton.A;
			case KeyEvent.VK_A: return JoypadButton.B;
			case KeyEvent.VK_SHIFT: return JoypadButton.SELECT;
			case KeyEvent.VK_ENTER: return JoypadButton.START;
		}
		return null;
	}

	@Override
	public void keyReleased(KeyEvent event) {
		JoypadButton button = getMappedButton(event);
		if (button != null) {
			for (IJoypadButtonListener listener : m_listeners) {
				listener.buttonReleased(button);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void addButtonListener(IJoypadButtonListener listener) {
		m_listeners.add(listener);
	}
}

