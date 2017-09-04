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

	/**
	 * Constructor for a new Agent;
	 * Initializes the Agent in a specified location with a specific heading and with full health
	 * 
	 * @param pos Tuple2D representing the specific location where the Agent is located
	 * @param heading Double representing the heading of the Agent
	 */
	public Agent(Tuple2D pos, double heading) {
		this.position = pos;
		this.heading = heading;
		resetHealth();
	}

	/**
	 * Resets the health of the Agent of the default value
	 */
	public final void resetHealth() {
		this.health = Parameters.parameters.integerParameter("breve2DAgentHealth");
	}

	/**
	 * Returns the current health of the Agent
	 * 
	 * @return Integer value of the Agent's current health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Subtracts a specified amount from the Agent's current health;
	 * if the damage is more than the Agent's current health,
	 * the Agent's health is reduced to 0 instead.
	 * 
	 * @param damage Integer amount of damage the Agent will take
	 */
	public void takeDamage(int damage) {
		health -= damage;
		health = Math.max(health, 0); // non-negative; if drops to a negative number, will be reset to 0
	}

	/**
	 * Returns true if the Agent is dead
	 * 
	 * @return True if the Agent's health is equal to 0, else returns false
	 */
	public boolean isDead() {
		return health == 0;
	}

	/**
	 * Returns the Agent's heading
	 * 
	 * @return Double representing the Agent's heading
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * Returns the value of the opposite direction of the Agent's heading
	 * 
	 * @return Double value representing the opposite direction of the Agent's heading
	 */
	public double getOppositeHeading() {
		return CartesianGeometricUtilities.restrictRadians(heading + Math.PI);
	}

	/**
	 * Returns the Agent's current position
	 * 
	 * @return Tuple2D representing the Agent's current position
	 */
	public Tuple2D getPosition() {
		return position;
	}

	/**
	 * Returns the distance between this Agent and a specific location
	 * 
	 * @param other ILocated2D representing a specific location
	 * @return Double value representing the distance between this Agent and the specified location "other" if
	 * 		the Agent's position is not Null, "other" is not Null, and other.getPosition() is not Null;
	 * 	Else returns Double.MAX_VALUE
	 */
	public double distance(ILocated2D other) {
		if (position == null || other == null || other.getPosition() == null) {
			return Double.MAX_VALUE;
		}
		return position.distance(other);
	}

	/**
	 * Returns the Agent's current X-Position
	 * 
	 * @return Double value representing the Agent's current X-position
	 */
	public double getX() {
		return position.getX();
	}

	/**
	 * Returns the Agent's current Y-position
	 * 
	 * @return Double value representing the Agent's current Y-position
	 */
	public double getY() {
		return position.getY();
	}

	/**
	 * Returns a String containing the Agent's position and heading
	 * 
	 * @return String containing the Agent's position and heading separated by a colon
	 */
	@Override
	public String toString() {
		return position + ":" + heading;
	}

	/**
	 * Sets the Agent's position to a specified location
	 * 
	 * @param pos Tuple2D representing the location where the Agent will be
	 */
	public void setPosition(Tuple2D pos) {
		position = pos;
	}

	/**
	 * Sets the Agent's heading to a specific value
	 * 
	 * @param h Double value representing what the Agent's heading will be
	 */
	public void setHeading(double h) {
		heading = CartesianGeometricUtilities.restrictRadians(h);
	}

	/**
	 * Turns the Agent by a specified amount
	 * 
	 * @param t Double value representing how much the Agent will turn by
	 */
	public void turn(double t) {
		setHeading(heading + t);
	}

	/**
	 * Changes the Agent's location based on a specified movement delta
	 * 
	 * @param delta Tuple2D representing by how much the Agent will change its position in both directions
	 */
	public void move(Tuple2D delta) {
		setPosition(position.add(delta));
	}

	/**
	 * Sets the identifier of this Agent to a specific Integer value
	 * 
	 * @param id Integer value of the Agent's ID
	 */
	public void setIdentifier(int id) {
		this.id = id;
	}

	/**
	 * Returns the ID of this Agent
	 * 
	 * @return Integer ID of this Agent
	 */
	public int getIdentifier() {
		return this.id;
	}

	/**
	 * Returns true if this Agent is facing a specified location within a specified allowance, else returns false
	 * 
	 * @param pos ILocated2D representing a specific location
	 * @param allowance Double value representing a "buffer"
	 * @return True if the Agent is facing the specified location within the allowance, else returns false
	 */
	public boolean isFacing(ILocated2D pos, double allowance) {
		return CartesianGeometricUtilities.sourceHeadingTowardsTarget(this.getHeading(), this.getPosition(), pos,
				allowance);
	}
}
