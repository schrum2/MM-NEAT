/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.util.HashMap;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MsPacManSensorBlock {

    // Key is Block hash code, pair of level time and sensor values
    private static HashMap<MsPacManSensorBlock, Pair<Integer, double[]>> sensorRecord = new HashMap<MsPacManSensorBlock, Pair<Integer, double[]>>();
    
    /**
     * Takes a set of input values under construction, and from the index of
     * startPoint, starts adding sensor values. Then the index after the last
     * sensor added is returned.
     *
     * @param inputs = sensor readings under construction, modified by side
     * effects
     * @param startPoint = starting index in inputs
     * @param gf = Game Facade to get sensor readings from
     * @param lastDirection = the actual last direction pacman was moving in
     * @return position of next index in inputs to add a sensor reading
     */
    public abstract int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection);

    /**
     * Performs the same action as incorporateSensors, but stores the result statically,
     * so that it can be retrieved by other calls that want to access it on the same
     * 
     * @param inputs
     * @param in
     * @param gf
     * @param lastDirection
     * @return 
     */
    public int retrieveSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        Pair<Integer, double[]> previousCalculation = sensorRecord.get(this);
        int currentTime = gf.getCurrentLevelTime();
        int toAdd = numberAdded();
        if(previousCalculation == null || previousCalculation.t1 != currentTime) {
            int result = incorporateSensors(inputs, in, gf, lastDirection);
            double[] store = new double[toAdd];
            System.arraycopy(inputs, in, store, 0, toAdd);
            sensorRecord.put(this, new Pair<Integer, double[]>(currentTime, store));
            return result;
        } else {
            //System.out.println("Retrieved cached sensor result: " + this.getClass().getSimpleName());
            System.arraycopy(previousCalculation.t2, 0, inputs, in, toAdd);
            return in + toAdd;
        }
    }
    
    public abstract int incorporateLabels(String[] labels, int in);

    public abstract int numberAdded();

    public void reset() {
    }

    public void updateLastDir(int dir) {
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof MsPacManSensorBlock) {
            equals((MsPacManSensorBlock) o);
        }
        return false;
    }

    public boolean equals(MsPacManSensorBlock o) {
        return this.getClass().getName().equals(o.getClass().getName());
    }

    @Override
    public int hashCode() {
        return this.getClass().getName().hashCode();
    }
}
