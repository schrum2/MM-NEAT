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
	public static final double MAX_8_BIT = 256;
	private static final int SAMPLE_BUFFER_SIZE = 4096;
	
	// The format below is based on a 16 bit, 44100 Hz, mono, little Endian audio file. This default
	// AudioFormat is used for generated frequencies, such as frequencies from the breedesizer,
	// but is not acceptable to use when converting an input audio file because the sound cannot be 
	// replicated if the AudioFormat doesn't exactly match the AudioFormat of the input file. 
	public static final AudioFormat DEFAULT_AUDIO_FORMAT = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
	
	public static class AmplitudeArrayPlayer extends Thread {
		private SourceDataLine line;   // to play the sound
		private byte[] buffer;         // our internal buffer
		private int bufferSize = 0;    // number of samples currently in internal buffer
		private double[] samples;
		private int bitNum;            // number of bits in audio (typically 16 or 8)

		private boolean playing = false;
		
		/**
		 * If play back is interruptable, then it can be stopped with this command
		 */
		public void stopPlayback() {
			playing = false;
		}
		
		/**
		 * Constructor that uses default AudioFormat (typically only for originally 
		 * generated wave amplitudes that don't have an accessible AudioFormat
		 * 
		 * @param samples double array representation of generated sound
		 */
		public AmplitudeArrayPlayer(double[] samples) {
			// 44,100 samples per second, 16-bit audio, mono, signed PCM, little Endian
			this(DEFAULT_AUDIO_FORMAT, samples);
				
		}
		
		/**
		 * Constructor that takes in AudioFormat and representative double array of an 
		 * audio file and changes the audio format so it can be used by the 
		 * SourceDataLine
		 * 
		 * @param format AudioFormat of audio
		 * @param samples double array representation of audio
		 */
		public AmplitudeArrayPlayer(AudioFormat format, double[] samples) {
			changeAudioFormat(format);
			this.samples = samples;
		}
		
		/**
		 * Method that changes the audio format being used for the SourceDataLine to 
		 * the specific audio format of the file being played. 
		 * 
		 * @param format AudioFormat of input audio
		 */
		public void changeAudioFormat(AudioFormat format) {
			if(line != null) line.close();
			try {
				bitNum = format.getSampleSizeInBits();
				//System.out.println("bitNum:"+bitNum);
				
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
				System.exit(1);
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

			// convert to bytes: Assume can only be 16 or 8
			// TODO: Handle 32-bit?
			// short s = (short) (MAX_16_BIT * sample);
			short s = (short) ((bitNum == 16 ? MAX_16_BIT : MAX_8_BIT) * sample);
			buffer[bufferSize++] = (byte) s;
			if(bitNum == 16)
				buffer[bufferSize++] = (byte) (s >> 8);   // little Endian

			// send to sound card if buffer is full        
			if (bufferSize >= buffer.length) {
				line.write(buffer, 0, buffer.length);
				bufferSize = 0;
			}
		}
		
		/**
		 * Loops through array of doubles and plays it as audio
		 * using playDouble(). When loop is exited, playing should be
		 * set to false. 
		 */
		public void run() {
			playing = true;
			for (int i = 0; playing && i < samples.length; i++) {
				playDouble(samples[i]);
			}		
			playing = false;
		}
		
		/**
		 * Method to access whether sound is playing or not
		 * @return true if playing, false if not
		 */
		public boolean isPlaying() {
			return playing;
		}
	}	
	
	/**
	 * Writes the array of samples (between -1.0 and +1.0) to standard audio.
	 * If a sample is outside the range, it will be clipped. Will play
	 * sample fully if interruptions are not allowed, but samples can be 
	 * interrupted if input boolean is set to true
	 *
	 * @param  format the AudioFormat of the specified audio (preset if a generated amplitude from
	 * breedesizer is being played
	 * @param  samples the array of samples to play
	 * @param  allowInterrupt dicates whether sounds being played can be interrupted by another
	 * sound before they finish playing
	 * @return AmplitudeArrayPlayer instance that plays audio or null if interruption is not allowed.
	 * Plays audio regardless
	 */
	
	public static AmplitudeArrayPlayer playDoubleArray(AudioFormat format, double[] samples, boolean allowInterrupt) {
		if (samples == null) throw new IllegalArgumentException("argument to play() is null");
		AmplitudeArrayPlayer aap = new AmplitudeArrayPlayer(format,samples);
		if(allowInterrupt) {
			aap.start(); // Launches in new Thread
			return aap; // Can be stopped via the returned AmplitudeArrayPlayer
		} else {
			aap.run(); // Play to completion
			return null; // then return null
		}
	}
	
	/**
	 * Plays double array given the input audio format. Allows interruption by default.
	 * This is used for audio being converted from an input file.
	 * 
	 * @param format AudioFormat of input file
	 * @param samples double array representing audio file
	 * @return AmplitudeArrayPlayer instance that plays audio
	 */
	public static AmplitudeArrayPlayer playDoubleArray(AudioFormat format, double[] samples) {
		return playDoubleArray(format, samples, true); // Allow interrupt
	}	
	
	/**
	 * Plays double array. Allows interruption by default, and also uses the default
	 * audio format. This is used for originally generated sounds that don't have
	 * an accessible AudioFormat.
	 * 
	 * @param samples double array representing generated audio
	 *  @return AmplitudeArrayPlayer instance that plays audio
	 */
	public static AmplitudeArrayPlayer playDoubleArray(double[] samples) {
		return playDoubleArray(DEFAULT_AUDIO_FORMAT, samples, true); // Allow interrupt
	}
	
	/**
	 * Plays double array based on whether interruption has been allowed or not. Uses
	 * default audio format. this is used for originally generated sounds that don't
	 * have an accessible AudioFormat.
	 * 
	 * @param samples double array representing generated audio
	 * @param allowInterrupt true if audio interruption is allowed, false otherwise
	 * @return AmplitudeArrayPlayer instance that plays audio
	 */
	public static AmplitudeArrayPlayer playDoubleArray(double[] samples, boolean allowInterrupt) {
		return playDoubleArray(DEFAULT_AUDIO_FORMAT, samples, allowInterrupt);
	}
	
	public static void removePops(double[] amplitude, int unitsToClip) {
		//ramping up volume at beginning
		for(int i = 0; i < unitsToClip; i++) {
			 amplitude[i] *= i/(float)unitsToClip;
		}
		//decreasing volume at end
		for(int i = amplitude.length - 1 - unitsToClip; i < amplitude.length; i++) {
			amplitude[i] *= (amplitude.length - 1 - i)/(float)unitsToClip;
		}
	}

}