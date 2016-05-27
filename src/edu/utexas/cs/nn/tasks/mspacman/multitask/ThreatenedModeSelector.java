package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToTargetThanThreatGhostBlock;

/**
 * A Mode selector which selects between 2 modes based on the following: 0)
 * PacMan is safe because can reach her target before the ghosts can reach her
 * (this is determined by various distance factors) 1) PacMan is threatened
 * because the distance between her and her target is greater than the distance
 * between her and the closest threatening ghost
 * 
 * @author Jacob Schrum
 */
public class ThreatenedModeSelector extends MsPacManModeSelector {

	public static final int THREATENED = 0;
	public static final int SAFE = 1;
	private final int closeGhostDistance;

	/**
	 * constructs mode selector and defines the closeGhostDistance threshold
	 * based on parameters
	 */
	public ThreatenedModeSelector() {
		super();
		closeGhostDistance = Parameters.parameters.integerParameter("closeGhostDistance");
	}

	/**
	 * A Mode selector which selects between 2 modes based on the following: 0)
	 * PacMan is safe because can reach her target before the ghosts can reach
	 * her (this is determined by various distance factors) 1) PacMan is
	 * threatened because the distance between her and her target is greater
	 * than the distance between her and the closest threatening ghost
	 * 
	 * @return mode
	 */
	public int mode() {
		int current = gs.getPacmanCurrentNodeIndex();
		int[] targets = gs.getThreatGhostLocations();
		if (targets.length == 0) {
			return SAFE;
		}
		int node = gs.getClosestNodeIndexFromNodeIndex(current, targets);
		double distance = gs.getShortestPathDistance(current, node);
		if (distance < closeGhostDistance) {
			return THREATENED;
		}
		// Somewhat analagous to counting the number of safe ants
		double safeDirs = 0;
		double options = 0;
		int[] neighbors = gs.neighbors(current);
		for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
			if (neighbors[i] != -1) {
				VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock block = new VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock(
						i);
				boolean safe = VariableDirectionCloserToTargetThanThreatGhostBlock
						.canReachAnyTargetSafelyInDirection(gs, block.getTargets(gs), i);
				if (safe) {
					safeDirs++;
				}
				options++;
			}
		}
		// Is this the best threshold?
		return safeDirs / options > 0.3 ? SAFE : THREATENED;
	}

	/**
	 * There are 2 modes for this mode selector
	 * 
	 * @return 2
	 */
	public int numModes() {
		return 2;
	}

	@Override
	/**
	 * gets the associated fitness scores with this mode selector
	 * 
	 * @return an int array holding the score for if pacMan is threatened in the
	 *         first index and the score for if pacman is safe in the second
	 *         index
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[THREATENED] = GAME_SCORE; // tentative
		result[SAFE] = GAME_SCORE; // tentative
		return result;
	}
}
