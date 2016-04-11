package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 * An action in the breve world is a turn amount and
 * a forward/backward force amount. Negative turns correspond
 * to turning left, and positive turns to turning right.
 * The Tuple2D is used to store the data, but this class is
 * a facade that uses appropriate names to access the data
 * in the tuple.
 *
 * @author Jacob Schrum
 */
public class Breve2DAction extends Tuple2D {

    /**
	 * Auto-generated serial ID
	 */
	private static final long serialVersionUID = -7126983774703243455L;

	/**
	 * Create breve action from tuple.
	 * Interpret x as turn amount, and y as force amount.
	 * @param tuple
	 */
	public Breve2DAction(Tuple2D tuple) {
        this(tuple.x, tuple.y);
    }

    public Breve2DAction(double turn, double force) {
        super(turn, force); // Use Tuple2D constructor
    }

    /**
     * Turn amount is in the x coordinate
     * @return turn amount: negative=left, positive=right
     */
    public double getTurn() {
        return getX();
    }

    /**
     * Force amount is in the y coordinate
     * @return force amount: negative=backward, positive=forward
     */
    public double getForce() {
        return getY();
    }

    /**
     * Update force to specified value.
     * @param force New force amount.
     */
    public void setForce(double force) {
        this.y = force;
    }
}
