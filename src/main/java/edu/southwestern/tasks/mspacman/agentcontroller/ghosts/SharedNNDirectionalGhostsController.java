package edu.southwestern.tasks.mspacman.agentcontroller.ghosts;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.stats.StatisticsUtilities;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public abstract class SharedNNDirectionalGhostsController extends SharedNNGhostsController {

	public SharedNNDirectionalGhostsController(Network n) {
		super(n);
	}

	@Override
	public int getDirection(GameFacade gf, Constants.GHOST ghost) {
		double[] dirPreferences = getDirectionPreferences(gf, GameFacade.ghostToIndex(ghost));
		int direction = directionFromPreferences(dirPreferences);
		return direction;
	}

	public int directionFromPreferences(double[] dirPreferences) {
		return CommonConstants.probabilisticSelection ? StatisticsUtilities.probabilistic(dirPreferences)
				: (CommonConstants.softmaxSelection
						? StatisticsUtilities.softmax(dirPreferences, CommonConstants.softmaxTemperature)
						: StatisticsUtilities.argmax(dirPreferences));
	}

	public abstract double[] getDirectionPreferences(GameFacade gf, int ghostIndex);
}
