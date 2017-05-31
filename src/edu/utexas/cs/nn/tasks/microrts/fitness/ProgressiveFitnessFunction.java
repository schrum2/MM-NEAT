package edu.utexas.cs.nn.tasks.microrts.fitness;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;

public class ProgressiveFitnessFunction extends RTSFitnessFunction{

	public ProgressiveFitnessFunction(){}

	/**
	 * uses information collected over time in MicroRTSUtility.oneEval
	 */
	@Override
	public Pair<double[], double[]> getFitness(GameState gs) {

		double[] fitness = new double[] {
				normalize(task.getHarvestingEfficiency(), maxCycles * task.getResourceGainValue()),
				normalize(task.getBaseUpTime(), maxCycles),
				normalize(task.getAverageUnitDifference(), pgs.getHeight()*pgs.getWidth())+1,
		};
		int winner = gs.winner(); //0:win 1:loss -1:tie
		double[]other = new double[]{
				winner + 1 % 2, //1:win 0:tie -1:loss 
				gs.getTime()
		};
		Pair<double[], double[]> result = new Pair<double[],double[]>(fitness, other);
		return result;
	}

	@Override
	public String[] getFunctions() {
		return new String[]{"Harvesting Efficiency","Time Base was Alive","Average Unit Difference"};
	}

	/**
	 * precondition: max > 0, min = 0.
	 * @param value :value to be converted
	 * @param max largest possible number in this category
	 * @return value on a scale from -1 to 1 with 1 being max
	 */
	private double normalize(double value, double max){
		return (2*value - max) / max;
	}

}
