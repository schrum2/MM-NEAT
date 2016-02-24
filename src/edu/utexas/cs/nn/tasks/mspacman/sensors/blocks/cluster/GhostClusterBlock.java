package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.cluster;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.ghosts.GhostComparator;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.awt.Color;
import java.util.Arrays;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class GhostClusterBlock extends MsPacManSensorBlock {

    private final boolean threats;
    private final boolean[] mask;

    public GhostClusterBlock(boolean threats) {
        this(new boolean[]{true, true, true, true}, threats);
    }

    public GhostClusterBlock(boolean[] mask, boolean threats) {
        this.threats = threats;
        this.mask = mask;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        //int[] locs = threats ? gf.getThreatGhostLocations() : gf.getEdibleGhostLocations();
        int numGhostsOfInterest = (threats ? gf.getThreatGhostLocations() : gf.getEdibleGhostLocations()).length;
        int clusterNumber = ArrayUtil.countOccurrences(true, mask);
        if (numGhostsOfInterest < clusterNumber) {
            inputs[in++] = 0;
        } else {
            Integer[] ghostIndices = new Integer[CommonConstants.numActiveGhosts];
            for (int i = 0; i < ghostIndices.length; i++) {
                ghostIndices[i] = i;
            }
            Arrays.sort(ghostIndices, new GhostComparator(gf, !threats, false));

            double maxDistance = 0;
            for (int i = 0; i < clusterNumber; i++) {
                for (int j = 0; j < clusterNumber; j++) {
                    if (i != j && mask[i] && mask[j]) {
                        int loc1 = gf.getGhostCurrentNodeIndex(ghostIndices[i]);
                        int loc2 = gf.getGhostCurrentNodeIndex(ghostIndices[j]);
                        if (CommonConstants.watch && clusterNumber == 3) {
                            gf.addPoints(Color.CYAN, gf.getShortestPath(loc1, loc2));
                        }
                        maxDistance = Math.max(maxDistance, gf.getShortestPathDistance(loc1, loc2));
                    }
                }
            }
            maxDistance = Math.min(maxDistance, GameFacade.MAX_DISTANCE);
            inputs[in++] = ((GameFacade.MAX_DISTANCE - maxDistance) / GameFacade.MAX_DISTANCE);
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        String type = threats ? "Threat" : "Edible";
        labels[in++] = ArrayUtil.countOccurrences(true, mask) + " " + type + " Ghost Clustering";
        return in;
    }

    public int numberAdded() {
        return 1;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o instanceof GhostClusterBlock) {
            GhostClusterBlock other = (GhostClusterBlock) o;
            return other.threats == this.threats && Arrays.equals(this.mask, other.mask);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.threats ? 1 : 0);
        hash = 79 * hash + super.hashCode();
        hash = 79 * hash + Arrays.hashCode(mask);
        return hash;
    }
}
