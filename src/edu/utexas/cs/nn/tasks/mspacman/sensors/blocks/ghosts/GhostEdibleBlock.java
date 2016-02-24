/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class GhostEdibleBlock extends GhostSensorBlock {

    @Override
    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int ghostIndex) {
        inputs[in++] = gf.isGhostEdible(ghostIndex) ? 1 : 0;
        return in;
    }

    @Override
    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "Is Ghost Edible";
        return in;
    }

    @Override
    public int numberAdded() {
        return 1;
    }

}
