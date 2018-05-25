package edu.southwestern.tasks.rlglue.tetris.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import edu.southwestern.networks.hyperneat.SubstrateArchitectureDefinition;
import edu.southwestern.util.datastructures.Triple;

public class TetrisConvolutionalArchitecture implements SubstrateArchitectureDefinition {

	/**
	 * specifies a convolutional architecture: Number of feature maps increases as size decreases
	 */
	@Override
	public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture() {
		List<Triple<Integer, Integer, Integer>> tetrisConvolutionalArchitecture = new LinkedList<Triple<Integer, Integer, Integer>>();
		// Input substrates also provided by HyperNEATTask
		//tetrisConvolutionalArchitecture.add(new Triple<>(2,10,20)); //input

		tetrisConvolutionalArchitecture.add(new Triple<>(3,8,18));
		tetrisConvolutionalArchitecture.add(new Triple<>(4,6,16));
		tetrisConvolutionalArchitecture.add(new Triple<>(6,4,14));
		tetrisConvolutionalArchitecture.add(new Triple<>(100,1,1));

		// Output substrates excluded because they are provided by the HyperNEATTask
		//tetrisConvolutionalArchitecture.add(new Triple<>(1,1,1)); //output
		return tetrisConvolutionalArchitecture;
	}

	/**
	 * @param inputSubstrateNames Unique identifier for a given substrate that is an input for another substrate
	 * @param outputSubstrateNames Unique identifier for a given substrate that is the output for another substrate
	 * @return List of all substrate connections as given by a Triple where the first String is the unique identifier
	 * 	for a particular input/process substrate, the second String is the unique identifier for a particular ouput/process
	 *  substrate. The boolean is true for each layer except the last two, which are fully connected(boolean is true if substrates
	 *  are capable of being convolutional).
	 *  
	 *  POSSIBLE ERROR: if the hidden layer specification above does not align with the constant final specifications below,
	 *  	unpredicted behavior can arise.
	 *  TODO: Fix this possible error
	 */
	@Override
	public List<Triple<String, String, Boolean>> getNetworkConnectivity(List<String> inputSubstrateNames, List<String> outputSubstrateNames) {
		List<Triple<String, String, Boolean>> networkConnectivity = new ArrayList<Triple<String, String, Boolean>>();
		int currentLayerNumber = 0;
		//		for(int i = 0; i < inputSubstrateNames.size(); i++) {
		//			for(int j = 0; j < getNetworkHiddenArchitecture().get(0).t1; j++) {
		//				networkConnectivity.add(new Triple<String, String, Boolean>(inputSubstrateNames.get(i), "process(" + j + "," + i + ")" , true));
		//			}
		//		}


		//		for(int i = 0; i < getNetworkHiddenArchitecture().size() - 1; i++) {
		//			for(int j = 0; j < getNetworkHiddenArchitecture().get(i).t1; j++) {
		//				for(int k = 0; k < getNetworkHiddenArchitecture().get(i + 1).t1; k++) {
		//					networkConnectivity.add(new Triple<String, String, Boolean>
		//					("process(" + j + "," + i + ")", "process(" + k + "," + j + ")" , true));
		//				}
		//			}
		//		}
		
		Iterator<Triple<Integer, Integer, Integer>> It_networkHiddenArchitecture = getNetworkHiddenArchitecture().iterator();
		Triple<Integer, Integer, Integer> currentLayerHyperParameters = It_networkHiddenArchitecture.next();
		Triple<Integer, Integer, Integer> nextLayerHyperParameters;

		//connects input layer to first hidden/process layer, convolutional because boolean = true
		addFirstLayerConnectivity(networkConnectivity, inputSubstrateNames, currentLayerHyperParameters, true);

		final int NUM_ADJACENT_CONV_HIDDEN_LAYER_PAIRS = 2;
		final int NUM_ADJACENT_FULL_HIDDEN_LAYER_PAIRS = 1;
		if (NUM_ADJACENT_FULL_HIDDEN_LAYER_PAIRS + NUM_ADJACENT_CONV_HIDDEN_LAYER_PAIRS + 1 != getNetworkHiddenArchitecture().size()) {
			throw new IllegalStateException("if the hidden layer specification does not align with the constant final specifications,\r\n" + 
					"	 *  	unpredicted behavior can arise.");
		}
		
		//connecting adjacent convolutional hidden layers
		for(int i = 0; i < NUM_ADJACENT_CONV_HIDDEN_LAYER_PAIRS; i++) {
			nextLayerHyperParameters = It_networkHiddenArchitecture.next();
			addInterLayerConnectivity(networkConnectivity, currentLayerHyperParameters, nextLayerHyperParameters, true);
			currentLayerHyperParameters = nextLayerHyperParameters;
		}

		//connecting adjacent fully connected hidden layers
		for(int i = 0; i < NUM_ADJACENT_FULL_HIDDEN_LAYER_PAIRS; i++) {	
			nextLayerHyperParameters = It_networkHiddenArchitecture.next();
			addInterLayerConnectivity(networkConnectivity, currentLayerHyperParameters, nextLayerHyperParameters, false);
			currentLayerHyperParameters = nextLayerHyperParameters;
		}

		//connects last hidden/output layer to output layer, convolutional because boolean = true
		addLastLayerConnectivity(networkConnectivity, currentLayerHyperParameters, outputSubstrateNames, false);

		return networkConnectivity;
	}

	/**
	 * TODO 
	 * @param networkConnectivity
	 * @param inputLayer
	 * @param firstHiddenLayer
	 * @param capableOfConvolution
	 * @return
	 */
	private void addFirstLayerConnectivity(List<Triple<String, String, Boolean>> networkConnectivity, 
			List<String> inputSubstrateNames, 
			Triple<Integer, Integer, Integer> firstHiddenLayer, boolean capableOfConvolution) {

		//inputLayer.t1 = width of input layer
//		for(int i = 0; i < inputLayer.t1; i++) {
//
//			//firstHiddenLayer.t1 = width of first hidden layer
//			for(int j = 0; j < firstHiddenLayer.t1; j++) {
//				networkConnectivity.add(new Triple<String, String, Boolean>
//				("process(" + i + "," + networkConnectivity.size() + ")", "process(" + j + "," + i + ")" , capableOfConvolution));
//			}
//		}
	}

	/**
	 * Connects first layer to second layer. TODO
	 * @param firstLayerName
	 * @param secondLayerName
	 * @param capableOfConvolution if true and if parameter convolution = true then this layer will be convolutional else it will be fully
	 * 	connected
	 */
	private void addInterLayerConnectivity(
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
	 * TODO: getInterLayerConnectivityPooling
	 */
	
	
	/**
	 * TODO
	 * @param networkConnectivity
	 * @param inputLayer
	 * @param firstHiddenLayer
	 * @param capableOfConvolution
	 */
	private void addLastLayerConnectivity(List<Triple<String, String, Boolean>> networkConnectivity, 
			Triple<Integer, Integer, Integer> lastHiddenLayer, 
			List<String> outputSubstrateNames, boolean capableOfConvolution) {

		//connecting last layer to output layer, fully connected
		for(int i = 0; i < getNetworkHiddenArchitecture().get(getNetworkHiddenArchitecture().size()).t1; i++) {
			for(int j = 0; j < outputSubstrateNames.size(); j++) {
				networkConnectivity.add(new Triple<String, String, Boolean>
				("process(" + j + "," + i + ")", outputSubstrateNames.get(j) , false));
			}
		}
	}


}
