package com.s2soft.tinygb;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.s2soft.tinygb.audio.AudioDevice;
import com.s2soft.tinygb.cpu.Disassembler;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.display.BufferedLCDDisplay;
import com.s2soft.tinygb.display.LCDDisplay;

public class Main {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public static void main(String[] args) throws Exception {
//		LCDDisplay lcdDisplay = new LCDDisplay();
		BufferedLCDDisplay lcdDisplay = new BufferedLCDDisplay();
		AudioDevice audioDevice = new AudioDevice(44100);
		SwingJoypad joypad = new SwingJoypad();
		IConfiguration configuration = new DefaultConfiguration();
		GameBoy gameBoy = new GameBoy(configuration, lcdDisplay, audioDevice, joypad);
		
		Cartidge cartidge = new Cartidge();
//		cartidge.read(Main.class.getResourceAsStream("/rom/Tetris.gb"));
		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/01-registers.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/02-len ctr.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/03-trigger.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/04-sweep.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/05-sweep details.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/06-overflow on trigger.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/07-len sweep period sync.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/08-len ctr during power.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/09-wave read while on.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/10-wave trigger while on.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/11-regs after power.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/12-wave write while on.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/dmg_sound.gb"));
		gameBoy.setCartidge(cartidge);
		
		JFrame mainFrame = new JFrame("TinyGB");
		mainFrame.setIconImages(Arrays.asList(new BufferedImage[] {
				ImageIO.read(Main.class.getResourceAsStream("/icon.png")),
				ImageIO.read(Main.class.getResourceAsStream("/icon_32.png"))
		}));
		mainFrame.addKeyListener(joypad);
		mainFrame.setFocusable(true);
		mainFrame.setFocusTraversalKeysEnabled(false);
		mainFrame.requestFocus();
		lcdDisplay.setFocusable(false);
		mainFrame.getContentPane().add(lcdDisplay);
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
		audioDevice.start();
		
		gameBoy.start();
	}
}

