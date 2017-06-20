package edu.utexas.cs.nn.tasks.microrts.fitness;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;

public class WinLossFitnessFunction extends ProgressiveFitnessFunction{
	
	/**
	 * uses information collected over time in MicroRTSUtility.oneEval
	 */
	@Override
	public ArrayList<Pair<double[], double[]>> getFitness(GameState gs) {
		ArrayList<Pair<double[], double[]>> result = new ArrayList<>();
		int winner = gs.winner(); //0:win 1:loss -1:tie
		double[] fitness = new double[]{
				normalize(task.getHarvestingEfficiency(1), gameEndTime),
				normalize(task.getBaseUpTime(1), gameEndTime),
				normalize(task.getAverageUnitDifference(), pgs.getHeight()*pgs.getWidth())+1,
				// normalize() assumes that the results range from 0 to a maximum number, whereas unit differences is 0 on average
				// and very unlikely to be anywhere close to the max; +1 makes it so that (-2 to 0) => (-1 to 1)
				normalize(task.getPercentEnemiesDestroyed(1), 100),
				
				(winner == 1 ? -1 : winner + 1), //changes 0:win, -1:tie, 1:loss  => 1:win 0:tie -1:loss (from ai1's perspective)
				
		};
		double[] opponentFitness = new double[fitness.length];
		if(coevolution){
			opponentFitness[0] = normalize(task.getHarvestingEfficiency(2), gameEndTime);
			opponentFitness[1] = normalize(task.getBaseUpTime(2), gameEndTime);
			opponentFitness[2] = fitness[2] * -1; // * -1 because each players UnitDifference score will be the Reverse of the other's.
			opponentFitness[3] = normalize(task.getPercentEnemiesDestroyed(2), 100);
		}
		double[]other = new double[]{
				gs.getTime(),
				mapSwitches, //updated when map is switched (belongs to parent)
				enemySwitches,
				numEvals,
		};
		Pair<double[], double[]> data1 = new Pair<double[],double[]>(fitness, other);
		Pair<double[], double[]> data2 = new Pair<double[],double[]>(fitness, other);
		result.add(data1);
		result.add(data2);
		return result;
	}
	
	@Override
	public String[] getFunctions() {
			return new String[]{"Harvesting Efficiency","Time Base was Alive","Average Unit Difference","% Enemies Destroyed","Win/Loss"};//Fitness
	}
	
	@Override
	public String[] getOtherScores(){
		return new String[]{"time game lasted","map switches","enemy switches","number of game state evals"}; //Other
	}
}
