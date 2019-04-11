package com.s2soft.tinygb.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.JPanel;

public class LCDDisplay extends JPanel implements IDisplay {

	//   ============================ Constants ==============================

	private static final long serialVersionUID = -1818975219787299491L;

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
	
	//	 =========================== Constructor =============================
	
	public LCDDisplay() {
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
		
		setPreferredSize(new Dimension(160 * m_scaleFactor, 144 * m_scaleFactor));
	}

	//	 ========================== Access methods ===========================

	protected BufferedImage getDisplayImage() {
		return m_displayImage;
	}

	//	 ========================= Treatment methods =========================

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);
		g2d.drawImage(m_displayImage, 0, 0, 160 * m_scaleFactor, 144 * m_scaleFactor, null);
	}
	
	@Override
	public void setEnable(boolean enabled) {
	}
	
	@Override
	public void putPixel(byte pixel) {
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
		m_pixelIndex = 0;
		getParent().repaint();
	}
}

