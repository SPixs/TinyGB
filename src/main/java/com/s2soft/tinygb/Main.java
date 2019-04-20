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

//		cartidge.read(Main.class.getResourceAsStream("/rom/dist.gb"));
//		cartidge.read(Main.class.getResourceAsStream("/rom/Bc.gb"));
//		cartidge.read(Main.class.getResourceAsStream("/rom/Ls.gb"));

//		cartidge.read(Main.class.getResourceAsStream("/rom/Alleyway (W) [!].gb"));
//		cartidge.read(Main.class.getResourceAsStream("/rom/Artic Zone (Sachen 4-in-1 Vol. 5) (Unl) [!].gb"));
//		cartidge.read(Main.class.getResourceAsStream("/rom/Tasmania Story (U) [!].gb"));
//		cartidge.read(Main.class.getResourceAsStream("/rom/Bomb Jack (U).gb"));
		
		///////////////// Unit tests //////////////////
		
		///////////////// MOONEYE //////////////////

//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/ei_timing.gb")); // OK

//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/div_write.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/rapid_toggle.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tim00.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tim00_div_trigger.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tim01.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tim01_div_trigger.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tim10.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tim10_div_trigger.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tim11.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tim11_div_trigger.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tima_reload.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tima_write_reloading.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/timer/tma_write_reloading.gb")); // OK

//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/interrupts/ie_push.gb")); // Failed

//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/instr/daa.gb")); // OK
		
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/bits/reg_f.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/bits/mem_oam.gb")); // OK
		cartidge.read(new FileInputStream("testROM/mooneye/acceptance/bits/unused_hwio-GS.gb")); // OK

		///////////////// BLARGG //////////////////
		
//		cartidge.read(new FileInputStream("testROM/Blargg/cpu_instrs/cpu_instrs.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/instr_timing/instr_timing.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/dmg_sound.gb")); // FAILED ! but all units pass... Check bank switching
	
	
//		cartidge.read(new FileInputStream("testROM/Blargg/mem_timing/individual/01-read_timing.gb"));  // FAILED 
//		cartidge.read(new FileInputStream("testROM/Blargg/mem_timing/individual/02-write_timing.gb"));  // FAILED
//		cartidge.read(new FileInputStream("testROM/Blargg/mem_timing/individual/03-modify_timing.gb"));  // FAILED

//		cartidge.read(new FileInputStream("testROM/Blargg/interrupt_time/interrupt_time.gb")); // FAILED

//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/01-registers.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/02-len ctr.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/03-trigger.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/04-sweep.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/05-sweep details.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/06-overflow on trigger.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/07-len sweep period sync.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/08-len ctr during power.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/09-wave read while on.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/10-wave trigger while on.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/11-regs after power.gb")); // OK
//		cartidge.read(new FileInputStream("testROM/Blargg/dmg_sound/rom_singles/12-wave write while on.gb")); 

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

