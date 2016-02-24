/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.data.NodeCollection;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.util.Arrays;
import java.util.Comparator;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class NearestEscapeNodeThreatDistanceDifferencesBlock extends MsPacManSensorBlock {

    private final NodeCollection escapeNodes;
    private final int howMany;

    public NearestEscapeNodeThreatDistanceDifferencesBlock(NodeCollection en, int howMany) {
        this.escapeNodes = en;
        this.howMany = howMany;
    }

    public int incorporateSensors(final double[] inputs, int in, final GameFacade gf, final int currentDir) {
        final int current = gf.getPacmanCurrentNodeIndex();
        int[] locs = escapeNodes.getNodes();
        Pair<Integer, Double>[] locationDistancePairs = new Pair[locs.length];
        for (int i = 0; i < locs.length; i++) {
            locationDistancePairs[i] = new Pair<Integer, Double>(locs[i], gf.getShortestPathDistance(current, locs[i]));
        }
        Arrays.sort(locationDistancePairs, new Comparator<Pair<Integer, Double>>() {
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                return (int) Math.signum(o1.t2 - o2.t2);
            }
        });

        for (int j = 0; j < howMany; j++) {
            int node = locationDistancePairs[j].t1;
            int[] path = gf.getShortestPath(current, node);
            double pacManDistance = path.length;
            double closestThreatDistance = Double.MAX_VALUE;
            for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
                if (gf.isGhostThreat(i)) { // Ghost is a threat
                    double distanceToNode;
                    if (gf.pathGoesThroughThreateningGhost(path)) {
                        distanceToNode = 0; // Really bad
                    } else {
//                        int[] gPath = gf.getGhostPath(i, node);
//                        distanceToNode = gPath.length;
                        distanceToNode = gf.getGhostPathDistance(i, node);
                        assert distanceToNode == gf.getGhostPath(i, node).length : "Different methods for calculating ghost path distance did not come out the same";
                    }
                    closestThreatDistance = Math.min(closestThreatDistance, distanceToNode);
                }
            }
            double diff = closestThreatDistance - pacManDistance - (Constants.EAT_DISTANCE + 1);
            inputs[in++] = diff;
        }

        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        for (int i = 0; i < howMany; i++) {
            labels[in++] = "Diff Dis To " + i + " Nearest Escape Node";
        }

        return in;
    }

    public int numberAdded() {
        return howMany;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            NearestEscapeNodeThreatDistanceDifferencesBlock other = (NearestEscapeNodeThreatDistanceDifferencesBlock) o;
            return other.howMany == this.howMany;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.howMany;
        hash = 73 * hash + super.hashCode();
        return hash;
    }
}
