/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public abstract class GhostSensorBlock {
    // Give all sensors a chance to be direction oriented
    public int direction = -1;

    /**
     * Adds sensor values starting at index in in array inputs
     * using data from gf with respect to the ghostIndex ghost.
     * @param inputs
     * @param in
     * @param gf
     * @param ghostIndex
     * @return Counter to position of last filled input in inputs
     */
    public abstract int incorporateSensors(double[] inputs, int in, GameFacade gf, int ghostIndex);
    
    public abstract int incorporateLabels(String[] labels, int in);

    public abstract int numberAdded();

    public void reset() {
    }

    public void setDirection(int dir) {
        this.direction = dir;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof GhostSensorBlock) {
            equals((GhostSensorBlock) o);
        }
        return false;
    }

    public boolean equals(GhostSensorBlock o) {
        return this.getClass().getName().equals(o.getClass().getName());
    }

    @Override
    public int hashCode() {
        return this.getClass().getName().hashCode();
    }
}
