package edu.utexas.cs.nn.util.sound;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 * Methods associated with extracting, saving and manipulating amplitude arrays from audio files. 
 * This class is where CPPNs are employed to generate new sounds/timbres 
 * 
 * @author Isabel Tweraser
 *
 */
public class SoundAmplitudeArrayManipulator {

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

	/**
	 * Takes in an array of bytes and saves it as a file. This method
	 * works for WAV and MIDI files, but not mp3 files. (Also, saved
	 * WAV files will play, but saved MIDI files won't).  TODO
	 * 
	 * @param amplitudeArray array of bytes
	 * @param fileDest location where file should be saved
	 * @throws IOException if an I/O operation has failed or been interrupted
	 */
	public static void saveFileFromArray(byte[] amplitudeArray, String fileDest) throws IOException {
			Path path = Paths.get(fileDest);
			Files.write(path, amplitudeArray);
	}

	//CPPN 

	/**
	 * Creates a double array of amplitudes using a CPPN. CPPN has three inputs - time, frequency,
	 * and a bias. It only has one output, which is the amplitude. The array of inputs is looped through 
	 * so that each index is manipulated by the CPPN, and the output indexes are saved into a result array 
	 * of doubles. 
	 * 
	 * @param CPPN network used to generate amplitude 
	 * @param length length of sample
	 * @param frequency Frequency of note being manipulated
	 * @return array of doubles representing all CPPN-manipulated output amplitudes
	 */
	public static double[] amplitudeGenerator(Network CPPN, int length, int frequency) {
		double[] result = new double[length];
		for(double time = 0; time < length; time++) {
			//double[] inputs = new double[]{time/StdAudio.SAMPLE_RATE, Math.sin(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), HyperNEATCPPNGenotype.BIAS};
			//double[] inputs = new double[]{time/StdAudio.SAMPLE_RATE, ActivationFunctions.triangleWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), HyperNEATCPPNGenotype.BIAS};
			double[] inputs = new double[]{time/MiscSoundUtil.SAMPLE_RATE, 
					Math.sin(2*Math.PI * frequency * time/MiscSoundUtil.SAMPLE_RATE), 
					//					ActivationFunctions.triangleWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), 
					//					ActivationFunctions.squareWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), 
					HyperNEATCPPNGenotype.BIAS};
			double[] outputs = CPPN.process(inputs);
			result[(int) time] = outputs[0]; // amplitude
		}
		return result;
	}

	/**
	 * Uses a CPPN to generate an output and saves that output into a file. 
	 * 
	 * @param CPPN network used to generate amplitude 
	 * @param length length of sample
	 * @param frequency Frequency of note being manipulated
	 * @param fileDest String representation of location where generated file will be saved
	 * @throws IOException if an I/O operation has failed or been interrupted
	 */
	public static void saveFileFromCPPN(Network CPPN, int length, int frequency, String fileDest) throws IOException {
		double[] generatedSound = amplitudeGenerator(CPPN, length, frequency);
		saveFileFromArray(ArrayUtil.doublesToBytes(generatedSound), fileDest);
	}

}
