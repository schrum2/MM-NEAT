package edu.utexas.cs.nn.tasks.gridTorus.objectives;

import edu.utexas.cs.nn.evolution.Organism;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.networks.Network;

/**
 * 
 * @author rollinsa The prey fitness function appearing in Constructing
 *         Competitive and Cooperative Agent Behavior Using Coevolution By
 *         Aditya Rawal, Padmini Rajagopalan, and Risto Miikkulainen
 *         http://nn.cs.utexas.edu/?rawal:cig10
 * 
 *         Note: this is generalized in order to support variable number of
 *         predators and prey Prey fitness score: 25 ------ if neither is caught
 *         12.5 ------ if one prey is caught 12.5p / M ------ if both prey
 *         caught p = number of time steps with at least one prey alive M =
 *         maximum possible number of time steps
 * 
 *         Encourages survival of all prey, but still rewards survival of some
 *         but not all, and rewards surviving as long as possible
 */
public class PreyRawalRajagopalanMiikkulainenObjective<T extends Network> extends GridTorusObjective<T> {

	public static final double ALL_PREY_SCORE = 10;

	@Override
	/**
	 * Find the score of the prey based on if all prey died, if no prey died, or
	 * if some prey died (generalized for variable number of prey/preds)
	 */
	public double fitness(Organism<T> individual) {
		TorusAgent[] prey = game.getPrey();
		double numCaught = 0;
		for (TorusAgent p : prey) {
			if (p == null) {
				numCaught++;
			}
		}
		// when no prey have died. Best possible score
		if (numCaught == 0)
			return ALL_PREY_SCORE;
		double eachPreyCaughtScore = (ALL_PREY_SCORE * (1.0 / prey.length));
		// when all prey have died
		if (numCaught == prey.length)
			return eachPreyCaughtScore * (((double) game.getTime()) / ((double) game.getTimeLimit()));
		// when some prey have died
		return (prey.length - numCaught) * eachPreyCaughtScore;
	}

}
