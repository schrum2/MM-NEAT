/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.lookahead;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.counts.VariableDirectionKStepCountBlock;
import java.util.ArrayList;

/**
 *
 * @author Jacob
 */
public abstract class DirectionalStaticLookAheadBlock extends MsPacManSensorBlock {

    private final int stepCount;
    private final boolean max;

    public DirectionalStaticLookAheadBlock() {
        this(true); // Use max count by default
    }
    
    public DirectionalStaticLookAheadBlock(boolean max) {
        this.max = max;
        this.stepCount = Parameters.parameters.integerParameter("smallStepSimDepth");
    }

    @Override
    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? lastDirection : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        int[] targets = getTargets(gf);

        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall || targets.length == 0) {
                inputs[in++] = 0;
            } else {
                inputs[in++] = getCountValue(gf, dir, targets);
            }

        }
        return in;
    }

    public double getCountValue(GameFacade gf, int dir, int[] targets) {
        final int currentLocation = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(currentLocation);
        final int next = neighbors[dir];
        assert next != -1 : "The next direction is not viable!";
        ArrayList<Integer> visited = new ArrayList<Integer>();
        visited.add(currentLocation);
        double count = VariableDirectionKStepCountBlock.countMembersAlongPath(gf, visited, targets, currentLocation, next, stepCount, max);
        double maxCount = maxCount(gf);
        return maxCount == 0 ? 0 : count / maxCount; // Normalized
    }

    @Override
    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = (max ? "Max" : "Min") + " Count " + targetType() + " " + stepCount + " Steps " + first;
        labels[in++] = (max ? "Max" : "Min") + " Count " + targetType() + " " + stepCount + " Steps Right";
        labels[in++] = (max ? "Max" : "Min") + " Count " + targetType() + " " + stepCount + " Steps " + last;
        labels[in++] = (max ? "Max" : "Min") + " Count " + targetType() + " " + stepCount + " Steps Left";
        return in;
    }

    @Override
    public int numberAdded() {
        return GameFacade.NUM_DIRS;
    }

    public abstract String targetType();

    public abstract int[] getTargets(GameFacade gf);

    public abstract double maxCount(GameFacade gf);
}
