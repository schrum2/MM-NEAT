package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public abstract class NearestFarthestIndexBlock extends MsPacManSensorBlock {

    private final int wallReading;
    private final boolean nearest;
    private final int unavailable;

    public NearestFarthestIndexBlock() {
        this(true);
    }

    public NearestFarthestIndexBlock(boolean nearest) {
        this(nearest, Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0);
    }

    public NearestFarthestIndexBlock(int unavailable) {
        this(true, unavailable);
    }

    public NearestFarthestIndexBlock(boolean nearest, int unavailable) {
        this.nearest = nearest;
        this.wallReading = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
        this.unavailable = unavailable;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            NearestFarthestIndexBlock other = (NearestFarthestIndexBlock) o;
            return this.unavailable == other.unavailable && this.nearest == other.nearest;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.nearest ? 1 : 0);
        hash = 37 * hash + this.unavailable;
        hash = 37 * hash + super.hashCode();
        return hash;
    }

    public int incorporateSensors(final double[] inputs, int in, final GameFacade gf, final int currentDir) {
        return incorporateSensors(inputs, in, gf, currentDir, getTargets(gf));
    }

    public abstract int[] getTargets(GameFacade gf);

    public int incorporateSensors(final double[] inputs, int in, final GameFacade gf, final int currentDir, int[] targets) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? currentDir : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        int targetDir = -1;
        if (targets.length > 0) {
            int target = nearest ? gf.getClosestNodeIndexFromNodeIndex(current, targets) : gf.getFarthestNodeIndexFromNodeIndex(current, targets);
            targetDir = gf.getNextPacManDirTowardsTarget(target);
//            if (watch) {
//                gf.addPoints(CombinatoricUtilities.colorFromInt(typeOfTarget().length()), gf.getShortestPath(current, target));
//            }
        }
        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall) {
                inputs[in++] = this.wallReading;
            } else if (targets.length == 0) {
                inputs[in++] = unavailable;
            } else {
                inputs[in++] = (targetDir == dir) ? 1 : 0;
            }
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = "Closest " + typeOfTarget() + " " + first + "?";
        labels[in++] = "Closest " + typeOfTarget() + " Right?";
        labels[in++] = "Closest " + typeOfTarget() + " " + last + "?";
        labels[in++] = "Closest " + typeOfTarget() + " Left?";

        return in;
    }

    public abstract String typeOfTarget();

    public int numberAdded() {
        return GameFacade.NUM_DIRS;
    }
}
