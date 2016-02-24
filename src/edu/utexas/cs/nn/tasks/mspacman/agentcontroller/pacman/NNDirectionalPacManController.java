package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
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

    public int getDirection(GameFacade gs) {
        double[] dirPreferences = getDirectionPreferences(gs);
        System.arraycopy(dirPreferences, 0, previousPreferences, 0, GameFacade.NUM_DIRS);
//        if (watch && (nn instanceof TWEANN) && ((TWEANN) nn).numModes() > 1) {
//            // Need to have a better indicator of current mode
//            gs.addPoints(CombinatoricUtilities.colorFromInt(((TWEANN) nn).chosenMode), gs.neighbors(gs.getPacmanCurrentNodeIndex()));
//        }
        int direction = directionFromPreferences(dirPreferences);
        return direction;
    }

    public int directionFromPreferences(double[] dirPreferences) {
        if (externalPreferenceNeurons) {
            dirPreferences = ArrayUtil.portion(dirPreferences, 0, dirPreferences.length - 2);
        }
        return CommonConstants.probabilisticSelection ? StatisticsUtilities.probabilistic(dirPreferences) : (CommonConstants.softmaxSelection ? StatisticsUtilities.softmax(dirPreferences, CommonConstants.softmaxTemperature) : StatisticsUtilities.argmax(dirPreferences));
    }

    public abstract double[] getDirectionPreferences(GameFacade gf);
}
