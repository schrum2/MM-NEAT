/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class VariableDirectionKStepCountBlock extends VariableDirectionBlock {

    public final int stepCount;
    public final boolean max;

    public VariableDirectionKStepCountBlock(int dir) {
        this(dir, true);
    }
    
    public VariableDirectionKStepCountBlock(int dir, boolean max) {
        this(dir, Parameters.parameters.integerParameter("smallStepSimDepth"), max);
    }

    public VariableDirectionKStepCountBlock(int dir, int k, boolean max) {
        super(dir);
        this.stepCount = k;
        this.max = max;
    }

    public double wallValue() {
        return 0;
    }

    @Override
    public double getValue(GameFacade gf) {
        final int currentLocation = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(currentLocation);
        final int next = neighbors[dir];
        assert next != -1 : "The next direction is not viable!";
        ArrayList<Integer> visited = new ArrayList<Integer>();
        visited.add(currentLocation);
        double count = countMembersAlongPath(gf, visited, getCountTargets(gf), currentLocation, next, stepCount, max);
        double maxCount = maxCount(gf);
        return maxCount == 0 ? 0 : count / maxCount; // Normalized
    }

    public abstract int maxCount(GameFacade gf);

    /**
     * Recursive method for counting the number of targets along paths a certain
     * number of steps from the starting point in a given direction.
     *
     * @param gf the game state
     * @param visited locations that have already been visited along the given
     * path. Note that the path may loop on to itself, and then continue on into
     * new areas.
     * @param targets objects that should be counted if they occur along the
     * path
     * @param sourceLocation where pacman came from. Cannot go back to this
     * location.
     * @param currentLocation where pacman currently is
     * @param remainingSteps How much further to continue along the path.
     * @return maximum number of targets found along all available paths
     */
    public static int countMembersAlongPath(GameFacade gf, ArrayList<Integer> visited, int[] targets, int sourceLocation, int currentLocation, int remainingSteps, boolean max) {
        assert visited.contains(sourceLocation) : "Must have visited source location";
        if (remainingSteps == 0) {
            return 0;
        } else {
            final int[] neighbors = gf.neighbors(currentLocation);
            boolean foundReverse = false;
            for (int i = 0; i < neighbors.length && !foundReverse; i++) {
                if (neighbors[i] == sourceLocation) {
                    neighbors[i] = -1; // Don't go backwards
                    foundReverse = true;
                }
            }
            assert foundReverse : "Should always find the reverse direction";
            int aggregateCount = max ? -1 : Integer.MAX_VALUE;
            visited.add(currentLocation);
            for (int i = 0; i < neighbors.length; i++) {
                if (neighbors[i] != -1) {
                    int count = countMembersAlongPath(gf, visited, targets, currentLocation, neighbors[i], remainingSteps - 1, max);
                    aggregateCount = max ? Math.max(aggregateCount, count) : Math.min(aggregateCount, count);
                }
            }
            assert visited.get(visited.size() - 1) == currentLocation : "Should be popping the currentLocation off end of list";
            visited.remove(visited.size() - 1); // Should remove currentLocation
            boolean alreadyCounted = visited.contains(currentLocation);
            int targetsToCount = alreadyCounted ? 0 : ArrayUtil.countOccurrences(currentLocation, targets);
            return aggregateCount + targetsToCount;
        }
    }

    public abstract int[] getCountTargets(GameFacade gf);

    @Override
    public String getLabel() {
        return (max ? "Max" : "Min") + " Num " + getType() + " " + stepCount + " Steps Ahead";
    }

    public abstract String getType();
}
