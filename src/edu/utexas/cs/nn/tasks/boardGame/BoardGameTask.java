package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.*;
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
	
	/**
	 * Constructor for a new BoardGameTask
	 */
	public BoardGameTask(){
		MMNEAT.registerFitnessFunction("Reward");
		
	    if(Parameters.parameters.booleanParameter("moBoardGame")){
	       	 MMNEAT.registerFitnessFunction("Time Alive"); // Generic MO BoardGame label. Can be changed later.
	    }
	}

	/**
	 * Returns the number of Objectives for the BoardGameTask
	 * 
	 * @return The number of Objectives for the BoardGameTask
	 */
	@Override
	public int numObjectives() {
		if(Parameters.parameters.booleanParameter("moBoardGame")){
			return 2;
		}else{ // Else, only Reach Goal
			return 1;			
		}
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
	public String[] sensorLabels() { // TODO: Create generic BoardGame input labels
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
	 * Used for Behavioral Diversity
	 */
	@Override
	public void prep() {
		// TODO: Behavioral Diversity
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

		//MiscUtil.waitForReadStringAndEnterKeyPress();

		Network n = individual.getPhenotype();
		double fitness = 0;
		int timeLimit = 1000;

		do {
			BoardGameState s = null; // TODO: Fill with generic BoardGame instance
			double[] sensors = s.getDescriptor();
			double[] outputs = n.process(sensors);
			int action = StatisticsUtilities.argmax(outputs);
			double rew = 0; // TODO: Figure out what to initialize "rew" to; may just be 0, but double-check

			if(view != null){ // If the BoardGameTaskViewer exists, update it

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(Parameters.parameters.booleanParameter("stepByStep")){
					System.out.print("Press enter to continue");
					MiscUtil.waitForReadStringAndEnterKeyPress();
				}
			}	

			// TODO: Add a GameState Reward update here; make "rew" equal to the new reward
			
			fitness += rew;
			timeLimit--;
			
		} while(timeLimit > 0); // TODO: Replace with generic BoardGame end State

		
		
		Pair<double[], double[]> evalResults = new Pair<double[], double[]>(new double[] {fitness}, new double[0]);	

		if(Parameters.parameters.booleanParameter("moPinball")){
			evalResults = new Pair<double[], double[]>(new double[] { fitness, 0 }, new double[0]); // TODO: Fill with generic second Objective for BoardGame.
		} else {
			evalResults = new Pair<double[], double[]>(new double[] { fitness }, new double[0]);			
		}
		
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
