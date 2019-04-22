package com.s2soft.tinygb.apu;

public class DAC implements ISoundProvider {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private Voice m_voice;
	
	private boolean m_enabled = false;
	
	private double capacitor = 0.0;

	//	 =========================== Constructor =============================

	public DAC(Voice voice) {
		m_voice = voice;
	}

	//	 ========================== Access methods ===========================

	public void setEnabled(boolean enabled) {
		if (!enabled) {
			m_voice.setEnabled(false);
		}
		m_enabled = enabled;
		if (GBAPU.TRACE) {
			System.out.println(m_voice.getName() + ". DAC enabled = " + m_enabled);
		}
	}

	public boolean isEnabled() {
		return m_enabled;
	}

	//	 ========================= Treatment methods =========================

	private double passHigh(double value) {

	     double out = value - capacitor;
	         
         // capacitor slowly charges to 'value' via their difference
         capacitor = value - out * 0.999958; // use 0.998943 for MGB&CGB

         return out;
	}
	
	@Override
	public double getValue() {
		int value = m_voice.getValue();
		if (value != (value & 0x0F)) throw new IllegalStateException();
		double voiceDigitalOutput = -1.0 + 2* (value / 15.0f);
		return (m_enabled /*&& m_voice.isEnabled()*/) ? passHigh(voiceDigitalOutput) : 0;
//		return (m_enabled /*&& m_voice.isEnabled()*/) ? (-1.0 + 2* (value / 15.0f)) : 0;
	}
	
	public static void main(String[] args) {
		Voice voice = new PulseVoice() {
			protected String getName() {
				return "test";
			}
		};
		voice.setEnabled(true);
		voice.getDAC().setEnabled(true);
		Voice voice1 = voice;
		Voice voice2 = voice;
		Voice voice3 = voice;
		Voice voice4 = voice;
		
		Switch switch1 = new Switch("1");
		switch1.setInput(voice1.getDAC());
		switch1.setEnabled(true);
		
		Switch switch2 = new Switch("2");
		switch2.setInput(voice2.getDAC());
		switch2.setEnabled(true);
		
		Switch switch3 = new Switch("3");
		switch3.setInput(voice3.getDAC());
		switch3.setEnabled(true);
		
		Switch switch4 = new Switch("4");
		switch4.setInput(voice4.getDAC());
		switch4.setEnabled(true);
		
		Mixer mixer = new Mixer("mixer");
		mixer.addInput(switch1);
		mixer.addInput(switch2);
		mixer.addInput(switch3);
		mixer.addInput(switch4);
		mixer.setVolume(7);
		
		voice1.setOutputValue(0);
		System.out.println(mixer.getValue());
		voice1.setOutputValue(15);
		System.out.println(mixer.getValue());
	}
}

