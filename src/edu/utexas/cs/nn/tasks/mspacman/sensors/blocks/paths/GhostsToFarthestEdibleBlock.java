/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.paths;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public class GhostsToFarthestEdibleBlock extends TargetsOnPathBlock {

    private final boolean edible;

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            GhostsToFarthestEdibleBlock other = (GhostsToFarthestEdibleBlock) o;
            return super.equals(other) && this.edible == other.edible;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.edible ? 1 : 0);
        hash = 67 * hash + super.hashCode();
        return hash;
    }

    public GhostsToFarthestEdibleBlock(boolean edible) {
        super();
        this.edible = edible;
    }

    @Override
    public int getPathTarget(GameFacade gf) {
        int[] ghosts = gf.getEdibleGhostLocations();
        if (ghosts.length == 0) {
            return -1;
        }
        final int current = gf.getPacmanCurrentNodeIndex();
        int farthest = gf.getFarthestNodeIndexFromNodeIndex(current, ghosts);
        return farthest;
    }

    @Override
    public String pathTargetLabel() {
        return "Farthest Edible";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return edible ? gf.getEdibleGhostLocations() : gf.getThreatGhostLocations();
    }

    @Override
    public String targetTypeLabel() {
        return edible ? "Edible" : "Threat";
    }
}
