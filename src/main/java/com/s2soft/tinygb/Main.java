package com.s2soft.tinygb;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.s2soft.tinygb.audio.AudioDevice;
import com.s2soft.tinygb.audio.NullAudioDevice;
import com.s2soft.tinygb.display.BufferedLCDDisplay;
import com.s2soft.tinygb.display.NullDisplay;

public class Main {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	//	 =========================== Constructor =============================

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public static void main(String[] args) throws Exception {
//		LCDDisplay lcdDisplay = new LCDDisplay();
		BufferedLCDDisplay lcdDisplay = new BufferedLCDDisplay();
		AudioDevice audioDevice = new AudioDevice(44100, 16);
		SwingJoypad joypad = new SwingJoypad();
		IConfiguration configuration = new DefaultConfiguration();
		
		GameBoy gameBoy = new GameBoy(configuration, lcdDisplay, audioDevice, joypad);
		
		Cartidge cartidge = new Cartidge();
//		cartidge.read(Main.class.getResourceAsStream("/rom/Tetris.gb"));

//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/cpu_instrs.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/instr_timing/instr_timing.gb")); // OK

//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/01-registers.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/02-len ctr.gb")); 
		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/03-trigger.gb")); 
		
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/01-special.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/02-interrupts.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/03-op sp,hl.gb")); // OK !!!
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/04-op r,imm.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/05-op rp.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/06-ld r,r.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/07-jr,jp,call,ret,rst.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/08-misc instrs.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/09-op r,r.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/10-bit ops.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/11-op a,(hl).gb")); // OK
//

		
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/04-op r,imm.gb")); // OK
		
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/cpu_instrs.gb"));²
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/01-special.gb"));
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/individual/09-op r,r.gb"));
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

