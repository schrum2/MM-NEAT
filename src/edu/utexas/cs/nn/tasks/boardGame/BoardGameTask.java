package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.*;
import boardGame.rps.RPSPlayer;
import boardGame.rps.RPSPlayerRandom;
import boardGame.rps.RockPaperScissors;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class BoardGameTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask{

	BoardGameViewer view = null;
	BoardGame bg;
	
	/**
	 * Constructor for a new BoardGameTask
	 */
	public BoardGameTask(){
		MMNEAT.registerFitnessFunction("?????");
		
		// TODO: Instantiate bg using ClassCreation
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
		// Should be associated with BoardGame instance bg, as in bg.featureLabels();
		return new String[]{};
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
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {

		if(CommonConstants.watch){ // If set to Visually Evaluate the Task
		}

		BoardGamePlayer p1 = null; // create from Genotype individual
		BoardGamePlayer p2 = null; // The opponent
		
		double fitness = 0;
		BoardGamePlayer[] players = new BoardGamePlayer[]{p1, p2};
		//bg.reset();
		while(!bg.isGameOver()){
			//System.out.println(game);
			bg.move(players[bg.getCurrentPlayer()]);
		}
//		System.out.println("Game over");
//		System.out.println(game);
			
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
