package com.s2soft.tinygb.apu;

import com.s2soft.tinygb.GameBoy;
import com.s2soft.tinygb.cpu.Instruction;
import com.s2soft.tinygb.mmu.GBMemory;
import com.s2soft.utils.BitUtils;

public class GBAPU {

	//   ============================ Constants ==============================

	public final static boolean TRACE = false;

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

	// The Vin signal is received from the game cartridge bus, allowing external hardware 
	// in the cartridge to supply a fifth sound channel, additionally to the gameboys internal four channels. 
	// This feature does not seem to be used by any existing games.
	private boolean m_vinLeft;
	private boolean m_vinRight;

	//	 =========================== Constructor =============================

	public GBAPU(GameBoy gameBoy) {
		m_gameBoy = gameBoy;
		m_memory = m_gameBoy.getMemory();
		
		for (int i=0;i<4;i++) {
			m_switch[i] = new Switch("Right channel, voice " + i); // right channel switch for voice i
			m_switch[i].setInput(m_voices[i].getDAC());
			m_rightMixer.addInput(m_switch[i]);
			m_switch[i+4] = new Switch("Left channel, voice " + i); // left channel switch for voice i
			m_switch[i+4].setInput(m_voices[i].getDAC());
			m_leftMixer.addInput(m_switch[i+4]);
		}
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	public void step() {
		if (!m_soundEnable) {
			return;
		}
		
		m_voice1.step();
		m_voice2.step();
		m_voice3.step();
		m_voice4.step();
		
		if (m_gameBoy.getEmulationSyncShift() > -5) {
			final double leftChannelValue = getLeftChannelValue();
			final double rightChannelValue = getRightChannelValue();
			m_gameBoy.getAudioDevice().putSample(leftChannelValue / 2.0, rightChannelValue / 2.0);
		}
	}

	
	public double getLeftChannelValue() {
		return m_leftMixer.getValue();
	}
	
	public double getRightChannelValue() {
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
		if (!m_soundEnable) { return; }
		m_leftMixer.setVolume((v >> 4) & 0x07);
		m_rightMixer.setVolume(v & 0x07);
		// Vin L and Vin R are not handled
		m_vinLeft = BitUtils.isSet(v, 7);
		m_vinRight = BitUtils.isSet(v, 3);
	}
	
	/**
	 * @return content of NR50 APU register
	 */
	public byte getNR50() {
		byte result = (byte) (((m_leftMixer.getVolume() & 0x07) << 4) | (m_rightMixer.getVolume() & 0x07));
		result = BitUtils.setBit(result, 3, m_vinRight);
		result = BitUtils.setBit(result, 7, m_vinLeft);
		return result;
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
		if (!m_soundEnable) { return; }
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
		boolean soundEnable = BitUtils.isSet(v, 7);
		if (TRACE) {
			System.out.println("Turning sound : " + (soundEnable ? "ON" : "OFF"));
		}
		if (wasEnabled && !soundEnable) {
			// destroys the contents of all sound registers
			stopSound();
		}
		else if (!wasEnabled && soundEnable) {
			startSound();
		}
		m_soundEnable = soundEnable;
	}
	
	/**
	 * @return content of NR52 APU register
	 */
	public byte getNR52() {
		byte result = (byte) 0xFF;
		result = BitUtils.setBit(result, 7, m_soundEnable);
		for (int i=0;i<4;i++) {
			result = BitUtils.setBit(result, i, m_voices[i].isEnabled() && m_voices[i].getDAC().isEnabled());
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
		if (TRACE) {
			System.out.println("NR10="+Instruction.toHexByte(v));
		}
		if (!m_soundEnable) { return; }
		byte sweepShift = (byte)(v & 0x07);
		boolean increase = BitUtils.isSet(v, 3);
		byte sweepTime = (byte)((v >> 4) & 0x07);
		m_voice1.setSweepShift(sweepShift);
		m_voice1.setSweepIncrease(!increase);
		m_voice1.setSweepTime(sweepTime);
	}

	public byte getNR10() {
		byte result = (byte) 0b10000000;
		result |= (m_voice1.getSweepShift() & 0x07);
		result |= ((m_voice1.getSweepTime() & 0x07) << 4) ;
		result = BitUtils.setBit(result, 3, !m_voice1.isSweepIncrease());
		return result;
	}
	
	/**
	 * I/O register NR30 at $FF1A 
	 * Channel 3 Sound on/off (R/W)
	 * Bit 7 - Sound Channel 3 Off  (0=Stop, 1=Playback)  (Read/Write)
	 * 
	 * @param v content of NR11 register 
	 */
	public void setNR30(byte v) {
//		if (TRACE) {
//			System.out.println("NR30="+Instruction.toHexByte(v));
//		}
		if (!m_soundEnable) { return; }
		boolean playback = BitUtils.isSet(v, 7);
//		m_voice3.setPlayback(playback);
		m_voice3.getDAC().setEnabled(playback);
	}
	
	public byte getNR30() {
		byte result = (byte) 0xFF;
		result = BitUtils.setBit(result, 7, m_voice3.getDAC().isEnabled());
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
		if (TRACE) {
			System.out.println("NR11="+Instruction.toHexByte(v));
		}
		
		// Write is allowed even if APU is off (DMG only !)
//		if (!m_soundEnable) { return; }
		if (!m_soundEnable) { v &= 0x3F; } // clear square duty
		setNRX1(m_voice1, v);
	}
	
	/**
	 * I/O register NR21 at $FF16 
	 * Same as NR11
	 * The Length value is used only if Bit 6 in NR24 is set.
	 * @param v
	 */
	public void setNR21(byte v) {
		// Write is allowed even if APU is off (DMG only !)
//		if (!m_soundEnable) { return; }
		if (!m_soundEnable) { v &= 0x3F; } // clear square duty
		setNRX1(m_voice2, v);
	}
	
	/**
	 * I/O register NR31 at $FF1B 
	 * Channel 3 Sound Length
	 * 
	 * Sound Length = (256-t1)*(1/256) seconds.
	 * This value is used only if Bit 6 in NR34 is set.
	 * 
	 * @param v
	 */
	public void setNR31(byte v) {
		if (TRACE) {
			System.out.println("NR31="+Instruction.toHexByte(v));
		}
		// Write is allowed even if APU is off (DMG only !)
//		if (!m_soundEnable) { return; }
		m_voice3.setRawLength(v & 0xFF);
	}
	
	/**
	 * I/O register NR41 at $FF20 
	 * Channel 4 Sound Length (R/W)
	 * Bit 5-0 - Sound length data (t1: 0-63)
	 * Sound Length = (64-t1)*(1/256) seconds The Length value is used only if Bit 6 in NR44 is set.
	 * 
	 * @param v
	 */
	public void setNR41(byte v) {
		// Write is allowed even if APU is off (DMG only !)
//		if (!m_soundEnable) { return; }
		m_voice4.setRawLength(v & 0b00111111);
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
	
	/**
	 * @return content of NR31 APU register
	 */
	public byte getNR31() {
		return (byte)0xFF;
	}

	/**
	 * Read Channel 4 Sound Length (R/W)
	 * @return content of NR41 APU register
	 */
	public byte getNR41() {
//		byte result = (byte) 0b11000000;
//		result |= (m_voice4.getRawLength() & 0b00111111);
//		return result;
		// Note: some specs say that this registered is readable
		// Blargg tests assume this one is masked...
		return (byte) 0xFF;
	}
	
	private byte getNRX1(PulseVoice voice1) {
		return (byte)(((m_voice1.getRawDuty() & 0x03) << 6) | 0b00111111);
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
		if (TRACE) {
			System.out.println("NR12="+Instruction.toHexByte(v));
		}
		if (!m_soundEnable) { return; }
		setNRX2(m_voice1, v);	
	}
	
	/**
	 * I/O register NR22 at $FF17 
	 * 
	 * @param v content of NR12 register 
	 */
	public void setNR22(byte v) {
		 if (!m_soundEnable) { return; }
		setNRX2(m_voice2, v);	
	}
	
	/**
	 * I/O register NR32 at $FF1C
	 * Channel 3 Select output level (R/W)
	 * Bit 6-5 - Select output level (Read/Write)
	 * 
	 * Possible Output levels are:
	 *  0: Mute (No sound)
	 *  1: 100% Volume (Produce Wave Pattern RAM Data as it is)
	 *  2:  50% Volume (Produce Wave Pattern RAM data shifted once to the right)
	 *  3:  25% Volume (Produce Wave Pattern RAM data shifted twice to the right)
	 * 
	 * @param v content of NR12 register 
	 */
	public void setNR32(byte v) {
		 if (!m_soundEnable) { return; }
		m_voice3.setOutputLevel((v >> 5) & 0x03);
	}
	
	/**
	 * I/O register NR42 at $FF21
	 * Channel 4 Volume Envelope (R/W) 
	 * 
	 * @param v content of NR12 register 
	 */
	public void setNR42(byte v) {
		if (!m_soundEnable) { return; }
		setNRX2(m_voice4, v);	
	}
	
	private void setNRX2(IVolumeEnveloppeVoice voice, byte v) {
		int envelopeSweep = v & 0x07;
		boolean increase = BitUtils.isSet(v, 3);
		int initialEnvelopeVolume = (v >> 4) & 0x0F; // 0 = No sound
		voice.getDAC().setEnabled((v & 0b11111000) != 0);
		
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
	
	/**
	 * @return content of NR32 APU register
	 */
	public byte getNR32() {
		byte result = (byte) 0b10011111;
		result |= (byte) ((m_voice3.getOutputLevel() & 0x03) << 5);
		return result;
	}

	/**
	 * @return content of NR42 APU register
	 */
	public byte getNR42() {
		return getNRX2(m_voice4);
	}

	private byte getNRX2(IVolumeEnveloppeVoice voice) {
		byte result = (byte) (voice.getEnvelopeSweep() & 0x07);
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
		if (!m_soundEnable) { return; }
		setNRX3(m_voice1, v);
	}
	
	/**
	 * I/O register NR23 at $FF18 
	 * 
	 * @param v content of NR23 register 
	 */ 
	public void setNR23(byte v) {
		if (!m_soundEnable) { return; }
		setNRX3(m_voice2, v);
	}
	
	/**
	 * I/O register NR33 at $FF33 
	 * Channel 3 Frequency's lower data (W)
	 * 
	 * Lower 8 bits of an 11 bit frequency (x).
	 * 
	 * @param v content of NR23 register 
	 */ 
	public void setNR33(byte v) {
		if (!m_soundEnable) { return; }
		setNRX3(m_voice3, v);
	}
	
	/**
	 * I/O register NR43 at $FF22 
	 * Channel 4 Polynomial Counter (R/W)
	 * The amplitude is randomly switched between high and low at the given frequency. 
	 * A higher frequency will make the noise to appear 'softer'. 
	 * When Bit 3 is set, the output will become more regular, and some frequencies will sound more like Tone than Noise.
	 * 
	 * Bit 7-4 - Shift Clock Frequency (s)
	 * Bit 3   - Counter Step/Width (0=15 bits, 1=7 bits)
	 * Bit 2-0 - Dividing Ratio of Frequencies (r)
	 * 
	 * Frequency = 524288 Hz / r / 2^(s+1) ;For r=0 assume r=0.5 instead
	 * 
	 * @param v content of NR43 register 
	 */ 
	public void setNR43(byte v) {
		if (!m_soundEnable) { return; }
		byte dividingRatio = (byte) (v & 0x07);
		boolean counterStepWidth = BitUtils.isSet(v, 3);
		byte shiftClockFrequency = (byte) ((v >> 4) & 0x0F);
		m_voice4.setDividingRatio(dividingRatio);
		m_voice4.setCounterStepWidth(counterStepWidth);
		m_voice4.setShiftClockFrequency(shiftClockFrequency);
	}
	
	private void setNRX3(Voice voice, byte v) {
		if (!m_soundEnable) { return; }
		// update 8 lower bits
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
	 * @return content of NR33 APU register (Write only, so $FF)
	 */
	public byte getNR33() {
		return (byte) 0xFF;
	}
	
	/**
	 * @return content of NR34 APU register
	 */
	public byte getNR34() {
		byte result = (byte) 0xFF;
		result = BitUtils.setBit(result, 6, m_voice3.isLengthEnabled());
		return result;
	}

	/**
	 * @return content of NR43 APU register
	 */
	public byte getNR43() {
		byte result = (byte) (m_voice4.getDividingRatio() & 0x07);
		result |= (m_voice4.getShiftClockFrequency() & 0x0F) << 4;
		result = BitUtils.setBit(result, 3, m_voice4.isCounterStepWidth());
		return result;
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
		if (TRACE) {
			System.out.println("NR14="+Instruction.toHexByte(v));
		}
		if (!m_soundEnable) { return; }
		setNRX4(m_voice1, v);
	}
	
	/**
	 * I/O register NR24 at $FF19 
	 * 
	 * @param v content of NR24 register 
	 */ 
	public void setNR24(byte v) {
		if (!m_soundEnable) { return; }
		setNRX4(m_voice2, v);
	}
	
	/**
	 * I/O register NR34 at $FF1E
	 * Channel 3 Frequency's higher data (R/W)
	 * 
	 * Bit 7   - Initial (1=Restart Sound)     (Write Only)
	 * Bit 6   - Counter/consecutive selection (Read/Write)
	 *           (1=Stop output when length in NR31 expires)
	 * Bit 2-0 - Frequency's higher 3 bits (x) (Write Only)
	 * 
	 * Frequency = 4194304/(64*(2048-x)) Hz = 65536/(2048-x) Hz
	 * 
	 * @param v content of NR34 register 
	 */ 
	public void setNR34(byte v) {
//		System.out.println("NR34="+Instruction.toHexByte(v));
		if (!m_soundEnable) { return; }
		setNRX4(m_voice3, v);
	}
	
	/**
	 * I/O register NR44 at $FF23 
	 * Channel 4 Counter/consecutive; Inital (R/W)
	 * 
	 * Bit 7   - Initial (1=Restart Sound)     (Write Only)
	 * Bit 6   - Counter/consecutive selection (Read/Write)
	 *           (1=Stop output when length in NR41 expires)
	 * 
	 * @param v content of NR44 register 
	 */ 
	public void setNR44(byte v) {
		if (!m_soundEnable) { return; }
		m_voice4.setLengthEnabled(BitUtils.isSet(v, 6), BitUtils.isSet(v, 7));
		if (BitUtils.isSet(v, 7)) {
			m_voice4.setEnabled(true);//m_voice4.getDAC().isEnabled());
			m_voice4.trigger();
		}
	}
	
	private void setNRX4(Voice voice, byte v) {
		// update 3 upper bits
		int frequency = voice.getRawFrequency() & 0x0FF;
		frequency |= (v & 0x07) << 8;
		voice.setRawFrequency(frequency);
		voice.setLengthEnabled(BitUtils.isSet(v, 6), BitUtils.isSet(v, 7));
		if (BitUtils.isSet(v, 7)) {
			voice.trigger();
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
	
	/**
	 * @return content of NR44 APU register
	 */
	public byte getNR44() {
		return getNRX4(m_voice4);
	}

	private byte getNRX4(Voice voice) {
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
		if (!m_soundEnable) { return; }
		m_voice3.setWAVData(index, v);
	}

	public byte getWAVData(int index) {
		return m_voice3.getWAVData(index);
	}
	
	private void startSound() {
		try { m_gameBoy.getAudioDevice().start(); }
		catch (Exception e) {
			e.printStackTrace();
		}
		for (Voice voice : m_voices) {
			voice.start();
		}
	}

	public void stopSound() {
		m_gameBoy.getAudioDevice().stop();
		m_leftMixer.setVolume(0);
		m_rightMixer.setVolume(0);
		for (Switch voiceSwitch : m_switch) {
			voiceSwitch.setEnabled(false);
		}
		for (Voice voice : m_voices) {
			voice.setEnabled(false);
		}
		byte nullValue = 0;

		setNR10(nullValue);
		setNR11((byte) (m_voice1.getRawLength() & 0x3F)); // Not reset for DMG
//		setNR11(nullValue);
		setNR12(nullValue);
		setNR13(nullValue);
		setNR14(nullValue);
		setNR21((byte) (m_voice2.getRawLength() & 0x3F)); // Not reset for DMG
//		setNR21(nullValue);
		setNR22(nullValue);
		setNR23(nullValue);
		setNR24(nullValue);
		setNR30(nullValue);
		setNR31((byte) (m_voice3.getRawLength())); // Not reset for DMG
//		setNR31(nullValue);
		setNR32(nullValue);
		setNR33(nullValue);
		setNR34(nullValue);
		setNR41((byte) (m_voice4.getRawLength() & 0x3F)); // Not reset for DMG
//		setNR41(nullValue);
		setNR42(nullValue);
		setNR43(nullValue);
		setNR44(nullValue);
		setNR50(nullValue);
		setNR51(nullValue);
		
		m_soundEnable = false;
	}
}

