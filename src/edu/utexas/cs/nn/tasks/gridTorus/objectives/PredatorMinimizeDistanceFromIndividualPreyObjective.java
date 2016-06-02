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
 *         reward the predators for being as close to individual prey as possible at
 *         the end of the game. If the prey is dead then distance from the prey
 *         is counted as zero (meaning that the predators are also inherently
 *         encouraged to eat the prey). This will provide a distance score for just
 *         the specific prey given (which will be minimized distance to all predators
 *         for each prey individually rather than as a whole)
 */
public class PredatorMinimizeDistanceFromIndividualPreyObjective<T extends Network> extends GridTorusObjective<T> {

	private int preyIndex = -1;

	/**
	 * Creates the objective for the fitness for the predators against the given prey 
	 * @param i index of the prey
	 */
	public PredatorMinimizeDistanceFromIndividualPreyObjective(int i){
		preyIndex = i;
	}

	@Override
	/**
	 *         reward the predators for being as close to individual prey as possible at
	 *         the end of the game. If the prey is dead then distance from the prey
	 *         is counted as zero (meaning that the predators are also inherently
	 *         encouraged to eat the prey). This will provide a distance score for just
	 *         the specific prey given (which will be minimized distance to all predators
	 *         for each prey individually rather than as a whole)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent prey = game.getPrey()[preyIndex];
		//if the prey is null, it was eaten, give max score of 0
		if(prey == null)
			return 0;
		
		return -(StatisticsUtilities.sum(prey.distances(game.getPredators())));
	}

	@Override
	/**
	 * worst possible score for the predators is the max distance from the prey for each predator
	 */
	public double minScore() {
		int height = Parameters.parameters.integerParameter("torusYDimensions");
		int width = Parameters.parameters.integerParameter("torusXDimensions");
		// max possible distance is the sum of half the world height and width
		// because the world wraps around
		double maxDist = height / 2 + width / 2;
		// min score, max distance from each predator to the prey
		return -(maxDist*Parameters.parameters.integerParameter("torusPredators"));
	}
}