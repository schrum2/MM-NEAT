/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.opposite;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.Statistic;

/**
 * Given another variable direction sensor, check that sensor value in all
 * possible directions except the one being focused on. Apply some statistic to
 * the sensor values in the other directions, and make that result a sensor
 * value. This way, the network applied to one direction has a sense of what is
 * available in the other directions.
 *
 * @author Jacob Schrum
 */
public class VariableDirectionOtherDirectionsStatisticBlock extends VariableDirectionBlock {

    private final VariableDirectionBlock sensor;
    private final Statistic stat;

    public VariableDirectionOtherDirectionsStatisticBlock(VariableDirectionBlock sensor, Statistic stat) {
        this(sensor, stat, -1);
    }

    public VariableDirectionOtherDirectionsStatisticBlock(VariableDirectionBlock sensor, Statistic stat, int dir) {
        super(dir);
        this.sensor = sensor;
        this.stat = stat;
    }

    @Override
    public double wallValue() {
        return sensor.wallValue();
    }

    @Override
    public double getValue(GameFacade gf) {
        int[] neighbors = gf.neighbors(gf.getPacmanCurrentNodeIndex());
        int wallCount = ArrayUtil.countOccurrences(-1, neighbors);
        double[] xs = new double[(neighbors.length - wallCount) - 1];
        int xIndex = 0;
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != -1 && i != dir) {
                sensor.setDirection(i);
                xs[xIndex++] = sensor.getValue(gf);
            }
        }
        return stat.stat(xs);
    }

    @Override
    public String getLabel() {
        return stat.getClass().getSimpleName() + " " + sensor.getLabel() + " In Other Dirs";
    }
}
