/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.ghosts;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public abstract class SharedNNDirectionalGhostsController extends SharedNNGhostsController {

    public SharedNNDirectionalGhostsController(Network n){
        super(n);
    }
    
    @Override
    public int getDirection(GameFacade gf, Constants.GHOST ghost) {
        double[] dirPreferences = getDirectionPreferences(gf, GameFacade.ghostToIndex(ghost));
        int direction = directionFromPreferences(dirPreferences);
        return direction;
    }

    public int directionFromPreferences(double[] dirPreferences) {
        return CommonConstants.probabilisticSelection ? StatisticsUtilities.probabilistic(dirPreferences) : (CommonConstants.softmaxSelection ? StatisticsUtilities.softmax(dirPreferences, CommonConstants.softmaxTemperature) : StatisticsUtilities.argmax(dirPreferences));
    }

    public abstract double[] getDirectionPreferences(GameFacade gf, int ghostIndex);
}
