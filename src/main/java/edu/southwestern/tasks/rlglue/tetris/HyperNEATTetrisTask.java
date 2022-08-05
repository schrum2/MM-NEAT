package edu.southwestern.tasks.rlglue.tetris;

import java.util.LinkedList;
import java.util.List;

import org.rlcommunity.environments.tetris.TetrisState;

import edu.southwestern.networks.Network;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.RLGlueTask;
import edu.southwestern.tasks.rlglue.featureextractors.tetris.ExtendedBertsekasTsitsiklisTetrisExtractor;
import edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class HyperNEATTetrisTask<T extends Network> extends TetrisTask<T> implements HyperNEATTask {

	// These values will be defined before they are needed
	public static final int HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION = 1; // Tetris output is on single 1 by 1 substrate
	private static final int SUBSTRATE_COORDINATES = 4; // Schrum: What is this?
	public final int numProcessLayers = Parameters.parameters.integerParameter("HNProcessDepth");
	public final int processingWidth = Parameters.parameters.integerParameter("HNProcessWidth");
	private static List<Substrate> substrateInformation = null;
	private static List<SubstrateConnectivity> substrateConnectivity = null; 
	// Value should be defined when class is constructed by a ClassCreation call, after the rlGlueExtractor is specified.
	private final boolean TWO_DIMENSIONAL_SUBSTRATES = RLGlueTask.rlGlueExtractor instanceof RawTetrisStateExtractor;
	// Even if the substrates are 2D, the CPPN inputs may need to be overridden to be 1D with certain substrate mappings
	public static boolean reduce2DTo1D = false; // This can affect CPPNs via the numCPPNInputs and filterCPPNInputs methods.

	// Number of inputs to CPPN if substrates are 1D
	public static final int NUM_CPPN_INPUTS_1D = 3;

	public static void hardSubstrateReset() {
		substrateInformation = null;
		substrateConnectivity = null;
	}
	
	/**
	 * Default number of CPPN substrates when 2D substrates are used, but fewer
	 * when 1D substrates are used, or a 1D substrate mapping on 2D substrates.
	 */
	@Override
	public int numCPPNInputs() {
		return TWO_DIMENSIONAL_SUBSTRATES && !reduce2DTo1D ? HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS : NUM_CPPN_INPUTS_1D;
	}

	/**
	 * When 2D substrates are used, the full inputs are returned unfiltered.
	 * When 1D substrates are used, only X1, X2, and BIAS inputs are returned.
	 * When Bottom1DSubstrateMapping is used with 2D substrates, only Y1, Y2, and BIAS inputs are returned. 
	 */
	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return TWO_DIMENSIONAL_SUBSTRATES ? 
				(!reduce2DTo1D ? // Use full 2D substrate inputs 
						fullInputs : // 2D substrates and 2D CPPN inputs
							// Assume Bottom1DSubstrateMapping being used, which has a constant X but Y varies
							new double[]{fullInputs[HyperNEATTask.INDEX_Y1], fullInputs[HyperNEATTask.INDEX_Y2], fullInputs[HyperNEATTask.INDEX_BIAS]}): 
								new double[]{fullInputs[HyperNEATTask.INDEX_X1], fullInputs[HyperNEATTask.INDEX_X2], fullInputs[HyperNEATTask.INDEX_BIAS]}; // else 1D
	}	

	/**
	 * TODO
	 */
	@Override
	public List<Substrate> getSubstrateInformation() {
		int outputDepth = SUBSTRATE_COORDINATES;
		if (substrateInformation == null) {
			int worldWidth = TetrisState.worldWidth;
			int worldHeight = TetrisState.worldHeight;
			boolean split = CommonConstants.splitRawTetrisInputs;

			// Different extractors correspond to different substrate configurations
			if(RLGlueTask.rlGlueExtractor instanceof RawTetrisStateExtractor) { // 2D grid of blocks			
				List<Triple<String,Integer,Integer>> output = new LinkedList<>();
				output.add(new Triple<>("output:utility", 1, 1));
				substrateInformation = HyperNEATUtil.getSubstrateInformation(worldWidth, worldHeight, split ? 2 : 1, output);				
			} else if(RLGlueTask.rlGlueExtractor instanceof ExtendedBertsekasTsitsiklisTetrisExtractor) { // Several 1D input substrates
				substrateInformation = new LinkedList<Substrate>();
				
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
					for(int k = 0; k < processingWidth; k++) {
						// Not sure processSubCoord coordinates make sense
						Triple<Integer, Integer, Integer> processSubCoord = new Triple<Integer, Integer, Integer>(k, outputDepth += SUBSTRATE_COORDINATES, 0);
						Substrate processSub = new Substrate(new Pair<Integer,Integer>(RLGlueTask.rlGlueExtractor.numFeatures(),1), Substrate.PROCCESS_SUBSTRATE, processSubCoord, "process(" + k + "," + i + ")");
						substrateInformation.add(processSub);
					}
				}
				// Regardless of inputs, there is only one output: state utility
				Triple<Integer, Integer, Integer> outSubCoord = new Triple<Integer, Integer, Integer>(split ? SUBSTRATE_COORDINATES/2 : 0, outputDepth, 0);
				Pair<Integer, Integer> outputSubstrateDimension = new Pair<Integer, Integer>(HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION, HYPERNEAT_OUTPUT_SUBSTRATE_DIMENSION);
				Substrate outputSub = new Substrate(outputSubstrateDimension, Substrate.OUTPUT_SUBSTRATE, outSubCoord,"output:utility");
				substrateInformation.add(outputSub);
			} else {
				System.out.println("No substrate configuration defined for this extractor!");
				System.exit(1);
			}
		}		
		return substrateInformation;
	}

	/**
	 * Connects substrates fully, in basic configuration
	 */
	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity() {
		if (substrateConnectivity == null) { // Generate once and save to be reused later
			// Different extractors correspond to different substrate configurations
			if(RLGlueTask.rlGlueExtractor instanceof RawTetrisStateExtractor) {			
				List<String> outputNames = new LinkedList<>();
				outputNames.add("output:utility");
				substrateConnectivity = HyperNEATUtil.getSubstrateConnectivity(CommonConstants.splitRawTetrisInputs ? 2 : 1, outputNames);				
			} else if(RLGlueTask.rlGlueExtractor instanceof ExtendedBertsekasTsitsiklisTetrisExtractor) {	
				substrateConnectivity = new LinkedList<SubstrateConnectivity>();
				if(numProcessLayers > 0) {
					for(int k = 0; k < processingWidth; k++) {
						// Link all inputs to processing layer
						substrateConnectivity.add(new SubstrateConnectivity("heights", "process(" + k + ",0)", SubstrateConnectivity.CTYPE_FULL));
						substrateConnectivity.add(new SubstrateConnectivity("differences", "process(" + k + ",0)", SubstrateConnectivity.CTYPE_FULL));
						substrateConnectivity.add(new SubstrateConnectivity("max_height", "process(" + k + ",0)", SubstrateConnectivity.CTYPE_FULL));
						substrateConnectivity.add(new SubstrateConnectivity("total_holes", "process(" + k + ",0)", SubstrateConnectivity.CTYPE_FULL));
						if(!CommonConstants.hyperNEAT){ // Possible if using HyperNEAT seed for standard NEAT networks
							substrateConnectivity.add(new SubstrateConnectivity("bias", "process(" + k + ",0)", SubstrateConnectivity.CTYPE_FULL)); // Extra bias input is needed
						}
						if(CommonConstants.splitRawTetrisInputs) {
							substrateConnectivity.add(new SubstrateConnectivity("holes", "process(" + k + ",0)", SubstrateConnectivity.CTYPE_FULL));
						}
					}
				}
				if(Parameters.parameters.booleanParameter("extraHNLinks")) { // Connect each input substrate directly to the output neuron
					substrateConnectivity.add(new SubstrateConnectivity("heights", "output:utility", SubstrateConnectivity.CTYPE_FULL));
					substrateConnectivity.add(new SubstrateConnectivity("differences", "output:utility", SubstrateConnectivity.CTYPE_FULL));
					substrateConnectivity.add(new SubstrateConnectivity("max_height", "output:utility", SubstrateConnectivity.CTYPE_FULL));
					substrateConnectivity.add(new SubstrateConnectivity("total_holes", "output:utility", SubstrateConnectivity.CTYPE_FULL));
					if(!CommonConstants.hyperNEAT){
						substrateConnectivity.add(new SubstrateConnectivity("bias", "output:utility", SubstrateConnectivity.CTYPE_FULL));
					}
					if(CommonConstants.splitRawTetrisInputs) {
						substrateConnectivity.add(new SubstrateConnectivity("holes", "output:utility", SubstrateConnectivity.CTYPE_FULL));
					}
				}

				for(int i = 0; i < (numProcessLayers - 1); i++) {
					for(int k = 0; k < processingWidth; k++) {
						for(int q = 0; q < processingWidth; q++) {
							// Each processing substrate at one depth connected to processing subsrates at next depth
							substrateConnectivity.add(new SubstrateConnectivity("process("+k+","+i+")", "process("+q+","+(i + 1)+")", SubstrateConnectivity.CTYPE_FULL));
						}
					}
				}
				if(numProcessLayers > 0) {
					for(int k = 0; k < processingWidth; k++) {
						// Connect final (only?) processing layer(s) to the output neuron
						substrateConnectivity.add(new SubstrateConnectivity("process("+k+","+(numProcessLayers - 1)+")", "output:utility", SubstrateConnectivity.CTYPE_FULL));
					}
				}
			} else {
				System.out.println("No substrate configuration defined for this extractor!");
				System.exit(1);
			}
		}
//		System.out.println(substrateConnectivity);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		return substrateConnectivity;
	}

	@Override
	public void flushSubstrateMemory() {
		substrateInformation = null;
		substrateConnectivity = null;
	}
}
