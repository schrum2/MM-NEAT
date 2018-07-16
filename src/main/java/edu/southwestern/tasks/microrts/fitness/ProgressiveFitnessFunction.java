package edu.southwestern.tasks.microrts.fitness;

import java.util.ArrayList;

import edu.southwestern.util.datastructures.Pair;
import micro.rts.GameState;

/**
 * fitness function that uses data tracked in MicroRTSUtility.oneEval()
 * 
 * @author alicequint
 */
public class ProgressiveFitnessFunction extends RTSFitnessFunction{

	public ProgressiveFitnessFunction(){}

	/**
	 * uses information collected over time in MicroRTSUtility.oneEval
	 */
	@Override
	public ArrayList<Pair<double[], double[]>> getFitness(GameState gs) {
		
		ArrayList<Pair<double[], double[]>> result = new ArrayList<>(); 
		
		//fitness is measured in terms of harvesting efficiency, length of time the base survived, the average unit difference,
		//and the percentage of enemies destroyed.
		double[] fitness = new double[]{
				normalize(task.getHarvestingEfficiency(1), gameEndTime),
				normalize(task.getBaseUpTime(1), gameEndTime),
				normalize(task.getAverageUnitDifference(), pgs.getHeight()*pgs.getWidth())+1,
				// normalize() assumes that the results range from 0 to a maximum number, whereas unit differences is 0 on average
				// and very unlikely to be anywhere close to the max; +1 makes it so that (-2 to 0) => (-1 to 1)
				normalize(task.getPercentEnemiesDestroyed(1), 100),
		};
		
		//array to hold opponentFitness
		double[] opponentFitness = new double[fitness.length];
		
		//If we are coevolving, then we must measure opponent fitness.
		if(coevolution){
			opponentFitness[0] = normalize(task.getHarvestingEfficiency(2), gameEndTime);
			opponentFitness[1] = normalize(task.getBaseUpTime(2), gameEndTime);
			opponentFitness[2] = fitness[2] * -1; // * -1 because each players UnitDifference score will be the Reverse of the other's.
			opponentFitness[3] = normalize(task.getPercentEnemiesDestroyed(2), 100);
		}
		
		int winner = gs.winner(); //0:win 1:loss -1:tie
		
		//Other scores contain who won, the length of the match, how many map switches there were, how many enemy switches there were,
		//and the number of evaluations there were
		double[]other = new double[]{
				(winner == 1 ? -1 : winner + 1), //changes 0:win, -1:tie, 1:loss  => 1:win 0:tie -1:loss (from ai1's perspective)
				gs.getTime(),
				mapSwitches, //updated when map is switched (belongs to parent)
				enemySwitches,
				numEvals,
		};
		
		//Give network it's fitness and other data, then add to result.
		Pair<double[], double[]> data1 = new Pair<double[],double[]>(fitness, other);
		result.add(data1);

		//Will Price edited some code. Originally, data1 and data2 were exact copies of one another and were added to result.
		//I believe that data2 was intended to be data relating to the opponent. Thus, if coevolution is true, then data2
		//should be created and contain data about the opponents fitness.
		if(coevolution) {
			Pair<double[], double[]> data2 = new Pair<double[],double[]>(opponentFitness, other);
			result.add(data2);
		}

		
		
		return result;
	}
	
	@Override
	//Getter method for the current fitness functions
	public String[] getFunctions() {
			return new String[]{"Harvesting Efficiency","Time Base was Alive","Average Unit Difference","% Enemies Destroyed"}; //Fitness
	}
	
	@Override
	//Getter method for the current other score functions
	public String[] getOtherScores(){
		return new String[]{"win?","time game lasted","map switches","enemy switches","number of game state evals"}; //Other
	}

	/**
	 * precondition: max > 0, min = 0.
	 * @param value :value to be converted
	 * @param max largest possible number in this category
	 * @return value on a scale from -1 to 1 with 1 being max
	 */
	protected double normalize(double value, double max){
		return (2*value - max) / max;
	}

}
