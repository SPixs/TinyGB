package com.s2soft.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class TestBufferStrategy extends JFrame implements Runnable, WindowListener {

	// ============================ Constants ==============================

	// =========================== Attributes ==============================

	private Thread graphicsThread;
	private boolean running = false;
	private BufferStrategy strategy;

	// =========================== Constructor =============================

	// ========================== Access methods ===========================

	// ========================= Treatment methods =========================

	public TestBufferStrategy() {
		super("FrameDemo");
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(500, 500);
		setResizable(true);
		setVisible(true);

		createBufferStrategy(2);
		strategy = getBufferStrategy();

		running = true;
		graphicsThread = new Thread(this);
		graphicsThread.start();
	}

	public static void main(String[] args) {
		new TestBufferStrategy();
	}

	public void addNotify() {
		super.addNotify();
		createBufferStrategy(2);
		strategy = getBufferStrategy();
	}

	@Override
	public void run() {
		// Main loop
		while (running) {
			// Prepare for rendering the next frame
			// ...

			// Render single frame
			do {
				// The following loop ensures that the contents of the drawing buffer
				// are consistent in case the underlying surface was recreated
				do {
					// Get a new graphics context every time through the loop
					// to make sure the strategy is validated
					Graphics g = strategy.getDrawGraphics();

					g.setColor(Color.GRAY);
					g.drawRect(0, 0, 500, 500);
					g.setColor(Color.BLACK);
					g.drawLine(50, 50, 200, 50);

					// Dispose the graphics
					g.dispose();

					// Repeat the rendering if the drawing buffer contents
					// were restored
				} while (running && strategy.contentsRestored());

				// Display the buffer
				strategy.show();

				// Repeat the rendering if the drawing buffer was lost
			} while (running && strategy.contentsLost());
		}
		setVisible(false);
		dispose();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		running = false;
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}
