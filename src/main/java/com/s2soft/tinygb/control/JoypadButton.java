package com.s2soft.tinygb.control;

/**
 *   This is the matrix layout for register $FF00:
 *   
 *   Bit 7 - Not used
 *   Bit 6 - Not used
 *   Bit 5 - P15 out port
 *   Bit 4 - P14 out port
 *   Bit 3 - P13 in port
 *   Bit 2 - P12 in port
 *   Bit 1 - P11 in port
 *   Bit 0 - P10 in port
 *           
 *            P14                P15
 *             |                  |
 * --P10-------O-Right------------O-A---------
 *             |                  |
 * --P11-------O-Left-------------O-B---------
 *             |                  |
 * --P12-------O-Up---------------O-Select----
 *             |                  |
 * --P13-------O-Down-------------O-Start-----
 *             |                  |
 *
 * @author smametz
 */
public enum JoypadButton {

	A(Constants.P10, Constants.P15),
	B(Constants.P11, Constants.P15),
	SELECT(Constants.P12, Constants.P15),
	START(Constants.P13, Constants.P15),
	UP(Constants.P12, Constants.P14),
	DOWN(Constants.P13, Constants.P14),
	LEFT(Constants.P11, Constants.P14),
	RIGHT(Constants.P10, Constants.P14);
	
	private byte i,j;
	
	private JoypadButton(byte i, byte j) {
		this.i = i;
		this.j = j;
	}

	/**
	 * @return button row coordinate in matrix
	 */
	protected byte getI() {
		return i;
	}

	/**
	 * @return button column coordinate in matrix
	 */
	protected byte getJ() {
		return j;
	}

	public final class Constants {
		public final static byte P15 = 0b00100000;
		public final static byte P14 = 0b00010000;
		public final static byte P13 = 0b00001000;
		public final static byte P12 = 0b00000100;
		public final static byte P11 = 0b00000010;
		public final static byte P10 = 0b00000001;
	}
}


