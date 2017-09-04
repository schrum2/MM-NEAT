package edu.southwestern.tasks.gridTorus.objectives.cooperative;

import edu.southwestern.evolution.Organism;
import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gridTorus.objectives.GridTorusObjective;

/**
 * 
 * @author rollinsa
 * 
 *         reward the predator for being as close to the prey as possible at
 *         the end of the game. If the prey is dead then distance from the prey
 *         is counted as zero (meaning that the predator is also inherently
 *         encouraged to eat the prey)
 */
public class IndividualPredatorMinimizeDistanceFromIndividualPreyObjective<T extends Network> extends GridTorusObjective<T> {

	private int predIndex = -1;
	private int preyIndex = -1;

	/**
	 * Creates the objective for the fitness for the given predator 
	 * @param i index of the predator
	 * @param j index of the prey
	 */
	public IndividualPredatorMinimizeDistanceFromIndividualPreyObjective(int i, int j){
		predIndex = i;
		preyIndex = j;
	}

	@Override
	/**
	 *         reward the predator for being as close to the prey as possible at
	 *         the end of the game. If the prey is dead then distance from the prey
	 *         is counted as zero (meaning that the predator is also inherently
	 *         encouraged to eat the prey)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent pred = game.getPredators()[predIndex];
		TorusAgent prey = game.getPrey()[preyIndex];

		//if the prey is null, it was eaten, give max score of 0
		if(prey == null)
			return 0;
		
		return -(pred.distance(prey));
	}

	@Override
	/**
	 * worst possible score for the predator is the maximum possible
	 * distance from the predator to the prey
	 */
	public double minScore() {
		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");
		// max possible distance is the sum of half the world height and width
		// because the world wraps around (this is also the worst score)
		return -(height / 2 + width / 2);
	}
}
