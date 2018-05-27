package edu.southwestern.tasks.rlglue.tetris.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import edu.southwestern.networks.hyperneat.SubstrateArchitectureDefinition;
import edu.southwestern.util.datastructures.Triple;

/**
 * Defines convolutional architecture such that the nubmer of feature maps increases as the size decreases
 * 
 * optimization possibility: pass only width of substrate to hidden methods not entire triple
 * organization possibility: put helper methods in own class for reuse
 * @author Jacob Schrum
 * @author Devon Fulcher
 */

public class TetrisConvolutionalArchitecture implements SubstrateArchitectureDefinition {

	/**
	 * specifies a convolutional architecture: Number of feature maps increases as size decreases
	 * @return List of triples that specifies each substrate with the index of each triple being its layer.
	 * 		Each triple looks like (width of layer, width of substrate, height of substrate)
	 */
	@Override
	public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture() {
		List<Triple<Integer, Integer, Integer>> tetrisConvolutionalArchitecture = new LinkedList<Triple<Integer, Integer, Integer>>();
		//Input substrates excluded because they are provided by the HyperNEATTask
		//tetrisConvolutionalArchitecture.add(new Triple<>(2,10,20)); //input

		tetrisConvolutionalArchitecture.add(new Triple<>(3,8,18));
		tetrisConvolutionalArchitecture.add(new Triple<>(4,6,16));
		tetrisConvolutionalArchitecture.add(new Triple<>(6,4,14));
		tetrisConvolutionalArchitecture.add(new Triple<>(100,1,1));

		//Output substrates excluded because they are provided by the HyperNEATTask
		//tetrisConvolutionalArchitecture.add(new Triple<>(1,1,1)); //output
		return tetrisConvolutionalArchitecture;
	}

	/**
	 * @param inputSubstrateNames List of input substrate names
	 * @param outputSubstrateNames List of output substrate names
	 * @return List of all substrate connections as given by a Triple where the first String is the unique identifier
	 * 	of a particular input/process substrate, the second String is the unique identifier of a particular output/process
	 *  substrate. The boolean is true for each layer except the last two, which are fully connected(boolean is true if substrates
	 *  are capable of being convolutional).
	 *  
	 *  POSSIBLE ERROR: if the hidden layer specification above does not align with the constant final specifications below,
	 *  	unpredicted behavior can arise.
	 *  TODO: Fix this possible error
	 */
	@Override
	public List<Triple<String, String, Boolean>> getSubstrateConnectivity(List<String> inputSubstrateNames, List<String> outputSubstrateNames) {
		List<Triple<String, String, Boolean>> networkConnectivity = new ArrayList<Triple<String, String, Boolean>>();
		
		//define network architecture here
		final int NUM_ADJACENT_CONV_HIDDEN_LAYER_PAIRS = 2;
		final int NUM_ADJACENT_FULL_HIDDEN_LAYER_PAIRS = 1;
		if (NUM_ADJACENT_FULL_HIDDEN_LAYER_PAIRS + NUM_ADJACENT_CONV_HIDDEN_LAYER_PAIRS + 1 != getNetworkHiddenArchitecture().size()) {
			throw new IllegalStateException("if the hidden layer specification above does not align with the constant final specifications below unpredicted behavior can arise.");
		}

		//iterator to access each layer
		Iterator<Triple<Integer, Integer, Integer>> It_networkHiddenArchitecture = getNetworkHiddenArchitecture().iterator();
		Triple<Integer, Integer, Integer> currentLayerHyperParameters = It_networkHiddenArchitecture.next();
		Triple<Integer, Integer, Integer> nextLayerHyperParameters;

		//connects input layer to first hidden/process layer, convolutional because boolean = true
		addFirstLayerConnectivity(networkConnectivity, inputSubstrateNames, currentLayerHyperParameters, true);

		//connects adjacent convolutional hidden layers
		for(int i = 0; i < NUM_ADJACENT_CONV_HIDDEN_LAYER_PAIRS; i++) {
			nextLayerHyperParameters = It_networkHiddenArchitecture.next();
			addHiddenLayerConnectivity(networkConnectivity, currentLayerHyperParameters, nextLayerHyperParameters, true);
			currentLayerHyperParameters = nextLayerHyperParameters;
		}

		//connects adjacent fully connected hidden layers
		for(int i = 0; i < NUM_ADJACENT_FULL_HIDDEN_LAYER_PAIRS; i++) {	
			nextLayerHyperParameters = It_networkHiddenArchitecture.next();
			addHiddenLayerConnectivity(networkConnectivity, currentLayerHyperParameters, nextLayerHyperParameters, false);
			currentLayerHyperParameters = nextLayerHyperParameters;
		}

		//connects last hidden/process layer to output layer, fully connected because boolean = false
		addLastLayerConnectivity(networkConnectivity, currentLayerHyperParameters, outputSubstrateNames, false);

		return networkConnectivity;
	}

