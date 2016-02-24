package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public abstract class DirectionalProximityBlock extends MsPacManSensorBlock {

    private final int absence;

    public DirectionalProximityBlock() {
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int currentDir) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? currentDir : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        int[] targets = getTargets(gf);

        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall || targets.length == 0) {
                inputs[in++] = absence;
            } else {
                Pair<Integer, int[]> pair = gf.getTargetInDir(current, targets, dir);
                int[] path = pair.t2;
                double distance = path.length;
                // Proximity
                inputs[in++] = 1.0 - (Math.min(distance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE);
                //inputs[in++] = MiscUtil.scaleAndInvert(distance, GameFacade.MAX_DISTANCE);
                //inputs[in++] = ActivationFunctions.sigmoid(MiscUtil.scaleAndInvert(distance, GameFacade.MAX_DISTANCE));
            }

        }
        return in;
    }

    public abstract int[] getTargets(GameFacade gf);

    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = "Proximity " + targetType() + " " + first;
        labels[in++] = "Proximity " + targetType() + " Right";
        labels[in++] = "Proximity " + targetType() + " " + last;
        labels[in++] = "Proximity " + targetType() + " Left";
        return in;
    }

    public abstract String targetType();

    public int numberAdded() {
        return GameFacade.NUM_DIRS;
    }
}
