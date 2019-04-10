package com.s2soft.tinygb.apu;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class GBAPU {

	//   ============================ Constants ==============================

	public final static boolean TRACE = true;

	//	 =========================== Attributes ==============================

	private GameBoy m_gameBoy;
	private GBMemory m_memory;
	
	private Switch[] m_switch = new Switch[8];
	private Mixer m_leftMixer = new Mixer("Left");
	private Mixer m_rightMixer = new Mixer("Right");
	
	private Voice1 m_voice1 = new Voice1();
	private Voice2 m_voice2 = new Voice2();
	private Voice3 m_voice3 = new Voice3();
	private Voice4 m_voice4 = new Voice4();
	
	private Voice[] m_voices = new  Voice[] { m_voice1, m_voice2, m_voice3, m_voice4 };
	
	private boolean m_soundEnable = false;

	//	 =========================== Constructor =============================

	public GBAPU(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
		m_memory = m_gameBoy.getMemory();
		
		for (int i=0;i<4;i++) {
			m_switch[i] = new Switch("Left channel, voice " + i); // left channel switch for voice i
			m_switch[i].setInput(m_voices[i]);
			m_leftMixer.addInput(m_switch[i]);
			m_switch[i+4] = new Switch("Right channel, voice " + i); // right channel switch for voice i
			m_rightMixer.addInput(m_switch[i+4]);
			m_switch[i+4].setInput(m_voices[i]);
		}
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public void step() {
		if (!m_soundEnable) {
			return;
		}
		
		for (Voice voice : m_voices) {
			voice.step();
		}
		
		m_gameBoy.getAudioDevice().putSample(
				(byte)(getLeftChannelValue() >> 3), 
				(byte)(getRightChannelValue() >> 3));
	}

	
	public int getLeftChannelValue() {
		return m_leftMixer.getValue();
	}
	
	public int getRightChannelValue() {
		return m_rightMixer.getValue();
	}

	/**
	 * I/O register NR50 at $FF24 
	 * The volume bits specify the "Master Volume" for Left/Right sound output.
	 * Bit 7   - Output Vin to SO2 terminal (1=Enable)
	 * Bit 6-4 - SO2 output level (volume)  (0-7)
	 * Bit 3   - Output Vin to SO1 terminal (1=Enable)
	 * Bit 2-0 - SO1 output level (volume)  (0-7)
	 * 
	 * The Vin signal is received from the game cartridge bus, allowing external hardware 
	 * in the cartridge to supply a fifth sound channel, additionally to the gameboys internal 
	 * four channels. As far as I know this feature isn't used by any existing games.
	 * 
	 * @param v content of NR50 register 
	 */
	public void setNR50(byte v) {
		m_leftMixer.setVolume(v & 0x07);
		m_rightMixer.setVolume((v >> 4) & 0x07);
		// Vin L and Vin R are not handled
	}
	
	/**
	 * @return content of NR50 APU register
	 */
	public byte getNR50() {
		return (byte) (((m_leftMixer.getVolume() & 0x07) << 3) | (m_rightMixer.getVolume() & 0x07));
	}
	
	/**
	 * I/O register NR51 at $FF25 
	 * Selection of Sound output terminal (R/W)
	 * Bit 7 - Output sound 4 to SO2 terminal
	 * Bit 6 - Output sound 3 to SO2 terminal
	 * Bit 5 - Output sound 2 to SO2 terminal
	 * Bit 4 - Output sound 1 to SO2 terminal
	 * Bit 3 - Output sound 4 to SO1 terminal
	 * Bit 2 - Output sound 3 to SO1 terminal
	 * Bit 1 - Output sound 2 to SO1 terminal
	 * Bit 0 - Output sound 1 to SO1 terminal
	 * 
	 * @param v content of NR51 register 
	 */
	public void setNR51(byte v) {
		for (int i=0;i<8;i++) {
			m_switch[i].setEnabled(BitUtils.isSet(v, i));
		}
	}
	
	/**
	 * @return content of NR51 APU register
	 */
	public byte getNR51() {
		byte switchesStates = 0;
		for (int i=0;i<8;i++) {
			switchesStates = BitUtils.setBit(switchesStates, i, m_switch[i].isEnabled());
		}
		return switchesStates;
	}
	
	/**
	 * I/O register NR52 at $FF26 
	 * Sound on/off
	 * If your GB programs don't use sound then write 00h to this register 
	 * to save 16% or more on GB power consumption. 
	 * Disabeling the sound controller by clearing Bit 7 destroys the contents 
	 * of all sound registers. Also, it is not possible to access any sound 
	 * registers (execpt FF26) while the sound controller is disabled.
	 * 
	 * Bit 7 - All sound on/off  (0: stop all sound circuits) (Read/Write)
	 * Bit 3 - Sound 4 ON flag (Read Only)
	 * Bit 2 - Sound 3 ON flag (Read Only)
	 * Bit 1 - Sound 2 ON flag (Read Only)
	 * Bit 0 - Sound 1 ON flag (Read Only)
	 * 
	 * Bits 0-3 of this register are read only status bits, writing to these bits does NOT enable/disable sound. 
	 * The flags get set when sound output is restarted by setting the Initial flag (Bit 7 in NR14-NR44), 
	 * the flag remains set until the sound length has expired (if enabled). 
	 * A volume envelopes which has decreased to zero volume will NOT cause the sound flag to go off.
	 * 
	 * @param v content of NR52 register 
	 */
	public void setNR52(byte v) {
		boolean wasEnabled = m_soundEnable;
		m_soundEnable = BitUtils.isSet(v, 7);
		if (TRACE) {
			System.out.println("Turning sound : " + (m_soundEnable ? "ON" : "OFF"));
		}
		if (wasEnabled && !m_soundEnable) {
			// destroys the contents of all sound registers
			reset();
		}
	}
	
	/**
	 * @return content of NR52 APU register
	 */
	public byte getNR52() {
		byte result = (byte) 0xFF;
		result = BitUtils.setBit(result, 7, m_soundEnable);
		for (int i=0;i<4;i++) {
			result = BitUtils.setBit(result, i, m_voices[i].isPlaying());
		}
		return result;
	}
	
	/**
	 * I/O register NR10 at $FF10 
	 * Channel 1 Sweep register (R/W)
	 * Bit 6-4 - Sweep Time
	 * Bit 3   - Sweep Increase/Decrease
	 *            0: Addition    (frequency increases)
	 *            1: Subtraction (frequency decreases)
	 * Bit 2-0 - Number of sweep shift (n: 0-7)
	 * Sweep Time:
	 *   000: sweep off - no freq change
	 *   001: 7.8 ms  (1/128Hz)
	 *   010: 15.6 ms (2/128Hz)
	 *   011: 23.4 ms (3/128Hz)
	 *   100: 31.3 ms (4/128Hz)
	 *   101: 39.1 ms (5/128Hz)
	 *   110: 46.9 ms (6/128Hz)
	 *   111: 54.7 ms (7/128Hz)
	 *   
	 *   The change of frequency (NR13,NR14) at each shift is calculated 
	 *   by the following formula where X(0) is initial freq & X(t-1) is last freq:
	 *   
	 *     X(t) = X(t-1) +/- X(t-1)/2^n
	 * 
	 * @param v content of NR11 register 
	 */
	public void setNR10(byte v) {
		byte sweepShift = (byte)(v & 0x03);
		boolean increase = BitUtils.isSet(v, 3);
		byte sweepTime = (byte)((v >> 4) & 0x03);
		m_voice1.setSweepShift(sweepShift);
		m_voice1.setSweepIncrease(increase);
		m_voice1.setSweepTime(sweepTime);
	}

	public byte getNR10() {
		byte result = (byte) 0b10000000;
		result |= (m_voice1.getSweepShift() & 0x03);
		result |= ((m_voice1.getSweepTime() & 0x03) << 4) ;
		result = BitUtils.setBit(result, 3, m_voice1.isSweepIncrease());
		return result;
	}

	/**
	 * I/O register NR11 at $FF11 
	 * Channel 1 Sound length/Wave pattern duty (R/W).
	 * Bit 7-6 - Wave Pattern Duty (Read/Write)
	 * Bit 5-0 - Sound length data (Write Only) (t1: 0-63)
	 * Wave Duty:
	 * 00: 12.5% ( _-------_-------_------- )
	 * 01: 25%   ( __------__------__------ )
	 * 10: 50%   ( ____----____----____---- ) (normal)
	 * 11: 75%   ( ______--______--______-- )
	 * Sound Length = (64-t1)*(1/256) seconds
	 * The Length value is used only if Bit 6 in NR14 is set.
	 * 
	 * @param v content of NR11 register 
	 */
	public void setNR11(byte v) {
		setNRX1(m_voice1, v);
	}
	
	/**
	 * I/O register NR11 at $FF16 
	 * Same as NR11
	 * The Length value is used only if Bit 6 in NR24 is set.
	 * @param v
	 */
	public void setNR21(byte v) {
		setNRX1(m_voice2, v);
	}

	private void setNRX1(PulseVoice voice, byte v) {
		voice.setRawLength(v & 0b00111111);
		voice.setRawDuty((v >> 6) & 0x03);
	}

	/**
	 * @return content of NR11 APU register
	 */
	public byte getNR11() {
		return getNRX1(m_voice1);
	}
	
	/**
	 * @return content of NR21 APU register
	 */
	public byte getNR21() {
		return getNRX1(m_voice2);
	}

	private byte getNRX1(PulseVoice voice1) {
		return (byte)(((m_voice1.getRawDuty() & 0x03) << 6) | (m_voice1.getRawLength() & 0b00111111));
	}


	/**
	 * I/O register NR12 at $FF12 
	 * Channel 1 Volume Envelope (R/W)
	 * Bit 7-4 - Initial Volume of envelope (0-0Fh) (0=No Sound)
	 * Bit 3   - Envelope Direction (0=Decrease, 1=Increase)
	 * Bit 2-0 - Number of envelope sweep (n: 0-7)
	 *           (If zero, stop envelope operation.)
	 * 
	 * Length of 1 step = n*(1/64) seconds
	 * 
	 * @param v content of NR12 register 
	 */
	public void setNR12(byte v) {
		setNRX2(m_voice1, v);	
	}
	
	/**
	 * I/O register NR22 at $FF17 
	 * 
	 * @param v content of NR12 register 
	 */
	public void setNR22(byte v) {
		setNRX2(m_voice2, v);	
	}
	
	private void setNRX2(PulseVoice voice, byte v) {
		int envelopeSweep = v & 0x03;
		boolean increase = BitUtils.isSet(v, 3);
		int initialEnvelopeVolume = (v >> 4) & 0x0F; // 0 = No sound
		
		// Note : 
		// By Setting the envelope register only nothing will be reflected in the output.
		// Always set the initial flag.
		voice.setEnvelopeIncrease(increase);
		voice.setEnvelopeSweep(envelopeSweep);
		voice.setInitialEnvelopeVolume(initialEnvelopeVolume);
	}
	
	/**
	 * @return content of NR12 APU register
	 */
	public byte getNR12() {
		return getNRX2(m_voice1);
	}
	
	/**
	 * @return content of NR22 APU register
	 */
	public byte getNR22() {
		return getNRX2(m_voice2);
	}

	private byte getNRX2(PulseVoice voice) {
		byte result = (byte) (voice.getEnvelopeSweep() & 0x03);
		result = BitUtils.setBit(result, 3, voice.isEnvelopeIncrease());
		result |= ((voice.getInitialEnvelopeVolume() & 0x0F) << 4);
		return result;

	}

	/**
	 * I/O register NR13 at $FF13 
	 * Channel 1 Frequency lo (Write Only)
	 * Lower 8 bits of 11 bit frequency (x).
	 * Next 3 bit are in NR14 ($FF14)
	 * 
	 * @param v content of NR13 register 
	 */ 
	public void setNR13(byte v) {
		setNRX3(m_voice1, v);
	}
	
	/**
	 * I/O register NR23 at $FF18 
	 * 
	 * @param v content of NR23 register 
	 */ 
	public void setNR23(byte v) {
		setNRX3(m_voice2, v);
	}
	
	private void setNRX3(PulseVoice voice, byte v) {
		// update 3 lower bits
		int frequency = voice.getRawFrequency() & 0x700;
		frequency |= (v & 0xFF);
		voice.setRawFrequency(frequency);
	}
	
	/**
	 * @return content of NR13 APU register (Write only, so $FF)
	 */
	public byte getNR13() {
		return (byte) 0xFF;
	}
	
	/**
	 * @return content of NR23 APU register (Write only, so $FF)
	 */
	public byte getNR23() {
		return (byte) 0xFF;
	}
	
	/**
	 * I/O register NR14 at $FF14 
	 * Channel 1 Frequency hi (R/W)
	 * Bit 7   - Initial (1=Restart Sound)     (Write Only)
	 * Bit 6   - Counter/consecutive selection (Read/Write)
	 *           (1=Stop output when length in NR11 expires)
	 * Bit 2-0 - Frequency's higher 3 bits (x) (Write Only)
	 * 
	 * Frequency = 131072/(2048-x) Hz
	 * 
	 * @param v content of NR14 register 
	 */ 
	public void setNR14(byte v) {
		setNRX4(m_voice1, v);
	}
	
	/**
	 * I/O register NR24 at $FF19 
	 * 
	 * @param v content of NR24 register 
	 */ 
	public void setNR24(byte v) {
		setNRX4(m_voice2, v);
	}
	
	private void setNRX4(PulseVoice voice, byte v) {
		// update 3 upper bits
		int frequency = voice.getRawFrequency() & 0x0FF;
		frequency |= (v & 0x07) << 8;
		voice.setRawFrequency(frequency);
		voice.setLengthEnabled(BitUtils.isSet(v, 6));
		if (BitUtils.isSet(v, 7)) {
			voice.setEnabled(true);
			voice.init();
		}
	}

	/**
	 * @return content of NR14 APU register
	 */
	public byte getNR14() {
		return getNRX4(m_voice1);
	}

	/**
	 * @return content of NR24 APU register
	 */
	public byte getNR24() {
		return getNRX4(m_voice2);
	}

	private byte getNRX4(PulseVoice voice) {
		// the only bit readable is 'length enable', bit 6
		byte result = (byte) 0xFF;
		result = BitUtils.setBit(result, 6, voice.isLengthEnabled());
		return result;
	}
	
	/**
	 * FF30-FF3F - Wave Pattern RAM
	 * Contents - Waveform storage for arbitrary sound data
	 * This storage area holds 32 4-bit samples that are played back upper 4 bits first.
	 * 
	 * @param index the RAM location (in range $00-$0F)
	 * @param v the pair of 4 bits samples
	 */
	public void setWAVData(int index, byte v) {
	}

	public byte getWAVData(int index) {
		return (byte) 0xFF;
	}

	public void reset() {
		m_leftMixer.setVolume(0);
		m_rightMixer.setVolume(0);
		m_soundEnable = false;
		for (Switch voiceSwitch : m_switch) {
			voiceSwitch.setEnabled(false);
		}
	}
}

