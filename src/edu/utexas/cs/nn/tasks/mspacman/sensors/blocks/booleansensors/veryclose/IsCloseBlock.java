/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.veryclose;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BooleanSensorBlock;

/**
 * Sensor is 1 if pacman is within distance of any member of a set of targets
 *
 * @author Jacob Schrum
 */
public abstract class IsCloseBlock extends BooleanSensorBlock {

    private final double distance;

    public boolean equals(MsPacManSensorBlock o) {
        if (o instanceof IsCloseBlock) {
            IsCloseBlock other = (IsCloseBlock) o;
            return other.distance == this.distance;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.distance) ^ (Double.doubleToLongBits(this.distance) >>> 32));
        hash = 97 * hash + super.hashCode();
        return hash;
    }

    public IsCloseBlock(double distance) {
        super();
        this.distance = distance;
    }

    @Override
    public String senseLabel() {
        return "Within " + distance + " of " + getType() + "?";
    }

    @Override
    public boolean predicate(GameFacade gf, int lastDirection) {
        int[] targets = getTargets(gf);
        final int current = gf.getPacmanCurrentNodeIndex();
        for (int i = 0; i < targets.length; i++) {
            int n = targets[i];
            if (n != -1 && gf.getNumNeighbours(n) > 0 && gf.getShortestPathDistance(current, n) < distance) {
                return true;
            }
        }
        return false;
    }

    public abstract int[] getTargets(GameFacade gf);

    public abstract String getType();
}
