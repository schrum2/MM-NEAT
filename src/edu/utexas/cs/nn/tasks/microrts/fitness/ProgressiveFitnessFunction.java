package edu.utexas.cs.nn.tasks.microrts.fitness;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;

public class ProgressiveFitnessFunction extends RTSFitnessFunction{
	
	public ProgressiveFitnessFunction(){}
	
	/**
	 * uses information collected over time in MicroRTSTask.oneEval
	 */
	@Override
	public Pair<double[], double[]> getFitness(GameState gs) {
		return null;
	}

	@Override
	public String[] getFunctions() {
		return new String[]{"","","Average Unit Difference"};
	}

}
