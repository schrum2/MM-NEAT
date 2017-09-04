package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

/**
 * Describes a pacman controller based on direction
 * 
 * @author Jacob Schrum
 */
public abstract class NNDirectionalPacManController extends NNPacManController {

	protected final boolean externalPreferenceNeurons;
	public static double[] previousPreferences = new double[GameFacade.NUM_DIRS];

	public NNDirectionalPacManController(Network n) {
		super(n);
		externalPreferenceNeurons = Parameters.parameters.booleanParameter("externalPreferenceNeurons");
	}

	/**
	 * finds the current direction
	 * 
	 * @param gs,
	 *            the gameFacade
	 * @return direction
	 */
	public int getDirection(GameFacade gs) {
		double[] dirPreferences = getDirectionPreferences(gs);
		System.arraycopy(dirPreferences, 0, previousPreferences, 0, GameFacade.NUM_DIRS);
		// if (watch && (nn instanceof TWEANN) && ((TWEANN) nn).numModes() > 1)
		// {
		// // Need to have a better indicator of current mode
		// gs.addPoints(CombinatoricUtilities.colorFromInt(((TWEANN)
		// nn).chosenMode), gs.neighbors(gs.getPacmanCurrentNodeIndex()));
		// }
		int direction = directionFromPreferences(dirPreferences);
		return direction;
	}

	/**
	 * find the prefered direction
	 * 
	 * @param dirPreferences
	 * @return the preferred direction based on designated preference parameters
	 */
	public int directionFromPreferences(double[] dirPreferences) {
		if (externalPreferenceNeurons) {
			dirPreferences = ArrayUtil.portion(dirPreferences, 0, dirPreferences.length - 2);
		}
		return CommonConstants.probabilisticSelection ? StatisticsUtilities.probabilistic(dirPreferences)
				: (CommonConstants.softmaxSelection
						? StatisticsUtilities.softmax(dirPreferences, CommonConstants.softmaxTemperature)
						: StatisticsUtilities.argmax(dirPreferences));
	}

	/**
	 * Find the direction preferences
	 * 
	 * @param gf,
	 *            the gameFacade
	 * @return array of doubles of direction preferences
	 */
	public abstract double[] getDirectionPreferences(GameFacade gf);
}
