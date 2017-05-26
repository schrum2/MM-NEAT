package edu.utexas.cs.nn.tasks.microrts.fitness;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;

/**
 * @author alicequint
 * not yet supported by MicroRTSTask
 */
public class AggressiveFitnessFunction extends RTSFitnessFunction{

	@Override
	public Pair<double[], double[]> getFitness(GameState gs) {
		double[] fitness = new double[] {
//				normalize(task.getHarvestingEfficiency(), maxCycles * task.getResourceGainValue()),
//				normalize(task.getBaseUpTime(), maxCycles),
//				normalize(task.getAverageUnitDifference(), pgs.getHeight()*pgs.getWidth())+1,
			};
			Pair<double[], double[]> result = new Pair<double[],double[]>(fitness, null);
			return result;
	}

	@Override
	public String[] getFunctions() {
		return new String[]{"Harvesting Efficiency","Average Unit Distance from Enemy Base","Average Unit Difference"};
		//possible others: "Enemy Base Kill Time" "Kills per Cycle"
	}
	
	//makes 0 => -1 , max => 1
	private double normalize(double value, double max){
		return (2*value - max) / max;
	}

}
