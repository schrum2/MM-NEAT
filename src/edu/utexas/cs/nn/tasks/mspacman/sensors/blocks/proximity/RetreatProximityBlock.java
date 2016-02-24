package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.MiscUtil;

/**
 *
 * @author Jacob Schrum
 */
public abstract class RetreatProximityBlock extends MsPacManSensorBlock {

    private final int absence;

    public RetreatProximityBlock() {
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int currentDir) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? currentDir : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        int[] targets = getTargets(gf);
        double[] dirActivations = new double[GameFacade.NUM_DIRS];
        for (int i = 0; i < targets.length; i++) {
            int escapeDir = gf.getNextPacManDirAwayFromTarget(targets[i]);
            double distance = gf.getShortestPathDistance(current, targets[i]);
            dirActivations[escapeDir] = Math.max(dirActivations[escapeDir], MiscUtil.scaleAndInvert(distance, GameFacade.MAX_DISTANCE));
        }

        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall || targets.length == 0) {
                inputs[in++] = absence;
            } else {
                inputs[in++] = dirActivations[dir];
            }

        }
        return in;
    }

    public abstract int[] getTargets(GameFacade gf);

    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = "Retreat Proximity " + targetType() + " " + first;
        labels[in++] = "Retreat Proximity " + targetType() + " Right";
        labels[in++] = "Retreat Proximity " + targetType() + " " + last;
        labels[in++] = "Retreat Proximity " + targetType() + " Left";
        return in;
    }

    public abstract String targetType();

    public int numberAdded() {
        return GameFacade.NUM_DIRS;
    }
}
