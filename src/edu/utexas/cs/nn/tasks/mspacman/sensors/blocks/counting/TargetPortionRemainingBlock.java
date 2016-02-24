/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public abstract class TargetPortionRemainingBlock extends MsPacManSensorBlock {

    private final boolean portion;
    private final boolean inverse;

    public TargetPortionRemainingBlock(boolean portion, boolean inverse) {
        super();
        this.portion = portion;
        this.inverse = inverse;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int max = getTargetMax(gf);
        int current = getTargetCurrent(gf);
        if (portion) {
            inputs[in++] = (current / (max * 1.0));
        }
        if (inverse) {
            inputs[in++] = ((max - current) / (max * 1.0));
        }
        return in;
    }

    public abstract int getTargetMax(GameFacade gf);

    public abstract int getTargetCurrent(GameFacade gf);

    public int incorporateLabels(String[] labels, int in) {
        if (portion) {
            labels[in++] = "Portion " + getTargetType() + " Remaining";
        }
        if (inverse) {
            labels[in++] = "Inverse Portion " + getTargetType() + " Remaining";
        }
        return in;
    }

    public abstract String getTargetType();

    public int numberAdded() {
        return (portion ? 1 : 0) + (inverse ? 1 : 0);
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            TargetPortionRemainingBlock other = (TargetPortionRemainingBlock) o;
            return other.portion == this.portion && other.inverse == this.inverse;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.portion ? 1 : 0);
        hash = 19 * hash + (this.inverse ? 1 : 0);
        hash = 19 * hash + super.hashCode();
        return hash;
    }
}
