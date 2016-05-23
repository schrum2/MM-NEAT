package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * 
 * @author rollinsa
 * 
 * reward the predators for being as close to the prey as possible at the end of the game
 * if the prey are dead then distance from that prey is counted as zero (meaning that the
 * predators are also inherently encouraged to eat the prey)
 */
public class PredatorMinimizeDistanceFromPreyObjective<T extends Network> extends GridTorusObjective<T> {

	/**
	 * Finds the sum of the sum of the distances from each predator to each prey. 
	 * In other words, for each predator, get the distances to each prey in a list (array). Then, add up these distances 
	 * so that there is a list (array) of sums of distances for each predator. Then add up these sums of distances for
	 * each predator to have one total sum.
	 * @param preds
	 * @param prey
	 * @return the sum of all distances
	 */
	public static double sumOfPredToPreyDistances(TorusAgent[] preds, TorusAgent[] prey){
		//this double array holds the distances to each prey for each pred
		double[][] distances = new double[preds.length][prey.length];
		//this array holds the sum of distances to all prey for each predator
		//initialize all values in sumOfDists to zero (Java does by default)
		double[] sumOfDists = new double[preds.length];
		for(int i = 0; i < preds.length; i++){
			distances[i] = preds[i].distances(prey);
			for(int j = 0; j < prey.length; j++){
				if(prey[j] != null)
					sumOfDists[i] += distances[i][j];
			}
		}
		//get the sum of the sum of distances for each predator to each prey
		return StatisticsUtilities.sum(sumOfDists);
	}
	
	
	
	@Override
	/**
	 * reward the predators for being as close to the prey as possible at the end
	 * if the prey are dead then distance from that prey is counted as zero (meaning that the
	 * predators are also inherently encouraged to eat the prey)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent[] preds = game.getPredators();
		TorusAgent[] prey = game.getPrey();
		//want this to be as low as possible to minimize distance to prey for each pred
		return -sumOfPredToPreyDistances(preds,prey);
	}

	@Override
	/**
	 * worst possible score for a predator is the sum of the maximum possible distance from each the prey for each pred
	 */
	public double minScore(){
		int numPreds = Parameters.parameters.integerParameter("torusPredators");
		int numPrey = Parameters.parameters.integerParameter("torusPreys");
		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");
		//max possible distance is the sum of half the world height and width because the world wraps around
		double maxDist = height/2 + width/2;
		//this array holds the sum of distances to all prey for each predator
		double[] sumOfDists = new double[numPreds];
		for(int i = 0; i < numPreds; i++){
			sumOfDists[i] = maxDist * numPrey;
		}
		//get the sum of the sum of distances for each predator to each prey
		//min score, sum of max distances to all prey from each pred
		return -StatisticsUtilities.sum(sumOfDists);
	}
}