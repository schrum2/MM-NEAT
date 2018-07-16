package edu.southwestern.networks.hyperneat.architecture;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;

/**
 * Defines convolutional architecture such that the number of feature maps increases as the size decreases
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
		List<Triple<Integer, Integer, Integer>> tetrisConvolutionalArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
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

	private List<SubstrateConnectivity> getSubstrateConnectivity (List<String> inputSubstrateNames, List<String> outputSubstrateNames) {
		return FlexibleSubstrateArchitecture.getDefaultConnectivity(inputSubstrateNames, outputSubstrateNames, getNetworkHiddenArchitecture());
	}
	
	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity(HyperNEATTask task) {
		Pair<List<String>, List<String>> io = FlexibleSubstrateArchitecture.getInputAndOutputNames(task);
		return getSubstrateConnectivity(io.t1, io.t2);
	}
}