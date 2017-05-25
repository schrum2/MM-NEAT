package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGamePlayerOneStepEval;
import boardGame.TwoDimensionalBoardGameViewer;
import boardGame.TwoDimensionalBoardGame;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class StaticOpponentBoardGameTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask {

	TwoDimensionalBoardGameViewer view = null;
	@SuppressWarnings("rawtypes")
	BoardGame bg;
	@SuppressWarnings("rawtypes")
	BoardGamePlayer opponent;

	// These only get filled in if HyperNEAT is being used
	List<Substrate> substrateInformation = null;
	List<Pair<String, String>> substrateConnectivity = null;

	/**
	 * Constructor for a new BoardGameTask
	 */
	@SuppressWarnings("rawtypes")
	public StaticOpponentBoardGameTask(){
		MMNEAT.registerFitnessFunction("Win Reward");

		try {
			bg = (BoardGame) ClassCreation.createObject("boardGame");
			opponent = (BoardGamePlayer) ClassCreation.createObject("boardGameOpponent"); // The opponent
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
	}

	/**
	 * Returns the number of Objectives for the BoardGameTask
	 * 
	 * @return The number of Objectives for the BoardGameTask
	 */
	@Override
	public int numObjectives() {
		return 1;
	}

	/**
	 * Returns the TimeStamp for a BoardGameTask
	 * 
	 * @return 0, because the TimeStamp doesn't appear useful for this task
	 */
	@Override
	public double getTimeStamp() {
		// Doesn't appear to be necessary for this Task, but may be used later.
		return 0;
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

	/**
	 * Returns the Behavior Vector for Behavioral Diversity
	 */
	@Override
	public ArrayList<Double> getBehaviorVector() {
		return null; // TODO: Behavioral Diversity
	}

	/**
	 * Evaluates a given individual network's Fitness;
	 * If the CommonConstants Watch variable is set to "True," runs a visual evaluation,
	 * Else runs a non-visual evaluation
	 * 
	 * @param individual Genotype<T> specifying a Network to be evaluated
	 * @param num Integer value
	 * @return Pair of Double Arrays that show the Fitness of an individual network
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {

		if(CommonConstants.watch){ // If set to Visually Evaluate the Task
		}

		BoardGamePlayer evolved = new BoardGamePlayerOneStepEval<T>(individual.getPhenotype());
		BoardGamePlayer[] players = new BoardGamePlayer[]{evolved, opponent};
		return BoardGameUtil.playGame(bg, players).get(0);
	}

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

	// Used for Hyper-NEAT
	// TODO: Move into 2D board game task!
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

	// Used for Hyper-NEAT
	// TODO: Move into 2D board game task!
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




}
