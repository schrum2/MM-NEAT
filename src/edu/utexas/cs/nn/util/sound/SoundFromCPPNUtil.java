package edu.utexas.cs.nn.util.sound;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.networks.Network;

/**
 * Utility methods associated with generating and saving a sound using an input
 * CPPN. These methods are particularly useful for the breedesizer so that 
 * audio playback can occur and be saved on the interface. 
 * 
 * @author Isabel Tweraser
 *
 */
public class SoundFromCPPNUtil {
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
		return amplitudeGenerator(CPPN, length, frequency, new double[]{1.0,1.0,1.0});
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
			double[] inputs = new double[]{time/PlayDoubleArray.SAMPLE_RATE, Math.sin(2*Math.PI * frequency * time/PlayDoubleArray.SAMPLE_RATE), HyperNEATCPPNGenotype.BIAS};	
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
	 * Takes in a WAV file represented as a double array and uses an input generated CPPN to "remix" the sound
	 * or manipulate it according to the specifications of the network. Loops through the inputs and manipulates 
	 * them according to the length, frequency, and whether the input multipliers have been turned on or not.
	 * Outputs a double array that represents the audio of the newly remixed audio. 
	 * 
	 * @param CPPN network used to generate amplitude
	 * @param inputWAV double array representing WAV file being remixed
	 * @param length length of sample
	 * @param frequency Frequency of note being manipulated
	 * @param sampleRate specified rate of input wav file 
	 * @param inputMultipliers double array determining whether checkboxes have been turned on or off in Breedesizer
	 * @return
	 */
	public static double[] amplitudeRemixer(Network CPPN, double[] inputWAV, int length, double frequency, int sampleRate, double[] inputMultipliers) {
		double[] result = new double[length];
		for(double time = 0; time < length; time++) {
			double[] inputs = new double[]{time/sampleRate, Math.sin(2*Math.PI * frequency * time/sampleRate), inputWAV[(int) time], HyperNEATCPPNGenotype.BIAS};	
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
	 * Uses a CPPN to generate an output and saves that output into a file.
	 * 
	 * @param CPPN network used to generate amplitude 
	 * @param length length of sample
	 * @param frequency Frequency of note being manipulated
	 * @param fileDest String representation of location where generated file will be saved
	 * @throws IOException if an I/O operation has failed or been interrupted
	 */
	public static void saveFileFromCPPN(Network CPPN, int length, double frequency, String fileName) throws IOException {
		double[] generatedSound = amplitudeGenerator(CPPN, length, frequency);
		SaveFromArray.saveFileFromDoubleArray(fileName, generatedSound);
	}
	
	/**
	 * Uses a double array of amplitudes and a CPPN to generate a remixed sound and saves that 
	 * output into a file.
	 * 
	 *@param CPPN network used to generate amplitude
	 * @param inputWAV double array representing WAV file being remixed
	 * @param length length of sample
	 * @param frequency Frequency of note being manipulated
	 * @param sampleRate specified rate of input wav file 
	 * @param inputMultipliers double array determining whether checkboxes have been turned on or off in Breedesizer
	 * @param fileName String representation of location where generated file will be saved
	 */
	public static void saveRemixedFileFromCPPN(Network cppn, double[] inputWAV, int length, double frequency, int sampleRate, double[] inputMultipliers, String fileName) {
		double[] generatedSound = amplitudeRemixer(cppn, inputWAV, length, frequency, sampleRate, inputMultipliers);
		SaveFromArray.saveFileFromDoubleArray(fileName, generatedSound);
	}

}
