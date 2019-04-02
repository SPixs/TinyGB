package com.s2soft.tinygb;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.s2soft.tinygb.display.LCDDisplay;

public class Main {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public static void main(String[] args) throws Exception {
		LCDDisplay lcdDisplay = new LCDDisplay();
		GameBoy gameBoy = new GameBoy(lcdDisplay);
		
		Cartidge cartidge = new Cartidge();
		cartidge.read(Main.class.getResourceAsStream("/Tetris.gb"));
		gameBoy.setCartidge(cartidge);
		
		JFrame mainFrame = new JFrame("TinyGB");
		mainFrame.setIconImages(Arrays.asList(new BufferedImage[] {
				ImageIO.read(Main.class.getResourceAsStream("/icon.png")),
				ImageIO.read(Main.class.getResourceAsStream("/icon_32.png"))
		}));
		GameboyEnclosurePanel panel = new GameboyEnclosurePanel(lcdDisplay);
		mainFrame.getContentPane().add(panel);
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
		gameBoy.start();
	}
}

