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
public class SpecificGhostLairTimeBlock extends MsPacManSensorBlock {

    private final int ghostIndex;

    public SpecificGhostLairTimeBlock(int ghostIndex) {
        this.ghostIndex = ghostIndex;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        inputs[in++] = Math.min(gf.getGhostLairTime(ghostIndex), Constants.COMMON_LAIR_TIME) / (Constants.COMMON_LAIR_TIME * 1.0);
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "Ghost " + ghostIndex + " Lair Time";
        return in;
    }

    public int numberAdded() {
        return 1;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            SpecificGhostLairTimeBlock other = (SpecificGhostLairTimeBlock) o;
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
