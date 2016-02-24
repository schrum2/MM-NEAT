/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.approaching;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public abstract class ApproachingLocationBlock extends MsPacManSensorBlock {

    private final int wallReading;

    public ApproachingLocationBlock() {
        this.wallReading = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int currentDir) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? currentDir : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        int[] targets = getTargets(gf);
        int targetDir = -1;
        if (targets.length > 0) {
            int target = gf.getClosestNodeIndexFromNodeIndex(current, targets);
            targetDir = gf.getNextPacManDirTowardsTarget(target);
        }
        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall) {
                inputs[in++] = this.wallReading;
            } else {
                inputs[in++] = (targetDir == dir && currentDir == dir) ? 1 : 0;
            }
        }
        return in;

    }

    public abstract int[] getTargets(GameFacade gf);

    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = "Approaching " + typeOfTarget() + " " + first + "?";
        labels[in++] = "Approaching " + typeOfTarget() + " Right?";
        labels[in++] = "Approaching " + typeOfTarget() + " " + last + "?";
        labels[in++] = "Approaching " + typeOfTarget() + " Left?";

        return in;
    }

    public int numberAdded() {
        return GameFacade.NUM_DIRS;
    }

    public abstract String typeOfTarget();
}
