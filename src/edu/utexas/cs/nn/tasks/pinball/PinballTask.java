package edu.utexas.cs.nn.tasks.pinball;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import pinball.PinBall;
import pinball.State;

public class PinballTask<T extends Network> extends NoisyLonerTask<T>implements NetworkTask {

	
	public PinballTask(){
        MMNEAT.registerFitnessFunction("Reward");
	}
	
	@Override
	public int numObjectives() {
		// Appears to only have one goal; reach the end of the obstacles
		return 1;
	}

	@Override
	public double getTimeStamp() {
		// Doesn't appear to be necessary for this Task, but may be used later.
		return 0;
	}

	@Override
	public String[] sensorLabels() {
		return new String[]{"X-Position", "Y-Position", "X-Velocity", "Y-Velocity"};
	}

	@Override
	public String[] outputLabels() {
		// TODO Ensure "Up" and "Down" are correct
		return new String[]{"Right", "Up", "Left", "Down", "None"};
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		PinBall p = new PinBall("data/pinball/pinball_simple_single.cfg");
		Network n = individual.getPhenotype();
		double fitness = 0;
		do {
			State s = p.getState();
			double[] sensors = s.getDescriptor();
			double[] outputs = n.process(sensors);
			int action = StatisticsUtilities.argmax(outputs);
			double rew = p.step(action);
			fitness += rew;			
		} while(!p.episodeEnd());
		
		Pair<double[], double[]> evalResults = new Pair<double[], double[]>(new double[] {fitness}, new double[0]);			

		return evalResults;
	}

}
