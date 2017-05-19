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

import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.Network;

/**
 * Methods associated with extracting, saving and manipulating amplitude arrays from audio files. 
 * This class is where CPPNs are employed to generate new sounds/timbres 
 * 
 * @author Isabel Tweraser
 *
 */
public class SoundAmplitudeArrayManipulator {
	
	public static void notes() {
		final double c3 = 130.81;
		final double cSharp3 = 138.59;
		final double d3 = 146.83;
		final double dSharp3 = 155.56;
		final double e3 = 164.81;
		final double f3 = 174.61;
		final double fSharp3 = 185.00;
		final double g3 = 196.00;
		final double gSharp3 = 207.65;
		final double a3 = 220.00;
		final double aSharp3 = 233.08;
		final double b3 = 246.94;
		final double c4 = 261.63;
		final double cSharp4 = 277.18;
		final double d4 = 293.66;
		final double dSharp4 = 311.13;
		final double e4 = 329.63;
		final double f4 = 349.23;
		final double fSharp4 = 369.99;
		final double g4 = 392.00;
		final double gSharp4 = 415.30;
		final double a4 = 440.00;
		final double aSharp4 = 466.16;
		final double b4 = 493.88;
		final double c5 = 523.25;
		
		double[] keyboard = new double[]{c3, cSharp3, d3, dSharp3, e3, f3, fSharp3, g3, gSharp3, a3, aSharp3, b3, c4, cSharp4, d4, dSharp4, e4, f4, fSharp4, g4, gSharp4, a4, aSharp4, b4, c5};
		
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

	/**
	 * Writes an array of double values to a WAV file.  This method only writes
	 * single channel data.
	 *
	 * @param doubleArray the array of double values to save
	 * @param fileName the desired name of the file to save
	 * @return boolean indicating success or failure of the write
	 * @throws IOException 
	 */
	public static boolean writeSingleChannel(AudioFormat format, double[] doubleArray, String fileName) throws IOException {
		/* convert the double array to a byte array */
		byte[] data = new byte[2 * doubleArray.length];
		for (int i = 0; i < doubleArray.length; i++) {
			int temp = (short) (doubleArray[i] * Short.MAX_VALUE);                 
			data[2*i + 0] = (byte) temp;
			data[2*i + 1] = (byte) (temp >> 8);
		}

		/* try saving the file */
		try {
			//AudioInputStream ais = WAVUtil.byteToAIS(data);
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			AudioInputStream ais = new AudioInputStream(bais, format, doubleArray.length);
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(fileName));
		}
		catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
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
	public static double[] amplitudeGenerator(Network CPPN, int length, double frequency) {
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
	 * Creates a double array of amplitudes using a CPPN. CPPN has three inputs - time, frequency,
	 * and a bias. It only has one output, which is the amplitude. The array of inputs is looped through 
	 * so that each index is manipulated by the CPPN, and the output indexes are saved into a result array 
	 * of doubles. This method does basically the same thing as the one above, but the inputs are modified
	 * for Breedesizer based on whether the checkboxes have been clicked or not. This allows the audio to be
	 * changed by the checkboxes as well as the images.
	 * 
	 * @param CPPN network used to generate amplitude 
	 * @param length length of sample
	 * @param frequency Frequency of note being manipulated
	 * @param inputMultipliers double array determining whether checkboxes have been turned on or off in Breedesizer
	 * @return array of doubles representing all CPPN-manipulated output amplitudes
	 */
	public static double[] amplitudeGenerator(Network CPPN, int length, double frequency, double[] inputMultipliers) {
		double[] result = new double[length];
		for(double time = 0; time < length; time++) {
			//double[] inputs = new double[]{time/StdAudio.SAMPLE_RATE, Math.sin(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), HyperNEATCPPNGenotype.BIAS};
			//double[] inputs = new double[]{time/StdAudio.SAMPLE_RATE, ActivationFunctions.triangleWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), HyperNEATCPPNGenotype.BIAS};
			double[] inputs = new double[]{time/MiscSoundUtil.SAMPLE_RATE, 
					Math.sin(2*Math.PI * frequency * time/MiscSoundUtil.SAMPLE_RATE), 
					//					ActivationFunctions.triangleWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), 
					//					ActivationFunctions.squareWave(2*Math.PI * frequency * time/StdAudio.SAMPLE_RATE), 
					HyperNEATCPPNGenotype.BIAS};	
			// Multiplies the inputs of the pictures by the inputMultiples; used to turn on or off the effects in each picture
			for(int i = 0; i < inputs.length; i++) {
				inputs[i] = inputs[i] * inputMultipliers[i];
			}			
			double[] outputs = CPPN.process(inputs);
			result[(int) time] = outputs[0]; // amplitude
		}
		return result;
	}

	/**
	 * Uses a CPPN to generate an output and saves that output into a file. DOESN'T WORK
	 * 
	 * @param CPPN network used to generate amplitude 
	 * @param length length of sample
	 * @param frequency Frequency of note being manipulated
	 * @param fileDest String representation of location where generated file will be saved
	 * @throws IOException if an I/O operation has failed or been interrupted
	 */
	public static void saveFileFromCPPN(Network CPPN, int length, double frequency, String fileName, AudioFormat format) throws IOException {
		double[] generatedSound = amplitudeGenerator(CPPN, length, frequency);
		writeSingleChannel(format, generatedSound, fileName);
	}

}
