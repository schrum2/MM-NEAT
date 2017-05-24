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

		double[] fitness = new double[] {
			task.getHarvestingEfficiency(),
			normalize(task.getBaseUpTime(), maxCycles),
			task.getAverageUnitDifference(),
		};
		Pair<double[], double[]> result = new Pair<double[],double[]>(fitness, null);
		return result;
	}

	@Override
	public String[] getFunctions() {
		return new String[]{"Harvesting Efficiency","Time Base was Alive","Average Unit Difference"};
	}

	/**
	 * @param value :value to be converted
	 * @param max largest possible number in this category
	 * @return value on a scale from -1 to 1 with 1 being max
	 */
	private double normalize(double value, double max){
		return (value / max) - 2;
	}

}
