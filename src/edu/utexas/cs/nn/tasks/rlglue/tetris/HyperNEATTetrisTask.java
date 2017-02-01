package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.LinkedList;
import java.util.List;
import org.rlcommunity.environments.tetris.TetrisState;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.ExtendedBertsekasTsitsiklisTetrisExtractor;
import edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class HyperNEATTetrisTask<T extends Network> extends TetrisTask<T> implements HyperNEATTask {

	// These values will be defined before they are needed
	public static final int HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION = 1;
	private static final int SUBSTRATE_COORDINATES = 4;
	public final int numProcessLayers = Parameters.parameters.integerParameter("HNTTetrisProcessDepth");
	private static List<Substrate> substrateInformation = null;
	private List<Pair<String, String>> substrateConnectivity = null;

	@Override
	public List<Substrate> getSubstrateInformation() {
		int outputDepth = SUBSTRATE_COORDINATES;
		if (substrateInformation == null) {
			substrateInformation = new LinkedList<Substrate>();
			int worldWidth = TetrisState.worldWidth;
			int worldHeight = TetrisState.worldHeight;
			boolean split = CommonConstants.splitRawTetrisInputs;
		
			// Different extractors correspond to different substrate configurations
			if(MMNEAT.rlGlueExtractor instanceof RawTetrisStateExtractor) {			
				Triple<Integer, Integer, Integer> blockSubCoord = new Triple<Integer, Integer, Integer>(0, 0, 0);
				Pair<Integer, Integer> substrateDimension = new Pair<Integer, Integer>(worldWidth, worldHeight);
				Substrate blockInputSub = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, blockSubCoord, "input_0");	
				substrateInformation.add(blockInputSub);
				if(split) {
					Triple<Integer, Integer, Integer> holesSubCoord = new Triple<Integer, Integer, Integer>(SUBSTRATE_COORDINATES, 0, 0);
					Substrate holesInputSub = new Substrate(substrateDimension, Substrate.INPUT_SUBSTRATE, holesSubCoord, "input_1");
					substrateInformation.add(holesInputSub);
				}
				if(!CommonConstants.hyperNEAT){
					Substrate biasSub = new Substrate(new Pair<Integer, Integer>(0,0), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(SUBSTRATE_COORDINATES+1, 0,0), "bias_1");
					substrateInformation.add(biasSub);
				}
				for(int i = 0; i < numProcessLayers; i++) {
					Triple<Integer, Integer, Integer> processSubCoord = new Triple<Integer, Integer, Integer>(split ? SUBSTRATE_COORDINATES/2 : 0, outputDepth += SUBSTRATE_COORDINATES, 0);
					Substrate processSub = new Substrate(substrateDimension, Substrate.PROCCESS_SUBSTRATE, processSubCoord,"process_" + i);
					substrateInformation.add(processSub);
				}
			} else if(MMNEAT.rlGlueExtractor instanceof ExtendedBertsekasTsitsiklisTetrisExtractor) {			
				Substrate heights = new Substrate(new Pair<Integer,Integer>(worldWidth,1), Substrate.INPUT_SUBSTRATE, new Triple<Integer,Integer,Integer>(0,0,0), "heights");	
				substrateInformation.add(heights);
				Substrate heightDiffs = new Substrate(new Pair<Integer,Integer>(worldWidth - 1,1), Substrate.INPUT_SUBSTRATE, new Triple<Integer,Integer,Integer>(1,0,0), "differences");	
				substrateInformation.add(heightDiffs);
				Substrate height = new Substrate(new Pair<Integer,Integer>(1,1), Substrate.INPUT_SUBSTRATE, new Triple<Integer,Integer,Integer>(2,0,0), "max_height");	
				substrateInformation.add(height);
				Substrate holes = new Substrate(new Pair<Integer,Integer>(1,1), Substrate.INPUT_SUBSTRATE, new Triple<Integer,Integer,Integer>(3,0,0), "total_holes");	
				substrateInformation.add(holes);
				if(!CommonConstants.hyperNEAT){ // This is only needed in HyperNEAT-Seeded NEAT networks, and maybe for HybrID in the future
					Substrate bias = new Substrate(new Pair<Integer,Integer>(1,1), Substrate.INPUT_SUBSTRATE, new Triple<Integer,Integer,Integer>(4,0,0), "bias");	
					substrateInformation.add(bias);//TODO put in if case if hnt false
				}
				if(split) {
					Substrate columnHoles = new Substrate(new Pair<Integer,Integer>(worldWidth,1), Substrate.INPUT_SUBSTRATE, new Triple<Integer,Integer,Integer>(5,0,0), "holes");	
					substrateInformation.add(columnHoles);
				}
				for(int i = 0; i < numProcessLayers; i++) {
					Triple<Integer, Integer, Integer> processSubCoord = new Triple<Integer, Integer, Integer>(0, outputDepth += SUBSTRATE_COORDINATES, 0);
					Substrate processSub = new Substrate(new Pair<Integer,Integer>(MMNEAT.rlGlueExtractor.numFeatures(),1), Substrate.PROCCESS_SUBSTRATE, processSubCoord, "process_" + i);
					substrateInformation.add(processSub);
				}
			} else {
				System.out.println("No substrate configuration defined for this extractor!");
				System.exit(1);
			}
			// Regardless of inputs, there is only one output
			Triple<Integer, Integer, Integer> outSubCoord = new Triple<Integer, Integer, Integer>(split ? SUBSTRATE_COORDINATES/2 : 0, outputDepth, 0);
			Pair<Integer, Integer> outputSubstrateDimension = new Pair<Integer, Integer>(HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION, HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION);
			Substrate outputSub = new Substrate(outputSubstrateDimension, Substrate.OUTPUT_SUBSTRATE, outSubCoord,"output_0");
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
			// Different extractors correspond to different substrate configurations
			if(MMNEAT.rlGlueExtractor instanceof RawTetrisStateExtractor) {			
				substrateConnectivity.add(new Pair<String, String>("input_0", "process_0"));
				if(CommonConstants.splitRawTetrisInputs) {
					substrateConnectivity.add(new Pair<String, String>("input_1", "process_0"));
				}
				if(Parameters.parameters.booleanParameter("extraHNLinks")) {
					substrateConnectivity.add(new Pair<String, String>("input_0", "output_0"));
					if(CommonConstants.splitRawTetrisInputs) {
						substrateConnectivity.add(new Pair<String, String>("input_1", "output_0"));
					}
				}
			} else if(MMNEAT.rlGlueExtractor instanceof ExtendedBertsekasTsitsiklisTetrisExtractor) {			
				substrateConnectivity.add(new Pair<String, String>("heights", "process_0"));
				substrateConnectivity.add(new Pair<String, String>("differences", "process_0"));
				substrateConnectivity.add(new Pair<String, String>("max_height", "process_0"));
				substrateConnectivity.add(new Pair<String, String>("total_holes", "process_0"));
				if(!CommonConstants.hyperNEAT){
					substrateConnectivity.add(new Pair<String, String>("bias", "process_0"));
				}
				if(CommonConstants.splitRawTetrisInputs) {
					substrateConnectivity.add(new Pair<String, String>("holes", "process_0"));
				}
				if(Parameters.parameters.booleanParameter("extraHNLinks")) {
					substrateConnectivity.add(new Pair<String, String>("heights", "output_0"));
					substrateConnectivity.add(new Pair<String, String>("differences", "output_0"));
					substrateConnectivity.add(new Pair<String, String>("max_height", "output_0"));
					substrateConnectivity.add(new Pair<String, String>("total_holes", "output_0"));
					if(!CommonConstants.hyperNEAT){
						substrateConnectivity.add(new Pair<String, String>("bias", "output_0"));
					}
					if(CommonConstants.splitRawTetrisInputs) {
						substrateConnectivity.add(new Pair<String, String>("holes", "output_0"));
					}
				}
			} else {
				System.out.println("No substrate configuration defined for this extractor!");
				System.exit(1);
			}
			// hidden layer connectivity is the same
			for(int i = 0; i < (numProcessLayers - 1); i++) {
				substrateConnectivity.add(new Pair<String, String>("process_" + i, "process_" + (i + 1)));
			}
			substrateConnectivity.add(new Pair<String, String>("process_" + (numProcessLayers - 1), "output_0"));
		}
		return substrateConnectivity;
	}
}
