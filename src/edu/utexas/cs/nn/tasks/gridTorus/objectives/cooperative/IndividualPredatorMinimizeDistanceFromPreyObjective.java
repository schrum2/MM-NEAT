package edu.utexas.cs.nn.tasks.gridTorus.objectives.cooperative;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.GridTorusObjective;

/**
 * 
 * @author rollinsa
 * 
 *         reward the predator for being as close to the prey as possible at
 *         the end of the game. If a prey is dead then distance from that prey
 *         is counted as zero (meaning that the predator is also inherently
 *         encouraged to eat the prey)
 */
public class IndividualPredatorMinimizeDistanceFromPreyObjective<T extends Network> extends GridTorusObjective<T> {

	private int predIndex = -1;

	/**
	 * Creates the objective for the fitness for the given predator 
	 * @param i index of the predator
	 */
	public IndividualPredatorMinimizeDistanceFromPreyObjective(int i){
		predIndex = i;
	}

	@Override
	/**
	 * reward the predator for being as close to the prey as possible at the
	 * end. If the prey are dead then distance from that prey is counted as zero
	 * (meaning that the predator is also inherently encouraged to eat the
	 * prey)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent pred = game.getPredators()[predIndex];
		TorusAgent[] prey = game.getPrey();
		double score = 0;
		for(int i = 0; i < prey.length; i++){
			//if the prey was eaten, rewards pred by not subtracting from fitness score
			if(prey[i] != null) 
				score -= pred.distance(prey[i]);
		}

		return score;
	}

	@Override
	/**
	 * worst possible score for the predator is the maximum possible
	 * distance from the predator to the prey for each prey
	 */
	public double minScore() {
		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");
		// max possible distance is the sum of half the world height and width
		// because the world wraps around
		double maxDist = height / 2 + width / 2;
		// min score, max distance from this predator to each prey
		return -(maxDist*Parameters.parameters.integerParameter("torusPreys"));
	}
}
