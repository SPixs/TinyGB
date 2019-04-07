package com.s2soft.tinygb;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.s2soft.tinygb.cpu.Disassembler;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.display.LCDDisplay;

public class Main {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public static void main(String[] args) throws Exception {
		LCDDisplay lcdDisplay = new LCDDisplay();
		SwingJoypad joypad = new SwingJoypad();
		IConfiguration configuration = new DefaultConfiguration();
		GameBoy gameBoy = new GameBoy(configuration, lcdDisplay, joypad);
		
		Cartidge cartidge = new Cartidge();
//		cartidge.read(Main.class.getResourceAsStream("/rom/bgSprite0.gb"));
		cartidge.read(Main.class.getResourceAsStream("/rom/Tetris.gb"));
//		cartidge.read(Main.class.getResourceAsStream("/rom/bgbtest.gb"));
//		cartidge.read(Main.class.getResourceAsStream("/Tennis (W) [!].gb"));
		gameBoy.setCartidge(cartidge);
		
//		gameBoy.getMemory().setBootROMLock(false);
//		new Disassembler(gameBoy).disassemble((short)0x2820, (short)(0x2820+0x0ff));
//		System.exit(0);
		
//		for (int i=0x0104;i<0x0104+(0x00D8-0x00A8);i++) {
//			System.out.println("CART LOGO " + i + "\t" + Instruction.toHexByte(cartidge.getROMByte(i)) );
//		}
//		
//		System.exit(0);
		
		JFrame mainFrame = new JFrame("TinyGB");
		mainFrame.setIconImages(Arrays.asList(new BufferedImage[] {
				ImageIO.read(Main.class.getResourceAsStream("/icon.png")),
				ImageIO.read(Main.class.getResourceAsStream("/icon_32.png"))
		}));
		mainFrame.addKeyListener(joypad);
		GameboyEnclosurePanel panel = new GameboyEnclosurePanel(lcdDisplay);
		mainFrame.getContentPane().add(panel);
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
		gameBoy.start();
	}
}

