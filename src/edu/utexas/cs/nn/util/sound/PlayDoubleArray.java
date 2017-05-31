package edu.utexas.cs.nn.util.sound;
import javax.sound.sampled.AudioFormat;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * This class contains a series of methods that can be used to play a double array
 * as an audio file. Class currently uses static initializer, which should be 
 * changed at some point. 
 * 
 *  @author Isabel Tweraser
 */
public final class PlayDoubleArray {

	/**
	 *  The sample rate - 44,100 Hz for CD quality audio.
	 */
	public static final int SAMPLE_RATE = 44100;

	public static final int BYTES_PER_SAMPLE = 2;                // 16-bit audio
	public static final int BITS_PER_SAMPLE = 16;                // 16-bit audio
	public static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
	private static final int SAMPLE_BUFFER_SIZE = 4096;

	
	public static class AmplitudeArrayPlayer extends Thread {
		private SourceDataLine line;   // to play the sound
		private byte[] buffer;         // our internal buffer
		private int bufferSize = 0;    // number of samples currently in internal buffer
		private double[] samples;

		private boolean playing = false;
		
		/**
		 * If play back is interruptable, then it can be stopped with this command
		 */
		public void stopPlayback() {
			playing = false;
		}
		
		public AmplitudeArrayPlayer(double[] samples) {
			// 44,100 samples per second, 16-bit audio, mono, signed PCM, little Endian
			this(new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false), samples);
				
		}
		
		public AmplitudeArrayPlayer(AudioFormat format, double[] samples) {
			changeAudioFormat(format);
			this.samples = samples;
		}
		
		public void changeAudioFormat(AudioFormat format) {
			if(line != null) line.close();
			try {
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

				line = (SourceDataLine) AudioSystem.getLine(info);
				//TODO: sample buffer size may need to be a parameter
				line.open(format, SAMPLE_BUFFER_SIZE * format.getFrameSize());

				// the internal buffer is a fraction of the actual buffer size, this choice is arbitrary
				// it gets divided because we can't expect the buffered data to line up exactly with when
				// the sound card decides to push out its samples.
				buffer = new byte[SAMPLE_BUFFER_SIZE * format.getFrameSize()/3];
			}
			catch (LineUnavailableException e) {
				System.out.println(e.getMessage());
			}

			// no sound gets made before this call
			line.start();		
		}
		
		/**
		 * Closes standard audio.
		 */
		public void close() {
			line.drain();
			line.stop();
		}
		
		/**
		 * Writes one sample (between -1.0 and +1.0) to standard audio.
		 * If the sample is outside the range, it will be clipped.
		 *
		 * @param  sample the sample to play
		 * @throws IllegalArgumentException if the sample is {@code Double.NaN}
		 */
		public void playDouble(double sample) {

			// clip if outside [-1, +1]
			if (Double.isNaN(sample)) throw new IllegalArgumentException("sample is NaN");
			if (sample < -1.0) sample = -1.0;
			if (sample > +1.0) sample = +1.0;

			// convert to bytes
			short s = (short) (MAX_16_BIT * sample);
			buffer[bufferSize++] = (byte) s;
			buffer[bufferSize++] = (byte) (s >> 8);   // little Endian

			// send to sound card if buffer is full        
			if (bufferSize >= buffer.length) {
				line.write(buffer, 0, buffer.length);
				bufferSize = 0;
			}
		}
		
		public void run() {
			playing = true;
			for (int i = 0; playing && i < samples.length; i++) {
				playDouble(samples[i]);
			}				
		}
	}

	

	

	/**
	 * Writes the array of samples (between -1.0 and +1.0) to standard audio.
	 * If a sample is outside the range, it will be clipped.
	 *
	 * @param  samples the array of samples to play
	 * @throws IllegalArgumentException if any sample is {@code Double.NaN}
	 * @throws IllegalArgumentException if {@code samples} is {@code null}
	 */
	public static void playDoubleArray(double[] samples) {
		playDoubleArray(samples, true); // allow interrupting by default
	}
	
	/**
	 * Writes the array of samples (between -1.0 and +1.0) to standard audio.
	 * If a sample is outside the range, it will be clipped. Will play
	 * sample fully if interruptions are not allowed, but samples can be 
	 * interrupted if input boolean is set to true
	 *
	 * @param  samples the array of samples to play
	 * @param  allowInterrupt dicates whether sounds being played can be interrupted by another
	 * sound before they finish playing
	 * @throws IllegalArgumentException if any sample is {@code Double.NaN}
	 * @throws IllegalArgumentException if {@code samples} is {@code null}
	 */

	// TOD: Eventually delete this version of the method
	public static void playDoubleArray(double[] samples, boolean allowInterrupt) {
		if (samples == null) throw new IllegalArgumentException("argument to play() is null");
		AmplitudeArrayPlayer aap = new AmplitudeArrayPlayer(samples);
		// TODO: Big changes needed here soon
		
		if(allowInterrupt) aap.start(); // Launches in new Thread
		else aap.run(); // Just executes the code sequentially
	}
	

	public static void playDoubleArray(AudioFormat format, double[] samples) {
		playDoubleArray(format, samples, false);
	}	
	
	public static void playDoubleArray(AudioFormat format, double[] samples, boolean allowInterrupt) {
		if (samples == null) throw new IllegalArgumentException("argument to play() is null");
		AmplitudeArrayPlayer aap = new AmplitudeArrayPlayer(format,samples);
		// TODO: Big changes needed here soon
		
		if(allowInterrupt) aap.start(); // Launches in new Thread
		else aap.run(); // Just executes the code sequentially
	}

}