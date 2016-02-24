/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public abstract class VariableDirectionBlock extends MsPacManSensorBlock {

    public int dir = -1;

    public VariableDirectionBlock(int dir) {
        //setDirection(dir);
        this.dir = dir;
    }

    public final void setDirection(int dir) {
        assert dir >= 0 && dir <= 3 : "Valid directions are from 0 to 3. " + dir + " is not valid!";
        this.dir = dir;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);
        assert dir >= 0 && dir <= 3 : "Direction must be in range 0-3: " + dir + " is not in this range!";
        inputs[in++] = neighbors[dir] == -1 ? wallValue() : getValue(gf);
        assert !Double.isNaN(inputs[in - 1]) : "Value is NaN: " + this.getLabel() + ":" + this.getClass().getSimpleName();
        return in;
    }

    public abstract double wallValue();

    public abstract double getValue(GameFacade gf);

    public int incorporateLabels(String[] labels, int startPoint) {
        labels[startPoint++] = getLabel() + " in dir " + dir;
        return startPoint;
    }

    public abstract String getLabel();

    public int numberAdded() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && this.getClass() == o.getClass()) {
            VariableDirectionBlock other = (VariableDirectionBlock) o;
            return this.dir == other.dir;
        }
        return false;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Directional sensors should not be used in situations where hash code is needed");
//        int hash = 7;
//        hash = 37 * hash + this.dir;
//        hash = 37 * hash + this.getClass().getName().hashCode();
//        return hash;
    }
}
