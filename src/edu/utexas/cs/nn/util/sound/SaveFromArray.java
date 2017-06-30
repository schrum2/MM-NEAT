package edu.utexas.cs.nn.util.sound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Includes methods that can save audio files from either an input
 * byte array or an input double array. 
 * 
 * @author Isabel Tweraser
 *
 */
public class SaveFromArray {
	/**
	 * Takes in an array of bytes and saves it as a file. This method
	 * works for WAV and MIDI files, but not mp3 files. (Also, saved
	 * WAV files will play, but saved MIDI files won't).  TODO
	 * 
	 * @param amplitudeArray array of bytes
	 * @param fileDest location where file should be saved
	 * @throws IOException if an I/O operation has failed or been interrupted
	 */
	public static void saveFileFromByteArray(byte[] amplitudeArray, String fileDest) throws IOException {
		Path path = Paths.get(fileDest);
		Files.write(path, amplitudeArray);
	}

	/**
	 * Saves the double array as an audio file (using .wav or .au format).
	 * Fully functional for saving file generated from CPPNs
	 *
	 * @param  filename the name of the audio file
	 * @param  samples the array of samples
	 * @throws IllegalArgumentException if unable to save {@code filename}
	 * @throws IllegalArgumentException if {@code samples} is {@code null}
	 */
	public static void saveFileFromDoubleArray(String filename, double[] samples) {
		AudioFormat format = new AudioFormat(PlayDoubleArray.SAMPLE_RATE, 16, 1, true, false);
		saveFileFromDoubleArray(filename, samples, format);
	}
	
	/**
	 * Saves a double array as a WAV file - compatible with multiple
	 * AudioFormats.
	 * 
	 * @param filename the name of the audio file
	 * @param samples the array of samples
	 * @param format the AudioFormat used to save the array
	 */
	public static void saveFileFromDoubleArray(String filename, double[] samples, AudioFormat format) {
		if (samples == null) {
			throw new IllegalArgumentException("samples[] is null");
		}
		byte[] data = new byte[2 * samples.length];
		for (int i = 0; i < samples.length; i++) {
			int temp = (short) (samples[i] * PlayDoubleArray.MAX_16_BIT);
			data[2*i + 0] = (byte) temp;
			data[2*i + 1] = (byte) (temp >> 8);
		}

		// now save the file
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			AudioInputStream ais = new AudioInputStream(bais, format, samples.length);
			if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
				AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
			}
			else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
				AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(filename));
			}
			else {
				throw new IllegalArgumentException("unsupported audio format: '" + filename + "'");
			}
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("unable to save file '" + filename + "'", ioe);
		}
	}
}
