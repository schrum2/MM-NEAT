package edu.utexas.cs.nn.util.sound;
//for playing midi sound files on some older systems
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
 *  This class provides a basic capability for
 *  creating, reading, and saving audio. 
 *  
 *  The audio format uses a sampling rate of 44,100 (CD quality audio), 16-bit, monaural.
 *
 *  
 *  For additional documentation, see <a href="http://introcs.cs.princeton.edu/15inout">Section 1.5</a> of
 *  <i>Computer Science: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Isabel Tweraser
 */
public final class MiscSoundUtil {

	/**
	 *  The sample rate - 44,100 Hz for CD quality audio.
	 */
	public static final int SAMPLE_RATE = 44100;

	public static final int BYTES_PER_SAMPLE = 2;                // 16-bit audio
	public static final int BITS_PER_SAMPLE = 16;                // 16-bit audio
	public static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
	private static final int SAMPLE_BUFFER_SIZE = 4096;


	private static SourceDataLine line;   // to play the sound
	private static SourceDataLine lineSave;   // to save the sound
	private static byte[] buffer;         // our internal buffer
	private static int bufferSize = 0;    // number of samples currently in internal buffer

	private static boolean playing = false;
	private static boolean available = true;

	

	private MiscSoundUtil() {
		// can not instantiate
	}

	// static initializer
	static {
		init();
	}

	// open up an audio stream
	private static void init() {
		try {
			// 44,100 samples per second, 16-bit audio, mono, signed PCM, little Endian
			AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
			
			// Extra one unnecessary?
			lineSave = (SourceDataLine) AudioSystem.getLine(info);
			lineSave.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);

			// the internal buffer is a fraction of the actual buffer size, this choice is arbitrary
			// it gets divided because we can't expect the buffered data to line up exactly with when
			// the sound card decides to push out its samples.
			buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE/3];
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
	public static void close() {
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
	public static void playDouble(double sample) {

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
	public static void playDoubleArray(double[] samples, boolean allowInterrupt) {
		if (samples == null) throw new IllegalArgumentException("argument to play() is null");
		playing = false; // Disable any previously playing sample
		if(allowInterrupt) {
			while(!available) { // Wait until previous sample finishes playing
				try {
					Thread.sleep(1); // short pause to wait for sound line to become available
				} catch (InterruptedException e) {
					e.printStackTrace(); // Should not happen?
				}
			}
		}
		// Play sound in its own Thread
		Thread temp = new Thread() {
			public void run() {
				playing = true;
				available = false;
				for (int i = 0; playing && i < samples.length; i++) {
					playDouble(samples[i]);
				}				
				available = true;
			}
		};
		
		if(allowInterrupt) temp.start(); // Launches in new Thread
		else temp.run(); // Just executes the code sequentially
	}

	/**
	 * Reads audio samples from a file (in .wav or .au format) and returns
	 * them as a double array with values between -1.0 and +1.0.
	 *
	 * @param  filename the name of the audio file
	 * @return the array of samples
	 */
	public static double[] read(String filename) {
		byte[] data = readByte(filename);
		int n = data.length;
		double[] d = new double[n/2];
		for (int i = 0; i < n/2; i++) {
			d[i] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) MAX_16_BIT);
		}
		return d;
	}

	/**
	 * Reads in data from audio file and returns it as an array of bytes. 
	 * 
	 * @param filename string reference to audio file being used
	 * @return byte array containing data from audio file 
	 */
	public static byte[] readByte(String filename) {
		byte[] data = null;
		AudioInputStream ais = null;
		try {

			// try to read from file
			File file = new File(filename);
			if (file.exists()) {
				ais = AudioSystem.getAudioInputStream(file);
				int bytesToRead = ais.available();
				data = new byte[bytesToRead];
				int bytesRead = ais.read(data);
				if (bytesToRead != bytesRead)
					throw new IllegalStateException("read only " + bytesRead + " of " + bytesToRead + " bytes"); 
			}

			// try to read from URL
			else {
				URL url = MiscSoundUtil.class.getResource(filename);
				ais = AudioSystem.getAudioInputStream(url);
				int bytesToRead = ais.available();
				data = new byte[bytesToRead];
				int bytesRead = ais.read(data);
				if (bytesToRead != bytesRead)
					throw new IllegalStateException("read only " + bytesRead + " of " + bytesToRead + " bytes"); 
			}
		}
		catch (IOException e) {
			throw new IllegalArgumentException("could not read '" + filename + "'", e);
		}

		catch (UnsupportedAudioFileException e) {
			throw new IllegalArgumentException("unsupported audio format: '" + filename + "'", e);
		}

		return data;
	}
	
	/**
	 * Plays sound using Applet.newAudioClip() - works for MIDI files
	 * 
	 * @param filename string reference to audio file being played
	 */
	public static void playApplet(String filename) {
		URL url = null;
		try {
			File file = new File(filename);
			if(file.canRead()) url = file.toURI().toURL();
		}
		catch (MalformedURLException e) {
			throw new IllegalArgumentException("could not play '" + filename + "'", e);
		}

		// URL url = StdAudio.class.getResource(filename);
		if (url == null) {
			throw new IllegalArgumentException("could not play '" + filename + "'");
		}

		AudioClip clip = Applet.newAudioClip(url);
		clip.play();
	}

}