package edu.utexas.cs.nn.breve2D.agent;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.CartesianGeometricUtilities;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 *
 * @author Jacob Schrum
 */
public class Agent implements ILocated2D {

    private Tuple2D position;
    private double heading;
    private int health;
    private int id = -1;

    public Agent(Tuple2D pos, double heading) {
        this.position = pos;
        this.heading = heading;
        resetHealth();
    }

    public final void resetHealth() {
        this.health = Parameters.parameters.integerParameter("breve2DAgentHealth");
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        health = Math.max(health, 0); // non-negative
    }

    public boolean isDead() {
        return health == 0;
    }

    public double getHeading() {
        return heading;
    }

    public double getOppositeHeading() {
        return CartesianGeometricUtilities.restrictRadians(heading + Math.PI);
    }

    public Tuple2D getPosition() {
        return position;
    }

    public double distance(ILocated2D other) {
        if (position == null || other == null || other.getPosition() == null) {
            return Double.MAX_VALUE;
        }
        return position.distance(other);
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    @Override
    public String toString() {
        return position + ":" + heading;
    }

    public void setPosition(Tuple2D pos) {
        position = pos;
    }

    public void setHeading(double h) {
        heading = CartesianGeometricUtilities.restrictRadians(h);
    }

    public void turn(double t) {
        setHeading(heading + t);
    }

    public void move(Tuple2D delta) {
        setPosition(position.add(delta));
    }

    public void setIdentifier(int id) {
        this.id = id;
    }

    public int getIdentifier() {
        return this.id;
    }

    public boolean isFacing(ILocated2D pos, double allowance) {
        return CartesianGeometricUtilities.sourceHeadingTowardsTarget(this.getHeading(), this.getPosition(), pos, allowance);
    }
}
