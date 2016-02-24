package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.Statistic;

/**
 * Distance to nearest instance of some type of target
 *
 * @author Jacob Schrum
 */
public abstract class StatDistanceBlock extends MsPacManSensorBlock {

    private final Statistic stat;
    private final double zeroResult;

    public StatDistanceBlock(Statistic s, double zeroResult) {
        this.stat = s;
        this.zeroResult = zeroResult;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o instanceof StatDistanceBlock) {
            StatDistanceBlock other = (StatDistanceBlock) o;
            return this.zeroResult == other.zeroResult && this.stat.getClass().equals(other.stat.getClass());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.stat != null ? this.stat.getClass().getName().hashCode() : 0);
        hash = 89 * hash + new Double(zeroResult).hashCode();
        return hash;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int current = gf.getPacmanCurrentNodeIndex();
        int[] targets = getTargets(gf);
        double distance;
        if (current == -1
                || targets == null
                || targets.length == 0
                || ArrayUtil.countOccurrences(-1, targets) == targets.length) {
            distance = zeroResult;
        } else {
            double[] distances = new double[targets.length];
            for (int i = 0; i < targets.length; i++) {
                if (targets[i] == -1 || gf.getNumNeighbours(targets[i]) == 0) { // In lair
                    distances[i] = zeroResult;
                } else {
                    distances[i] = gf.getShortestPathDistance(current, targets[i]);
                }
                distances[i] = Math.min(distances[i], GameFacade.MAX_DISTANCE);
            }
            distance = stat.stat(distances);
        }
        inputs[in++] = distance / GameFacade.MAX_DISTANCE;
        return in;
    }

    public abstract int[] getTargets(GameFacade gf);

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = stat.getClass().getSimpleName() + " " + getType() + " Distance";
        return in;
    }

    public abstract String getType();

    public int numberAdded() {
        return 1;
    }
}
