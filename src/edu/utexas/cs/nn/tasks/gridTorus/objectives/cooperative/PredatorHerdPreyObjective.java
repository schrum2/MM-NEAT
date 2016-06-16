package edu.utexas.cs.nn.tasks.gridTorus.objectives.cooperative;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.GridTorusObjective;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * 
 * @author rollinsa
 * 
 *         reward the predator for minimizing the distance between all of the prey
 *         (the distance from each prey to each other prey summed up)
 *         If a prey is dead, the predator is rewarded the maximum scores for that prey
 *         (which would be zero since zero is the max score)
 */
public class PredatorHerdPreyObjective<T extends Network> extends GridTorusObjective<T> {

	@Override
	/**
	 *         reward the predator for minimizing the distance between all of the prey
	 *         (the distance from each prey to each other prey summed up)
	 *         If a prey is dead, the predator is rewarded the maximum scores for that prey
	 *         (which would be zero since zero is the max score)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent[] prey = game.getPrey();
		double score = 0;
		for(int i = 0; i < prey.length; i++){
			//if the prey is null, it was eaten, give max score of 0 for that prey by not subtracting anything
			if(prey[i] != null){
				score -= StatisticsUtilities.sum(prey[i].distances(prey));
			}	
		}
		return score;
	}

	@Override
	/**
	 * worst possible score for the predator is the sum of the maximum possible
	 * distance each prey to each other prey
	 */
	public double minScore() {
		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");
		int numPrey = Parameters.parameters.integerParameter("torusPreys");
		// max possible distance is the sum of half the world height and width
		// because the world wraps around (this is also the worst score)
		int maxDist = (height / 2) + (width / 2);
		double score = 0;
		for(int i = 0; i < numPrey; i++){
			//numPrey minus one because the distances would include the distance to itself
			score -= (numPrey-1)*maxDist;
		}
		return  score;
	}
}
