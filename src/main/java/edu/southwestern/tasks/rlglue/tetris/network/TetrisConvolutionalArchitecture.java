package edu.southwestern.tasks.rlglue.tetris.network;

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

}
