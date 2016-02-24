package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.cluster;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public class GhostTreeClusterBlock extends MsPacManSensorBlock {

    private final boolean threats;
    private final boolean allMustBePresent;

    public GhostTreeClusterBlock(boolean threats, boolean allMustBePresent) {
        this.threats = threats;
        this.allMustBePresent = allMustBePresent;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int numGhostsOfInterest = (threats ? gf.getThreatGhostLocations() : gf.getEdibleGhostLocations()).length;
        if (allMustBePresent && numGhostsOfInterest < CommonConstants.numActiveGhosts) {
            inputs[in++] = 0;
        } else {
            inputs[in++] = (threats ? gf.threatGhostClusterTreeSize() : gf.edibleGhostClusterTreeSize()) / (gf.getNumMazeNodes() * 1.0);
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        String type = threats ? "Threat" : "Edible";
        labels[in++] = type + " Ghost " + (allMustBePresent ? "Full" : "Partial") + " Tree Clustering";
        return in;
    }

    public int numberAdded() {
        return 1;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o instanceof GhostTreeClusterBlock) {
            GhostTreeClusterBlock other = (GhostTreeClusterBlock) o;
            return other.threats == this.threats && other.allMustBePresent == this.allMustBePresent;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.threats ? 1 : 0);
        hash = 79 * hash + super.hashCode();
        hash = 79 * hash + (this.allMustBePresent ? 1 : 0);
        return hash;
    }
}
