package edu.utexas.cs.nn.tasks.boardGame;

import java.util.LinkedList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.TwoDimensionalBoardGame;
import boardGame.TwoDimensionalBoardGameViewer;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

/**
 * NOTE: Will probably be deleted
 */
public class TwoDimensionalBoardGameTask implements NetworkTask, HyperNEATTask {
	
	@SuppressWarnings("rawtypes")
	TwoDimensionalBoardGameViewer view = null;
	@SuppressWarnings("rawtypes")
	BoardGame bg;
	@SuppressWarnings("rawtypes")
	BoardGamePlayer opponent;
	
	// These only get filled in if HyperNEAT is being used
	List<Substrate> substrateInformation = null;
	List<Pair<String, String>> substrateConnectivity = null;
	
	
	
	// Used for Hyper-NEAT
		@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}
		
	// Used for Hyper-NEAT
		@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs; // default behavior
	}

	// Used for HyperNEAT
	@Override
	public List<Substrate> getSubstrateInformation() {
		if(substrateInformation == null) {
			if(bg instanceof TwoDimensionalBoardGame) {
				@SuppressWarnings("rawtypes")
				TwoDimensionalBoardGame temp = (TwoDimensionalBoardGame) bg;
				int height = temp.getStartingState().getBoardHeight();
				int width = temp.getStartingState().getBoardWidth();
				substrateInformation = new LinkedList<Substrate>();
				Substrate boardInputs = new Substrate(new Pair<Integer, Integer>(width, height), 
						Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.INPUT_SUBSTRATE, 0), "Board Inputs");
				substrateInformation.add(boardInputs);
				Substrate processing = new Substrate(new Pair<Integer, Integer>(width, height), 
						Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.PROCCESS_SUBSTRATE, 0), "Processing");
				substrateInformation.add(processing);
				Substrate output = new Substrate(new Pair<Integer, Integer>(1, 1), // Single utility value
						Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Utility Output");
				substrateInformation.add(output);
			}
			// Otherwise, no substrates will be defined, and the code will crash from the null result
		}
		return substrateInformation;
	}

	// Used for HyperNEAT
	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		if(substrateConnectivity == null) {
			substrateConnectivity = new LinkedList<Pair<String, String>>();
			substrateConnectivity.add(new Pair<String, String>("Board Inputs", "Processing"));
			substrateConnectivity.add(new Pair<String, String>("Processing", "Utility Output"));	
			if(Parameters.parameters.booleanParameter("extraHNLinks")) {
				substrateConnectivity.add(new Pair<String, String>("Board Inputs", "Utility Output"));
			}
		}
		return substrateConnectivity;
	}

	/**
	 * Returns a String containing the Sensor Labels for the BoardGameTask
	 * 
	 * @return String containing the Sensor Labels for the BoardGameTask
	 */
	@Override
	public String[] sensorLabels() {
		return bg.getFeatureLabels();
	}
	
	/**
	 * Returns a String containing the Output Labels for the BoardGameTask
	 * 
	 * @return String containing the Output Labels for the BoardGameTask
	 */
	@Override
	public String[] outputLabels() {
		return new String[]{"Utility"};
	}
	
	
}
