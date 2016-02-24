package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.lair;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public abstract class LairDistanceBlock extends MsPacManSensorBlock {

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int lairExit = gf.getGhostInitialNodeIndex();
        int target = getTarget(gf);
        // 0 distance means target is in the lair
        double distance = target == -1 || gf.getNumNeighbours(target) == 0 ? 0 : gf.getShortestPathDistance(lairExit, target);
        inputs[in++] = Math.min(distance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = sourceLabel() + " Distance From Lair";
        return in;
    }

    public int numberAdded() {
        return 1;
    }

    public abstract String sourceLabel();

    public abstract int getTarget(GameFacade gf);
}
