package edu.utexas.cs.nn.tasks.gridTorus.objectives.cooperative;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.gridTorus.objectives.GridTorusObjective;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * 
 * @author rollinsa
 * 
 *         reward the prey for being as far from the predators as possible at
 *         the end of the game. If the prey is dead then it receives the lowest 
 *         possible score (zero)
 */
public class IndividualPreyMaximizeDistanceFromPredatorsObjective<T extends Network> extends GridTorusObjective<T> {

	private int preyIndex = -1;

	/**
	 * Creates the objective for the fitness for the given prey 
	 * @param i index of the prey
	 */
	public IndividualPreyMaximizeDistanceFromPredatorsObjective(int i){
		preyIndex = i;
	}

	@Override
	/**
	 *         reward the prey for being as far from the predators as possible at
	 *         the end of the game. If the prey is dead then it receives the lowest 
	 *         possible score (zero)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent prey = game.getPrey()[preyIndex];
		//if the prey is null, it was eaten, give min score of 0
		if(prey == null)
			return 0;

		return StatisticsUtilities.sum(prey.distances(game.getPredators()));
	}

}