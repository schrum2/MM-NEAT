package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * 
 * @author rollinsa
 * 
 * reward the prey for being as far from the predators as possible at the end of the game
 * if the prey are dead then distance for that prey is counted as zero (meaning that the
 * prey are also inherently encouraged to survive because it is trying to maximize distance)
 */
public class PreyMaximizeDistanceFromPredatorsObjective<T extends Network> extends GridTorusObjective<T> {

	/**
	 * Finds the sum of the sum of the distances from each prey to each predator. 
	 * In other words, for each prey, get the distances to each predator in a list (array). Then, add up these distances 
	 * so that there is a list (array) of sums of distances for each prey. Then add up these sums of distances for
	 * each prey to have one total sum.
	 * @param preds
	 * @param prey
	 * @return the sum of all distances
	 */
	public static double sumOfPreyToPredDistances(TorusAgent[] preds, TorusAgent[] prey){
		//this double array holds the distances to each pred for each prey
		double[][] distances = new double[prey.length][preds.length];
		//this array holds the sum of distances to all predators for each prey
		//initialize all values in sumOfDists to zero (Java does by default)
		double[] sumOfDists = new double[prey.length];
		for(int i = 0; i < prey.length; i++){
			distances[i] = prey[i].distances(preds);
			for(int j = 0; j < preds.length; j++){
				if(preds[j] != null)
					sumOfDists[i] += distances[i][j];
			}
		}
		//get the sum of the sum of distances for each prey to each predator
		return StatisticsUtilities.sum(sumOfDists);
	}
	
	
	
	@Override
	/**
	 * reward the prey for being as far from the predators as possible at the end
	 * if the prey are dead then distance for that prey is counted as zero (meaning that the
	 * prey are also inherently encouraged to survive because it is trying to maximize distance)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent[] preds = game.getPredators();
		TorusAgent[] prey = game.getPrey();
		//want this to be as high as possible to maximize distance from prey to each pred
		return sumOfPreyToPredDistances(preds,prey);
	}

}