
package edu.utexas.cs.nn.gridTorus;

import edu.utexas.cs.nn.util.CombinatoricUtilities;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.awt.Color;

/**
 * Generic agent in a torus grid world.
 * 
 * @author Jacob Schrum modified by Alex Rollins
 */
public class TorusAgent implements ILocated2D {

	private final TorusWorld world;
	private int x;
	private int y;
	private final int type;

	/**
	 * Constructor to create the agent
	 * 
	 * @param world
	 *            a grid world
	 * @param startX
	 *            the starting x-coordinate for the agent's location
	 * @param startY
	 *            the starting y-coordinate for the agent's location
	 * @param type
	 *            integer defining the agent type for coloring
	 */
	public TorusAgent(TorusWorld world, int startX, int startY, int type) {
		this.world = world;
		this.x = startX;
		this.y = startY;
		this.type = type;
	}

	/**
	 * get position of the agent returns the tuple, the x and y coordinates
	 */
        @Override
	public Tuple2D getPosition() {
		return new Tuple2D(x, y);
	}

	/**
	 * Returns Manhattan distance
	 * 
	 * @param other
	 * @return
	 */
        @Override
	public double distance(ILocated2D other) {
		if (other == null) {
			// this agent (other) got eaten and no longer exists so is null and
			// is no longer there
			// set to be infinite distance away so that every other agent is
			// always closer
			return Double.POSITIVE_INFINITY;
		}
		return world.shortestXDistance(x, (int) other.getX()) + world.shortestYDistance(y, (int) other.getY());
	}

	/**
	 * return the position of the agent on the x-axis of the grid world
	 */
        @Override
	public double getX() {
		return x;
	}

	/**
	 * return the position of the agent on the y-axis of the grid world
	 */
        @Override
	public double getY() {
		return y;
	}

	/**
	 * Move the agent by dx and dy amount
	 * 
	 * @param dx
	 *            amount to move the agent in the x direction
	 * @param dy
	 *            amount to move the agent in the y direction
	 */
	public void move(int dx, int dy) {
		x = world.boundX(x + dx);
		y = world.boundY(y + dy);
	}

	/**
	 * get the color of this agent
	 * 
	 * @return the color of this type of agent
	 */
	public Color getColor() {
		return CombinatoricUtilities.colorFromInt(type);
	}

	/**
	 * Return true if other agent is in same cell as this agent
	 * 
	 * @param other
	 *            torus agent
	 * @return whether cell is shared by other agent
	 */
	public boolean isCoLocated(TorusAgent other) {
		return other != null && this.x == other.x && this.y == other.y;
	}

	/**
	 * Is this agent in the same spot as any agent in a list?
	 * 
	 * @param others
	 *            other agents
	 * @return whether any share the cell of this agent
	 */
	public boolean isCoLocated(TorusAgent[] others) {
            for (TorusAgent other : others) {
                if (isCoLocated(other)) {
                    return true;
                }
            }
            return false;
	}

	/**
	 * Given array of other agents, return the one that is closest to me in
	 * Manhattan distance.
	 * 
	 * @param others
	 *            agents
	 * @return the closest agent in others
	 */
    public TorusAgent closestAgent(TorusAgent[] others) {
        TorusAgent closest = null;
        double closestDistance = Integer.MAX_VALUE;
        for (TorusAgent other : others) {
            double manhattanDistance = distance(other);
            if (manhattanDistance < closestDistance) {
                closestDistance = manhattanDistance;
                closest = other;
            }
        }
        return closest;
    }

	/**
	 * Find the distance from this agent to all other agents
	 * 
	 * @param others
	 *            array of the other agents
	 * @return array with the distance from this agent to each agent
	 */
	public double[] distances(TorusAgent[] others) {
		double[] ds = new double[others.length];
		for (int i = 0; i < others.length; i++) {
			ds[i] = this.distance(others[i]);
		}
		return ds;
	}

	/**
	 * Shortest x offset from this agent to the given agent, where a negative
	 * value means left, and a positive value means right.
	 *
	 * @param torusAgent
	 *            the given agent to compare to
	 * @return offset from this agent to the given agent
	 */
	public double shortestXOffset(TorusAgent torusAgent) {
		if (torusAgent == null) {
			return world.width();
		}
		return world.shortestXOffset(x, torusAgent.x);
	}

	/**
	 * Shortest y offset from this agent to the given agent
	 *
	 * @param torusAgent
	 *            the given agent to compare to
	 * @return offset from this agent to the given agent
	 */
	public double shortestYOffset(TorusAgent torusAgent) {
		if (torusAgent == null) {
			return world.height();
		}
		return world.shortestYOffset(y, torusAgent.y);
	}
}
