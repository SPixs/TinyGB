package com.s2soft.tinygb.display;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class BufferedLCDDisplay extends Canvas implements IDisplay {

	//   ============================ Constants ==============================

	private static final long serialVersionUID = -1200605456389201862L;

	//	 =========================== Attributes ==============================

	// Gameboy pocket colors
	private Color white = new Color(0x00E0DBCD);
	private Color light = new Color(0x00A89F94);
	private Color dark 	= new Color(0x00706B66);
	private Color black = new Color(0x002B2B26);
	
	private byte[] m_videoMemory;
	private BufferedImage m_displayImage; 
	
	private int m_pixelIndex = 0;
	
	private int m_scaleFactor = 2;

	private BufferStrategy m_strategy;

	private BufferedImage m_gameboyPocketLCD;

	private BufferedImage m_gameboyPocketEnclosure;

	private boolean m_enabled;
	
	//	 =========================== Constructor =============================
	
	public BufferedLCDDisplay() throws IOException {
		
		m_videoMemory = new byte[160*144/4];
		
		Color[] palette = new Color[] { white, light, dark, black };
		byte[] redComponents = new byte[palette.length];
		byte[] greenComponents = new byte[palette.length];
		byte[] blueComponents = new byte[palette.length];
		for (int i=0;i<palette.length;i++) {
			Color color = palette[i];
			redComponents[i] = (byte)(color.getRed() & 0x0FF);
			greenComponents[i] = (byte)(color.getGreen() & 0x0FF);
			blueComponents[i] = (byte)(color.getBlue() & 0x0FF);
		}
		
        IndexColorModel colorModel = new IndexColorModel(2, 4, redComponents, greenComponents, blueComponents);
        DataBuffer buffer = new DataBufferByte(m_videoMemory, m_videoMemory.length);
        WritableRaster raster = Raster.createPackedRaster(buffer, 160, 144, 2, new Point(0, 0));
        
		m_displayImage = new BufferedImage(colorModel, raster, false, null);
		
		m_gameboyPocketEnclosure = ImageIO.read(getClass().getResourceAsStream("/pocket_enclosure.png"));
		m_gameboyPocketLCD = ImageIO.read(getClass().getResourceAsStream("/pocket_lcd_alpha.png"));
		
		setPreferredSize(new Dimension(m_gameboyPocketEnclosure.getWidth(), m_gameboyPocketEnclosure.getHeight()));
		
		m_enabled = false;
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	@Override
	public void setEnable(boolean enabled) {
		System.out.println("LCD DISPLAY : " + enabled);
		m_enabled = enabled;
		if (!enabled) {
			m_pixelIndex = 0;
			Arrays.fill(m_videoMemory, (byte)0);
			refresh();
		}
	}

	@Override
	public void putPixel(byte pixel) {
		if (!m_enabled) {
			throw new IllegalStateException();
		}
		byte pixels = m_videoMemory[m_pixelIndex / 4];
		pixel = (byte) (pixel << ((3-(m_pixelIndex % 4)) * 2));
		byte mask = (byte) (0x03 << ((3-(m_pixelIndex % 4)) * 2));
		pixels &= ~mask;
		pixels |= pixel;
		m_videoMemory[m_pixelIndex / 4] = pixels;
		m_pixelIndex++;
	}

	@Override
	public void refresh() {
		if (m_strategy == null) {
			createBufferStrategy(2);
			m_strategy = getBufferStrategy();
			
			Graphics2D g2d = (Graphics2D) m_strategy.getDrawGraphics();
			g2d.drawImage(m_gameboyPocketEnclosure, 0, 0, null);
		}
		
		do {
			Graphics2D g2d = (Graphics2D) m_strategy.getDrawGraphics();
			m_pixelIndex = 0;
			if (m_strategy.contentsLost()) {
				g2d.drawImage(m_gameboyPocketEnclosure, 0, 0, null);
			}
			g2d.drawImage(m_displayImage, 65, 38, 160 * m_scaleFactor, 144 * m_scaleFactor, null);
			g2d.drawImage(m_gameboyPocketLCD, 0, 0, null);
			
			g2d.dispose();
			
			m_strategy.show();
		}
		while (m_strategy.contentsLost());
	}
}

