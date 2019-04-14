package com.s2soft.tinygb.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFileFormat.Type;

public class AudioDevice implements IAudioDevice {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private AudioFormat m_format;
	
	private int m_bufferSamplesCount;
	private byte[] m_buffer;
	private int m_bufferIndex;

	private int m_counter; // to synch machine clock with audio sample rate

	private SourceDataLine m_auline;

	private ByteArrayOutputStream m_recordedOutputStream;

	//	 =========================== Constructor =============================

	public AudioDevice(int sampleRate) {
		m_format = new AudioFormat(sampleRate, 8, 2, true, true);
//		m_format = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 44100, 8, 2, 2, 44100, false);
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
		
		m_bufferSamplesCount = 256;
		m_buffer = new byte[m_format.getChannels() * m_bufferSamplesCount * m_format.getSampleSizeInBits() / 8];
		m_bufferIndex = 0;
		
		m_recordedOutputStream = new ByteArrayOutputStream();
	}
	
	@Override
	public void stop() {
		m_auline.drain();
		m_auline.close();
	}

	@Override
	public void putSample(byte leftSample, byte rightSample) {
//		if (1==1) return;
		m_counter = m_counter % (int)(4194304 / m_format.getSampleRate());
		if (m_counter == 0) {
			m_buffer[m_bufferIndex++] = leftSample;
			m_buffer[m_bufferIndex++] = rightSample;
			if (m_bufferIndex == m_buffer.length) {
				m_auline.write(m_buffer, 0, m_buffer.length);
				m_recordedOutputStream.write(m_buffer, 0, m_buffer.length);
				m_bufferIndex = 0;
				
//				if (m_recordedOutputStream.size() == 256 * 8000) {
//					System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
//					try {
//						byte[] byteArray = m_recordedOutputStream.toByteArray();
//						AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(byteArray), m_format, byteArray.length / m_format.getFrameSize());
//						OutputStream outputStream = new FileOutputStream("output.wav");
//						AudioSystem.write(audioInputStream, Type.WAVE, outputStream);
//						audioInputStream.close();
//						outputStream.flush();
//						outputStream.close();
//					} 
//					catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
			}
		}
		m_counter++;
	}
}


