package edu.southwestern.tasks.gridTorus.objectives;

import edu.southwestern.evolution.Organism;
import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.stats.StatisticsUtilities;

/**
 * 
 * @author rollinsa
 * 
 *         Encourages catching all prey with a very high score for doing so. If
 *         the prey aren't all caught, this fitness function will emphasize that
 *         as many prey are caught as possible, and with minimized distance to
 *         any remaining prey
 */
public class PredatorCatchCloseObjective<T extends Network> extends GridTorusObjective<T> {

	public static final double NO_PREY_SCORE = Parameters.parameters.integerParameter("torusPreys");

	@Override
	/**
	 *         Encourages catching all prey with a very high score for doing so. If
	 *         the prey aren't all caught, this fitness function will emphasize that
	 *         as many prey are caught as possible, and with minimized distance to
	 *         any remaining prey
	 */
	public double fitness(Organism<T> individual) {

		TorusAgent[] prey = game.getPrey();
		TorusAgent[] preds = game.getPredators();
		int numPrey = prey.length;
		int numPreds = preds.length;
		double numCaught = 0;

		for (TorusAgent p : prey) {
			if (p == null) {
				numCaught++;
			}
		}
		// when all prey have been caught. Best possible score
		// this should be the score that would be returned from the
		// algorithm below when catching all prey, but this avoids
		// the unnecessary calculations and is a simplified check.
		if (numCaught == numPrey)
			return NO_PREY_SCORE;

		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");

		// max possible distance is the sum of half the world height and width
		// because the world wraps around
		double maxDistance = (height / 2) + (width / 2);

		//array to hold each preys' sum of distances to each member of the predator team.
		double [] preyDistancesFromPredTeam = new double [numPrey];
		for(int i = 0; i < numPrey; i++){
			//if the prey was eaten
			if(prey[i] == null){
				//award the minimum possible distance score because the numCaught weight is the same as the maximum
				//possible distance score and the numCaught weight will now have just gone up by one
				//this allows for the distance score to then take into account the distance to the next prey to catch
				//because the distance to the next prey will now be the distance score (because the max distance score
				//is the one that is used in the algorithm, in order to score for getting closer to each individual prey)
				preyDistancesFromPredTeam[i] = 0.0;
			} else{
			//Finds the sum of the distances from this prey to each predator, 
			//divides this result by the product of the maximum possible distance for each predator to normalize the value, 
			//and take 1 subtracted by this result to encourage minimizing this distance.
			preyDistancesFromPredTeam[i] = 1.0 - ((StatisticsUtilities.sum(prey[i].distances(preds)))/(numPreds*maxDistance));
			}
		}

		// needs to be less than the maximum score, NO_PREY_SCORE, which is
		// given when all prey are caught.
		double WEIGHT = NO_PREY_SCORE / (numPrey);
		
		//one option here is to take the max of the preyDistances array to encourage getting as close as possible to a single prey
		//another option is to simply factor in all preyDistances in the array, but this could be problematic because the predators
		//may attempt to go to a middle ground between each prey, since increasing the distance score to one prey by getting closer
		//to that one prey may mean decreasing the distance score to the other prey.
		double d = StatisticsUtilities.maximum(preyDistancesFromPredTeam);

		// distance score is weighted to be less than catching each prey, but
		// the distance score still helps the predators to learn how to
		// get close to the prey if they haven't learned to catch any prey yet
		return (d * WEIGHT) + (numCaught * WEIGHT);
	}

}