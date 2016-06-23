package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.LinkedList;
import java.util.List;
import org.rlcommunity.environments.tetris.TetrisState;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class HyperNEATTetrisTask<T extends Network> extends TetrisTask<T> implements HyperNEATTask {

	// These values will be defined before they are needed
	public static final int HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION = 1;
	private static final int SUBSTRATE_COORDINATES = 4;

	private static List<Substrate> substrateInformation = null;
	private List<Pair<String, String>> substrateConnectivity = null;

	@Override
	public List<Substrate> getSubstrateInformation() {
		if (substrateInformation == null) {
			substrateInformation = new LinkedList<Substrate>();
			int worldWidth = TetrisState.worldWidth;
			int worldHeight = TetrisState.worldHeight;
			boolean split = CommonConstants.splitHyperNEATTetrisInputs;
			Triple<Integer, Integer, Integer> blockSubCoord = new Triple<Integer, Integer, Integer>(0, 0, 0);
			Triple<Integer, Integer, Integer> processSubCoord = new Triple<Integer, Integer, Integer>(split ? SUBSTRATE_COORDINATES/2 : 0,SUBSTRATE_COORDINATES, 0);
			Triple<Integer, Integer, Integer> outSubCoord = new Triple<Integer, Integer, Integer>(split ? SUBSTRATE_COORDINATES/2 : 0,SUBSTRATE_COORDINATES * 2, 0);

			Pair<Integer, Integer> substrateDimension = new Pair<Integer, Integer>(worldWidth, worldHeight);
			Pair<Integer, Integer> outputSubstrateDimension = new Pair<Integer, Integer>(HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION, HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION);
			Substrate blockInputSub = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, blockSubCoord, "input_0");
			Substrate processSub = new Substrate(substrateDimension, Substrate.PROCCESS_SUBSTRATE, processSubCoord,"process_0");
			Substrate outputSub = new Substrate(outputSubstrateDimension, Substrate.OUTPUT_SUBSTRATE, outSubCoord,"output_0");
			substrateInformation.add(blockInputSub);
			if(split) {
				Triple<Integer, Integer, Integer> holesSubCoord = new Triple<Integer, Integer, Integer>(SUBSTRATE_COORDINATES, 0, 0);
				Substrate holesInputSub = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, holesSubCoord, "input_1");
				substrateInformation.add(holesInputSub);
			}
			substrateInformation.add(processSub);
			substrateInformation.add(outputSub);
		}

		return substrateInformation;
	}

	/**
	 * Connects substrates fully, in basic configuration
	 */
	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		if (substrateConnectivity == null) {
			substrateConnectivity = new LinkedList<Pair<String, String>>();
			substrateConnectivity.add(new Pair<String, String>("input_0", "process_0"));
			if(CommonConstants.splitHyperNEATTetrisInputs) {
				substrateConnectivity.add(new Pair<String, String>("input_1", "process_0"));
			}
			substrateConnectivity.add(new Pair<String, String>("process_0", "output_0"));
			if(Parameters.parameters.booleanParameter("extraHNTetrisLinks")) {
				substrateConnectivity.add(new Pair<String, String>("input_0", "output_0"));
				if(CommonConstants.splitHyperNEATTetrisInputs) {
					substrateConnectivity.add(new Pair<String, String>("input_1", "output_0"));
				}
			}
		}
		return substrateConnectivity;
	}
}
