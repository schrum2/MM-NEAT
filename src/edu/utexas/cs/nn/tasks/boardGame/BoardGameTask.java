package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGamePlayerOneStepEval;
import boardGame.BoardGameViewer;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class BoardGameTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask{

	BoardGameViewer view = null;
	@SuppressWarnings("rawtypes")
	BoardGame bg;
	@SuppressWarnings("rawtypes")
	BoardGamePlayer opponent;
	
	/**
	 * Constructor for a new BoardGameTask
	 */
	@SuppressWarnings("rawtypes")
	public BoardGameTask(){
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {

		if(CommonConstants.watch){ // If set to Visually Evaluate the Task
		}

		BoardGamePlayer evolved = new BoardGamePlayerOneStepEval<T>(individual.getPhenotype());
		BoardGamePlayer[] players = new BoardGamePlayer[]{evolved, opponent};
		//bg.reset();
		while(!bg.isGameOver()){
			//System.out.println(game);
			bg.move(players[bg.getCurrentPlayer()]);
		}
//		System.out.println("Game over");
//		System.out.println(game);
		
		List<Integer> winners = bg.getWinners();
		
		double fitness = winners.size() == 2 ? 0 : // two winners means tie: fitness is 0 
						(winners.get(0) == 0 ? 1 // If the one winner is 0, then the neural network won: fitness 1
											 : -2); // Else the network lost: fitness -2
		
		// TODO: What is the fitness?
		Pair<double[], double[]> evalResults = new Pair<double[], double[]>(new double[] { fitness }, new double[0]);			
		return evalResults; // Returns the Fitness of the individual's Genotype<T>
	}

	// Used for Hyper-NEAT
	@Override
	public int numCPPNInputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	// Used for Hyper-NEAT
	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		// TODO Auto-generated method stub
		return null;
	}

	// Used for Hyper-NEAT
	@Override
	public List<Substrate> getSubstrateInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	// Used for Hyper-NEAT
	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
