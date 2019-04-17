package com.s2soft.tinygb.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;

public class AudioDevice implements IAudioDevice {

	//   ============================ Constants ==============================

	private final static boolean RECORD_BOOT = false;
	
	//	 =========================== Attributes ==============================

	private AudioFormat m_format;
	
	private int m_bufferSamplesCount;
	private byte[] m_buffer;
	private int m_bufferIndex;

	private int m_counter; // to synch machine clock with audio sample rate

	private SourceDataLine m_auline;

	private ByteArrayOutputStream m_recordedOutputStream;

	private int m_resolution;

	private byte[] m_sample;

	//	 =========================== Constructor =============================

	public AudioDevice(int sampleRate, int resolution) {
		m_resolution = resolution;
		m_format = new AudioFormat(sampleRate, m_resolution, 2, true, true);
//		m_format = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 44100, 8, 2, 2, 44100, false);
		m_sample = new byte[resolution / 8];
	}

	//	 ========================== Access methods ===========================

	//	 ========================= Treatment methods =========================

	// =========================== Access methods =========================
	
	// ========================= Threatment methods =======================

	public void start() throws Exception {
		m_counter = 0;
		
		Info info = new DataLine.Info(SourceDataLine.class, m_format);
		m_auline = (SourceDataLine) AudioSystem.getLine(info);
		m_auline.open(m_format);
		m_auline.start();	
		
		m_bufferSamplesCount = 32;
		m_buffer = new byte[m_format.getChannels() * m_bufferSamplesCount * m_format.getSampleSizeInBits() / 8];
		m_bufferIndex = 0;
		
		if (RECORD_BOOT) {
			m_recordedOutputStream = new ByteArrayOutputStream();
		}
	}
	
	@Override
	public void stop() {
		if (m_auline != null) {
			m_auline.flush();
			m_auline.close();
		}
	}

	public byte[] getSample(double normalizedValue) {
		if (m_resolution == 8) {
			m_sample[0] = (byte)quantize(normalizedValue, 8);
		}
		else if (m_resolution == 16) {
			int quantized = quantize(normalizedValue, 16);
			m_sample[0] = (byte) (quantized >> 8);
			m_sample[1] = (byte) (quantized & 0xFF);
		}
		else if (m_resolution == 24) {
			int quantized = quantize(normalizedValue, 24);
			m_sample[0] = (byte) (quantized >> 16);
			m_sample[1] = (byte) ((quantized >> 8) & 0xFF);
			m_sample[2] = (byte) (quantized & 0xFF);
		}
		return m_sample;
	}
	
	private final int quantize(double normalizedValue, int resolution) {
		int sample = (int) Math.round(Math.scalb(normalizedValue, resolution-1));
		sample = (int) Math.min(Math.pow(2, resolution) / 2 - 1, sample);
		sample = (int) Math.max(-Math.pow(2, resolution) / 2, sample);
		return sample;
	}
	
	@Override
	public void putSample(double leftSignal, double rightSignal) {
		m_counter = m_counter % (int)(4194304 / m_format.getSampleRate());
		if (m_counter == 0) {

			leftSignal = Math.min(Math.max(-1, leftSignal), 1);
			rightSignal = Math.min(Math.max(-1, rightSignal), 1);

			byte[] leftSample = getSample(leftSignal);
			System.arraycopy(leftSample, 0, m_buffer, m_bufferIndex, leftSample.length);
			m_bufferIndex += leftSample.length;
			
			byte[] rightSample = getSample(rightSignal);
			System.arraycopy(rightSample, 0, m_buffer, m_bufferIndex, rightSample.length);
			m_bufferIndex += rightSample.length;
			
//			m_buffer[m_bufferIndex++] = (byte)quantize(leftSignal, 8);
//			m_buffer[m_bufferIndex++] = (byte)quantize(rightSignal, 8);
			
			if (m_bufferIndex == m_buffer.length) {
				if (m_auline.available() < m_buffer.length) {
					System.out.println("Warning : missing " + (m_buffer.length - (m_auline.available()) + " in input audio line"));
				}
				m_auline.write(m_buffer, 0, m_buffer.length);
				m_bufferIndex = 0;
				
				if (RECORD_BOOT) {
					m_recordedOutputStream.write(m_buffer, 0, m_buffer.length);
					
					if (m_recordedOutputStream.size() == 256 * 8000) {
						System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
						try {
							byte[] byteArray = m_recordedOutputStream.toByteArray();
							AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(byteArray), m_format, byteArray.length / m_format.getFrameSize());
							OutputStream outputStream = new FileOutputStream("output.wav");
							AudioSystem.write(audioInputStream, Type.WAVE, outputStream);
							audioInputStream.close();
							outputStream.flush();
							outputStream.close();
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		m_counter++;
	}
}