	/**
	 * connects input layer to first hidden/process layer
	 * @param networkConnectivity list that connectivity is appended to
	 * @param inputSubstrateNames list of each input substrate name
	 * @param firstHiddenLayerHyperParameters list of triples for each substrate in the first hidden layer.
	 * 		Looks like: (width of layer, width of substrate, height of substrate)
	 * @param capableOfConvolution if true and if parameter convolution = true in batch file
	 * 		then this layer will be convolutional else it will be fully connected
	 */
	private void addFirstLayerConnectivity(List<Triple<String, String, Boolean>> networkConnectivity, 
			List<String> inputSubstrateNames, 
			Triple<Integer, Integer, Integer> firstHiddenLayerHyperParameters, boolean capableOfConvolution) {

		Iterator<String> It_inputSubstrateNames = inputSubstrateNames.iterator();
		for(int i = 0; i < inputSubstrateNames.size(); i++) {
			String currentinputSubstrateName = It_inputSubstrateNames.next();

			//firstHiddenLayer.t1 = width of first hidden layer
			for(int j = 0; j < firstHiddenLayerHyperParameters.t1; j++) {
				networkConnectivity.add(new Triple<String, String, Boolean>
				(currentinputSubstrateName, "process(" + j + "," + i + ")" , capableOfConvolution));
			}
		}
	}

	/**
	 * connects two adjacent hidden substrate layers
	 * @param networkConnectivity list that connectivity is appended to
	 * @param firstLayerHyperParameters list of triples for each substrate in this hidden layer.
	 * 		Looks like: (width of layer, width of substrate, height of substrate)
	 * @param secondLayerHyperParameters list of triples for each substrate in this hidden layer.
	 * 		Looks like: (width of layer, width of substrate, height of substrate)
	 * @param capableOfConvolution if true and if parameter convolution = true in batch file
	 * 		then this layer will be convolutional else it will be fully connected
	 */
	private void addHiddenLayerConnectivity(
			List<Triple<String, String, Boolean>> networkConnectivity, 
			Triple<Integer, Integer, Integer> firstLayerHyperParameters, 
			Triple<Integer, Integer, Integer> secondLayerHyperParameters, boolean capableOfConvolution) {

		//firstLayerHyperParameters.t1 = width of layer
		for(int i = 0; i < firstLayerHyperParameters.t1; i++) {

			//secondLayerHyperParameters.t1 = width of layer
			for(int j = 0; j < secondLayerHyperParameters.t1; j++) {
				networkConnectivity.add(new Triple<String, String, Boolean>
				("process(" + i + "," + networkConnectivity.size() + ")", "process(" + j + "," + i + ")" , capableOfConvolution));
			}
		}
	}


	/**
	 * TODO: addPoolingHiddenLayerConnectivity
	 */


	/**
	 * connects last hidden layer to output layer
	 * @param networkConnectivity list that connectivity is appended to
	 * @param lastHiddenLayerHyperParameters list of triples for each substrate in the last hidden layer.
	 * 		Looks like: (width of layer, width of substrate, height of substrate)
	 * @param outputSubstrateNames list of each output substrate name
	 * @param capableOfConvolution if true and if parameter convolution = true in batch file
	 * 		then this layer will be convolutional else it will be fully connected
	 */
	private void addLastLayerConnectivity(List<Triple<String, String, Boolean>> networkConnectivity, 
			Triple<Integer, Integer, Integer> lastHiddenLayerHyperParameters, 
			List<String> outputSubstrateNames, boolean capableOfConvolution) {
		
		Iterator<String> It_outputSubstrateNames = outputSubstrateNames.iterator();

		//lastHiddenLayerHyperParameters.t1 = the width of the last hidden layer
		for(int i = 0; i < lastHiddenLayerHyperParameters.t1; i++) {
			String currentOutputSubstrateName = It_outputSubstrateNames.next();
			for(int j = 0; j < outputSubstrateNames.size(); j++) {
				networkConnectivity.add(new Triple<String, String, Boolean>
				("process(" + j + "," + i + ")", currentOutputSubstrateName, false));
			}
		}
	}
}
