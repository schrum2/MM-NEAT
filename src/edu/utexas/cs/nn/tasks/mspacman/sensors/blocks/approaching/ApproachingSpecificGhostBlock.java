/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.approaching;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public class ApproachingSpecificGhostBlock extends ApproachingLocationBlock {

    private int targetGhostIndex;
    private boolean edible;

    public ApproachingSpecificGhostBlock(int ghostIndex, boolean edible) {
        this.targetGhostIndex = ghostIndex;
        this.edible = edible;
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        if (gf.isGhostEdible(targetGhostIndex) == edible) {
            // Ghost matches desired state, so return location
            int node = gf.getGhostCurrentNodeIndex(targetGhostIndex);
            return new int[]{node};
        }
        return new int[0];
    }

    @Override
    public String typeOfTarget() {
        return "Ghost " + targetGhostIndex;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o instanceof ApproachingSpecificGhostBlock) {
            ApproachingSpecificGhostBlock other = (ApproachingSpecificGhostBlock) o;
            return other.edible == this.edible && other.targetGhostIndex == this.targetGhostIndex;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.targetGhostIndex;
        hash = 89 * hash + (this.edible ? 1 : 0);
        hash = 89 * hash + super.hashCode();
        return hash;
    }
}
