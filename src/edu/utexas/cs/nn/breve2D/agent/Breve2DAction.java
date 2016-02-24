package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 *
 * @author Jacob Schrum
 */
public class Breve2DAction extends Tuple2D {

    public Breve2DAction(Tuple2D tuple) {
        this(tuple.x, tuple.y);
    }

    public Breve2DAction(double turn, double force) {
        super(turn, force);
    }

    public double getTurn() {
        return getX();
    }

    public double getForce() {
        return getY();
    }

    public void setForce(double force) {
        this.y = force;
    }
}
