package edu.utexas.cs.nn.tasks.microrts.fitness;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;

public class ProgressiveFitnessFunction extends RTSFitnessFunction{

	public ProgressiveFitnessFunction(){}

	/**
	 * uses information collected over time in MicroRTSUtility.oneEval
	 */
	@Override
	public ArrayList<Pair<double[], double[]>> getFitness(GameState gs) {
		ArrayList<Pair<double[], double[]>> result = new ArrayList<>(); 
		double[] fitness = new double[]{
				normalize(task.getHarvestingEfficiency(1), maxCycles * task.getResourceGainValue()),
				normalize(task.getBaseUpTime(1), maxCycles),
				normalize(task.getAverageUnitDifference(), pgs.getHeight()*pgs.getWidth())+1, // TODO: explain +1
		};
		double[] fitness2 = new double[fitness.length];
		if(coevolution){
			fitness2[0] = normalize(task.getHarvestingEfficiency(2), maxCycles * task.getResourceGainValue());
			fitness2[1] = normalize(task.getBaseUpTime(2), maxCycles);
			fitness2[2] = (normalize(task.getAverageUnitDifference(), pgs.getHeight()*pgs.getWidth())+1) * -1; // TODO: explain +1 and * -1
		}

		int winner = gs.winner(); //0:win 1:loss -1:tie
		double[]other = new double[]{
				winner + 1 % 2, //1:win 0:tie -1:loss (from ai1's perspective)
				gs.getTime()
		};
		Pair<double[], double[]> data1 = new Pair<double[],double[]>(fitness, other);
		Pair<double[], double[]> data2 = new Pair<double[],double[]>(fitness, other);
		result.add(data1);
		result.add(data2);
		return result;
	}

	@Override
	public String[] getFunctions() {
		// TODO: Despite the effort put in to fix this today, as I look at it, I think this is designed incorrectly.
		//       Although coevolution is happening, there is still only one copy of each fitness function per individual,
		//       which is why I've commented things out. Check that this works and let me know of any problems you run
		//       in to.
		//if(!coevolution)
			return new String[]{"Harvesting Efficiency","Time Base was Alive","Average Unit Difference"};
//		else //assuming task is co-evolving counterpart 
//			return new String[]{"p1's Harvesting Efficiency","p1's Time Base was Alive","p1's Average Unit Difference"
//							   +"p2's Harvesting Efficiency","p2's Time Base was Alive","p2's Average Unit Difference"};
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
