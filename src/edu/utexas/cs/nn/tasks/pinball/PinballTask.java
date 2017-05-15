package edu.utexas.cs.nn.tasks.pinball;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import pinball.PinBall;
import pinball.State;

public class PinballTask<T extends Network> extends NoisyLonerTask<T>implements NetworkTask {
	
	/**
	 * Constructor for a new PinballTask
	 */
	public PinballTask(){
        MMNEAT.registerFitnessFunction("Reward");
	}
	
	/**
	 * Returns the number of Objectives for the PinballTask
	 * 
	 * @return 1, The number of Objectives for the PinballTask
	 */
	@Override
	public int numObjectives() {
		// Appears to only have one goal; reach the end of the obstacles
		return 1;
	}

	/**
	 * Returns the TimeStamp for a PinballTask
	 * 
	 * @return 0, because the TimeStamp doesn't appear useful for this task
	 */
	@Override
	public double getTimeStamp() {
		// Doesn't appear to be necessary for this Task, but may be used later.
		return 0;
	}

	/**
	 * Returns a String containing the Sensor Labels for the PinballTask
	 * 
	 * @return String containing the Sensor Labels for the PinballTask
	 */
	@Override
	public String[] sensorLabels() {
		return new String[]{"X-Position", "Y-Position", "X-Velocity", "Y-Velocity"};
	}

	/**
	 * Returns a String containing the Output Labels for the PinballTask
	 * 
	 * @return String containing the Output Labels for the PinballTask
	 */
	@Override
	public String[] outputLabels() {
		// TODO Ensure "Up" and "Down" are correct
		return new String[]{"Right", "Up", "Left", "Down", "None"};
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

		PinballViewer view = null;
		
		if(CommonConstants.watch){
			view = new PinballViewer();
			view.setVisible(true);
			view.setAlwaysOnTop(true);	
		}
		
		PinBall p = new PinBall("data/pinball/" + Parameters.parameters.stringParameter("pinballConfig"));
		Network n = individual.getPhenotype();
		double fitness = 0;
		int timeLimit = 1000;

		view = new PinballViewer();
		view.setVisible(true);
		view.setAlwaysOnTop(true);
		
		do {
			State s = p.getState();
			double[] sensors = s.getDescriptor();
			double[] outputs = n.process(sensors);
			int action = StatisticsUtilities.argmax(outputs);
			double rew = p.step(action);
	
		if(view != null){
			view.actionPerformed(action);
		}			

			fitness += rew;
			timeLimit--;
		} while(!p.episodeEnd() && timeLimit > 0);
		
		Pair<double[], double[]> evalResults = new Pair<double[], double[]>(new double[] {fitness}, new double[0]);			

		return evalResults;
	}

}
