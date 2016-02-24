/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class SpecificGhostEdibleTimeBlock extends MsPacManSensorBlock {

    private final int ghostIndex;

    public SpecificGhostEdibleTimeBlock(int ghostIndex) {
        this.ghostIndex = ghostIndex;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        inputs[in++] = gf.getGhostEdibleTime(ghostIndex) / (Constants.EDIBLE_TIME * 1.0);
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "Ghost " + ghostIndex + " Edible Time";
        return in;
    }

    public int numberAdded() {
        return 1;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            SpecificGhostEdibleTimeBlock other = (SpecificGhostEdibleTimeBlock) o;
            return this.ghostIndex == other.ghostIndex;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.ghostIndex;
        hash = 37 * hash + super.hashCode();
        return hash;
    }
}
