package edu.utexas.cs.nn.util.sound;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Contains a variety of utility methods that can extract byte 
 * and double arrays (both of which are useful for manipulating, playing, and
 * reading audio files) from a variety of inputs (file, string, AudioInputStream).
 * 
 * @author Isabel Tweraser
 *
 */
public class SoundToArray {
	/**
	 * Reads audio samples from a file (in .wav or .au format) and returns
	 * them as a double array with values between -1.0 and +1.0.
	 *
	 * @param  filename the name of the audio file
	 * @return the array of samples
	 */
	public static double[] read(String filename) {
		byte[] data= readByte(filename);
		int N= data.length;
		double[] d= new double[N/2];
		for (int i= 0; i < N/2; i++) {
			d[i]= ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / PlayDoubleArray.MAX_16_BIT;
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
		AudioInputStream ais;
		byte[] data= null;
		try {
			ais = WAVUtil.audioStream(filename);
			data= new byte[ais.available()];
			ais.read(data);
		} catch (UnsupportedAudioFileException e1) {
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return data;
	}

	//Methods from GT - used to extract amplitude from recorded wave

	/**
	 * Method that inputs an AudioInputStream, calls method that extracts amplitude byte array from
	 * the audio input stream, and returns a method call using the resulting byte array to 
	 * extractAmplitudeDataFromAmplitudeByteArray(). 
	 * 
	 * @param audioInputStream stream of audio being converted into amplitude data
	 * @return  method call that extracts amplitude data from byte array formed
	 */
	public static int[] extractAmplitudeDataFromAudioInputStream(AudioInputStream audioInputStream) {  
		AudioFormat format = audioInputStream.getFormat();  
		byte[] audioBytes = extractAmplitudeByteArrayFromAudioInputStream(audioInputStream);
		return extractAmplitudeDataFromAmplitudeByteArray(format, audioBytes);  //calls method that extracts amplitude data from byte array formed
	}  

	/**
	 * Method that inputs an AudioInputStrean, obtains the format of the stream and forms an array of bytes 
	 * based on the size of the stream to return. 
	 * 
	 * @param audioInputStream stream of audio being converted to byte array
	 * @return byte array representation of audio file
	 */
	public static byte[] extractAmplitudeByteArrayFromAudioInputStream(AudioInputStream audioInputStream) {
		AudioFormat format = audioInputStream.getFormat();  
		byte[] audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];  
		try {  
			audioInputStream.read(audioBytes);  
		} catch (IOException e) {  
			System.out.println("IOException during reading audioBytes");  
			e.printStackTrace();  
		}  
		return audioBytes;
	}

	/**
	 * Method that inputs the format of an AudioInputStream as well as the byte array formed from its contents
	 * and then creates an array of ints containing the amplitude data of the stream. 
	 * 
	 * @param format AudioFormat of AudioinputStream
	 * @param audioBytes byte array formed based on the size of the stream
	 * @return int array containing amplitude data from stream
	 */
	private static int[] extractAmplitudeDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) {  
		// convert
		int[]  audioData = null;  
		if (format.getSampleSizeInBits() == 16) {  
			int nlengthInSamples = audioBytes.length / 2;  
			audioData = new int[nlengthInSamples];  
			if (format.isBigEndian()) {  
				for (int i = 0; i < nlengthInSamples; i++) {  
					/* First byte is MSB (high order) */  
					int MSB = audioBytes[2 * i];  
					/* Second byte is LSB (low order) */  
					int LSB = audioBytes[2 * i + 1];  
					audioData[i] = (MSB << 8 | (255 & LSB));  
				}  
			} else {  
				for (int i = 0; i < nlengthInSamples; i++) {  
					/* First byte is LSB (low order) */  
					int LSB = audioBytes[2 * i];  
					/* Second byte is MSB (high order) */  
					int MSB = audioBytes[2 * i + 1];  
					audioData[i] = (MSB << 8 | (255 & LSB));  
				}  
			}  
		} else if (format.getSampleSizeInBits() == 8) {  
			int nlengthInSamples = audioBytes.length;  
			audioData = new int[nlengthInSamples];  
			if (format.getEncoding().toString().startsWith("PCM_SIGN")) {  
				// PCM_SIGNED  
				for (int i = 0; i < audioBytes.length; i++) {  
					audioData[i] = audioBytes[i];  
				}  
			} else {  
				// PCM_UNSIGNED  
				for (int i = 0; i < audioBytes.length; i++) {  
					audioData[i] = audioBytes[i] - 128;  
				}  
			}  
		}
		return audioData;  
	}
}
