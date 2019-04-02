package com.s2soft.tinygb;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.s2soft.tinygb.display.LCDDisplay;

public class GameboyEnclosurePanel extends JPanel {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private BufferedImage m_gameboyPocketEnclosure;
	private BufferedImage m_gameboyPocketLCD;
	private LCDDisplay m_lcdDisplay;

	//	 =========================== Constructor =============================

	public GameboyEnclosurePanel(LCDDisplay lcdDisplay) throws IOException {
		m_gameboyPocketEnclosure = ImageIO.read(getClass().getResourceAsStream("/pocket_enclosure.png"));
		m_gameboyPocketLCD = ImageIO.read(getClass().getResourceAsStream("/pocket_lcd_alpha.png"));
		m_lcdDisplay = lcdDisplay;
		
		setLayout(null);
		setPreferredSize(new Dimension(m_gameboyPocketEnclosure.getWidth(), m_gameboyPocketEnclosure.getHeight()));
		
		final JPanel enclosureBackground = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.drawImage(m_gameboyPocketEnclosure, 0, 0, null);
			}
		};
		enclosureBackground.setBounds(0, 0, (int)getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
		m_lcdDisplay.setBounds(65, 38, (int)m_lcdDisplay.getPreferredSize().getWidth(), (int)m_lcdDisplay.getPreferredSize().getHeight());
		final JPanel enclosureLCD = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.drawImage(m_gameboyPocketLCD, 0, 0, null);
			}
		};
		enclosureLCD.setBounds(0, 0, (int)getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
		add(enclosureLCD);
		add(m_lcdDisplay);
		add(enclosureBackground);
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================
	
//	@Override
//	protected void paintChildren(Graphics g) {
//		super.paintComponents(g);
//		Graphics2D g2d = (Graphics2D) g;
//////		super.paintComponent(g);
//		g2d.fillRect(0,  0,  320 , 200);
//	}
	
//	@Override
//	public void paintComponents(Graphics g) {
////		super.paintComponents(g);
//		Graphics2D g2d = (Graphics2D) g;
////		super.paintComponent(g);
//		g2d.fillRect(0,  0,  320 , 200);
//	}
	
//	@Override
//	protected void paintComponent(Graphics g) {
////		super.paintComponent(g);
//		Graphics2D g2d = (Graphics2D) g;
////		float alpha = 1.0f;
////		int type = AlphaComposite.SRC_OVER; 
////		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
////		g2d.setComposite(composite);
////		g2d.drawImage(m_gameboyPocketLCD, 0, 0, null);
//	}
	
//	@Override
//	protected void paintChildren(Graphics g) {
//		super.paintChildren(g);
//		Graphics2D g2d = (Graphics2D) g;
//		g2d.drawImage(m_gameboyPocketLCD, 0, 0, null);
//	}
	
}

