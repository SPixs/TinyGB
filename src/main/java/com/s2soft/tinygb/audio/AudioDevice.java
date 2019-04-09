package com.s2soft.tinygb.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;

public class AudioDevice implements IAudioDevice {

	//   ============================ Constants ==============================

	//	 =========================== Attributes ==============================

	private AudioFormat m_format;
	
	private int m_bufferSamplesCount;
	private byte[] m_buffer;
	private int m_bufferIndex;

	private int m_counter; // to synch machine clock with audio sample rate

	private SourceDataLine m_auline;

	//	 =========================== Constructor =============================

	public AudioDevice(int sampleRate) {
		m_format = new AudioFormat(sampleRate, 8, 2, true, true);
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
	}

	@Override
	public void putSample(byte leftSample, byte rightSample) {
		m_counter = m_counter % (int)(4194304 / m_format.getSampleRate());
		if (m_counter == 0) {
			m_buffer[m_bufferIndex++] = leftSample;
			m_buffer[m_bufferIndex++] = rightSample;
			if (m_bufferIndex == m_buffer.length) {
				m_auline.write(m_buffer, 0, m_buffer.length);
				m_bufferIndex = 0;
			}
		}
		m_counter++;
	}
	
	
	
//		m_consumerThread = new Thread() {
//			public void run() {
//
//				long samplesCount = 0;
//				notifyTime(0);
//				byte[] emptyBuffer = new byte[m_format.getSampleSizeInBits() / 8];
//				m_shouldStop = false;
//				
//				// create a buffer that can hold 20ms of data
//				final float sampleRate = m_format.getSampleRate();
//				int bufferSamplesCount = 256;//(int) Math.round(sampleRate * 0.020 / 10.0d);
////				int bufferSamplesCount = (int) Math.round(sampleRate * 0.01125);
//				byte[] buffer = new byte[m_format.getChannels() * bufferSamplesCount * m_format.getSampleSizeInBits() / 8];
//				int notificationsCount = 0;
//				
//				long startTime = System.currentTimeMillis();
//				
//				while (!m_shouldStop) {
//					for (int i=0;i<bufferSamplesCount;i++) {
//						double time = samplesCount / sampleRate;
//						if (m_format.getChannels() == 2) {
//							byte[] leftSample = (m_leftSampler == null) ? emptyBuffer : m_leftSampler.getSample(time);
//							byte[] rightSample = (m_rightSampler == null) ? emptyBuffer : m_rightSampler.getSample(time);
//							System.arraycopy(leftSample, 0, buffer, leftSample.length * i * 2, leftSample.length);
//							System.arraycopy(rightSample, 0, buffer, rightSample.length * (i * 2 + 1), rightSample.length);
//						}
//						else {
//							byte[] sample = (m_monoSampler == null) ? emptyBuffer : m_monoSampler.getSample(time);
//							System.arraycopy(sample, 0, buffer, sample.length * i, sample.length);
//						}
//						samplesCount++;
//						
//						if (samplesCount >= ((notificationsCount + 1) * 0.020d) * sampleRate) {
//							notificationsCount++;
//							notifyTime(samplesCount / sampleRate);
//						}
//						
//						long elapsed = System.currentTimeMillis() - startTime;
//						long wait = (long) (((samplesCount * 1000)/ sampleRate) - elapsed);
//						if (wait > 0) {
//							try { Thread.sleep(wait); } 	
//							catch (InterruptedException e) {}
//						}
//					}
//					
//					auline.write(buffer, 0, buffer.length);
//				}
//				
//				auline.drain();
//				auline.close();
//			};
//		};
//		
//		m_consumerThread.start();
//	}
}


