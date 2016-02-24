package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 * Could implement with StatDistanceBlock, but this implementation is more
 * efficient
 *
 * Distance to nearest instance of some type of target
 *
 * @author Jacob Schrum
 */
public abstract class NearestDistanceBlock extends MsPacManSensorBlock {

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int current = gf.getPacmanCurrentNodeIndex();
        int[] targets = getTargets(gf);
        double distance;
        if (current == -1
                || targets == null
                || targets.length == 0
                || ArrayUtil.countOccurrences(-1, targets) == targets.length) {
            distance = GameFacade.MAX_DISTANCE;
            //System.out.println(this.getClass().getSimpleName() + ":Fail:"+current + ":" + Arrays.toString(targets));
        } else {
            int node = gf.getClosestNodeIndexFromNodeIndex(current, targets);
            distance = gf.getShortestPathDistance(current, node);
            //System.out.println(this.getClass().getSimpleName() + ":distance:"+distance+" out of " + GameFacade.MAX_DISTANCE);
        }
        inputs[in++] = Math.min(distance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
        return in;
    }

    public abstract int[] getTargets(GameFacade gf);

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "Nearest " + getType() + " Distance";
        return in;
    }

    public abstract String getType();

    public int numberAdded() {
        return 1;
    }
}
