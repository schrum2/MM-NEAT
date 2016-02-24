/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.paths;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 *
 * @author Jacob Schrum
 */
public abstract class TargetsOnPathBlock extends MsPacManSensorBlock {

    private final int absence;

    public TargetsOnPathBlock() {
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? lastDirection : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        int pathTargetNode = getPathTarget(gf);
        int[] targetItems = getTargets(gf);

        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall || pathTargetNode == -1) {
                inputs[in++] = this.absence;
            } else if (targetItems.length == 0) {
                inputs[in++] = 0;
            } else {
                int[] directionalPath = gf.getDirectionalPath(current, pathTargetNode, dir);
                int[] intersection = ArrayUtil.intersection(directionalPath, targetItems);
                inputs[in++] = (intersection.length * 1.0) / targetItems.length;
            }
        }
        return in;
    }

    public abstract int getPathTarget(GameFacade gf);

    public abstract String pathTargetLabel();

    public abstract int[] getTargets(GameFacade gf);

    public abstract String targetTypeLabel();

    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = "Num " + targetTypeLabel() + " on " + first + " Path to " + pathTargetLabel();
        labels[in++] = "Num " + targetTypeLabel() + " on Right Path to " + pathTargetLabel();
        labels[in++] = "Num " + targetTypeLabel() + " on " + last + " Path to " + pathTargetLabel();
        labels[in++] = "Num " + targetTypeLabel() + " on Left Path to " + pathTargetLabel();

        return in;
    }

    public int numberAdded() {
        return GameFacade.NUM_DIRS;
    }
}
