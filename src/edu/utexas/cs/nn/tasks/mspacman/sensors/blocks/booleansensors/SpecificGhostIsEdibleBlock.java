/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public class SpecificGhostIsEdibleBlock extends MsPacManSensorBlock {

    private final int ghostIndex;

    public SpecificGhostIsEdibleBlock(int ghostIndex) {
        this.ghostIndex = ghostIndex;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        inputs[in++] = gf.isGhostEdible(ghostIndex) ? 1 : 0;
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "Ghost " + ghostIndex + " Is Edible";
        return in;
    }

    public int numberAdded() {
        return 1;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            SpecificGhostIsEdibleBlock other = (SpecificGhostIsEdibleBlock) o;
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
