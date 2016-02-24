/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.lair;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public class SpecificGhostLairDistanceBlock extends LairDistanceBlock {

    private final int ghostIndex;

    public SpecificGhostLairDistanceBlock(int ghostIndex) {
        this.ghostIndex = ghostIndex;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.ghostIndex;
        hash = 37 * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o instanceof SpecificGhostLairDistanceBlock) {
            SpecificGhostLairDistanceBlock other = (SpecificGhostLairDistanceBlock) o;
            return this.ghostIndex == other.ghostIndex;
        }
        return false;
    }

    @Override
    public String sourceLabel() {
        return "Ghost " + ghostIndex;
    }

    @Override
    public int getTarget(GameFacade gf) {
        return gf.getGhostCurrentNodeIndex(ghostIndex);
    }
}
