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
public abstract class GhostVariableDirectionBlock extends GhostSensorBlock {

    @Override
    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int ghostIndex) {
        final int current = gf.getGhostCurrentNodeIndex(ghostIndex);
        final int[] neighbors = gf.neighbors(current);
        assert direction >= 0 && direction <= 3 : "Direction must be in range 0-3: " + direction + " is not in this range!";
        inputs[in++] = neighbors[direction] == -1 ? wallValue() : getValue(gf,ghostIndex);
        assert !Double.isNaN(inputs[in - 1]) : "Value is NaN: " + this.getLabel() + ":" + this.getClass().getSimpleName();
        return in;
    }

    @Override
    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = getLabel() + " in dir " + direction;
        return in;
    }

    @Override
    public int numberAdded() {
        return 1;
    }

    private double wallValue() {
        return -1;
    }

    public abstract String getLabel();

    public abstract double getValue(GameFacade gf, int ghostIndex);

}
