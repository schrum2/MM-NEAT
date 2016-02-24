/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class GhostEatingRewardBlock extends MsPacManSensorBlock {

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        inputs[in++] = gf.anyIsEdible() ? (gf.getGhostCurrentEdibleScore() / Constants.GHOST_EAT_SCORE) / 8.0 : 0;
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "Next Eaten Ghost Value";
        return in;
    }

    public int numberAdded() {
        return 1;
    }
}
