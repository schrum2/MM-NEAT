package edu.southwestern.tasks.gridTorus.objectives.cooperative;

import edu.southwestern.evolution.Organism;
import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.gridTorus.objectives.GridTorusObjective;

/**
 * 
 * @author rollinsa
 * 
 *         reward the prey for being as far from the predator as possible at
 *         the end of the game. If the prey is dead then it receives the lowest 
 *         possible score (zero)
 */
public class IndividualPreyMaximizeDistanceFromIndividualPredatorObjective<T extends Network> extends GridTorusObjective<T> {

	private int predIndex = -1;
	private int preyIndex = -1;

	/**
	 * Creates the objective for the fitness for the given prey 
	 * @param i index of the predator
	 * @param j index of the prey
	 */
	public IndividualPreyMaximizeDistanceFromIndividualPredatorObjective(int i, int j){
		predIndex = i;
		preyIndex = j;
	}

	@Override
	/**
	 *         reward the prey for being as far from the predator as possible at
	 *         the end of the game. If the prey is dead then it receives the lowest 
	 *         possible score (zero)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent pred = game.getPredators()[predIndex];
		TorusAgent prey = game.getPrey()[preyIndex];

		//if the prey is null, it was eaten, give min score of 0
		if(prey == null)
			return 0;

		return prey.distance(pred);
	}

}