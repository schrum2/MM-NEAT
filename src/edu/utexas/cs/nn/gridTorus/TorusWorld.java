/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.gridTorus;

import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 * 2D grid world that wraps around
 *
 * @author Jacob Schrum
 */
public class TorusWorld {

	private final int xDim;
	private final int yDim;

	/**
	 * constructor to create the grid world with the given x and y dimensions
	 * 
	 * @param xDim
	 *            x dimensions
	 * @param yDim
	 *            y dimensions
	 */
	public TorusWorld(int xDim, int yDim) {
		this.xDim = xDim;
		this.yDim = yDim;
	}

	/**
	 * 
	 * @return the height of the world (simply the y dimension)
	 */
	public int height() {
		return yDim;
	}

	/**
	 * 
	 * @return the width of the world (simply the x dimension)
	 */
	public int width() {
		return xDim;
	}

	/**
	 * the method that causes agents to wrap around the grid world
	 * 
	 * @param p
	 *            position of the agent (x or y)
	 * @param max
	 *            maximum position in the grid world (will be xDim or yDim)
	 * @return the new position of the agent after bounding it in a wrapped
	 *         manner
	 */
	public static int bound(int p, int max) {
		// if the agent position is less than zero, it is off the map,
		// so move it to the other side (max value of dimensions)
		// else, move it within the boundaries by modulating it
		return (p < 0 ? p + max : p % max);
	}

	/**
	 * bound the agent along the x-axis
	 * 
	 * @param x
	 *            the agent's position on the x-axis
	 * @return the new position of the agent after bounding it in a wrapped
	 *         manner
	 */
	public int boundX(int x) {
		return bound(x, xDim);
	}

	/**
	 * bound the agent along the y-axis
	 * 
	 * @param y
	 *            the agent's position on the y-axis
	 * @return the new position of the agent after bounding it in a wrapped
	 *         manner
	 */
	public int boundY(int y) {
		return bound(y, yDim);
	}

	/**
	 * find the shortest distance between the two given positions this could be
	 * simply the distance between the two on the axis on the grid or the
	 * distance from each other by going off the edge and wrapping, whichever is
	 * closer
	 * 
	 * @param p1
	 *            position of first agent (x or y)
	 * @param p2
	 *            position of second agent (x or y)
	 * @param max
	 *            boundary of gridWorld (maximum possible position/edge of
	 *            world), xDim or yDim
	 * @return the distance that was shorter
	 */
	public static int shortestDistance(int p1, int p2, int max) {
		int plainDis = Math.abs(p2 - p1);
		int wrapDis = max - plainDis;
		return Math.min(plainDis, wrapDis);
	}

	/**
	 * find the shortest distance between the two given positions for the x-axis
	 * 
	 * @param x1
	 *            agent 1 x position
	 * @param x2
	 *            agent 2 x position
	 * @return the shortest distance between the agents, either wrapping or
	 *         plain distance
	 */
	public int shortestXDistance(int x1, int x2) {
		return shortestDistance(x1, x2, xDim);
	}

	/**
	 * find the shortest distance between the two given positions for the y-axis
	 * 
	 * @param y1
	 *            agent 1 y position
	 * @param y2
	 *            agent 2 y position
	 * @return the shortest distance between the agents, either wrapping or
	 *         plain distance
	 */
	public int shortestYDistance(int y1, int y2) {
		return shortestDistance(y1, y2, yDim);
	}

	/**
	 * Shortest x offset from position x1 to position x2, where a negative value
	 * means left, and a positive value means right.
	 *
	 * @param x1
	 *            x-coord of agent
	 * @param x2
	 *            x-coord of relative target
	 * @return offset from x1 to x2
	 */
	public int shortestXOffset(int x1, int x2) {
		return shortestOffset(x1, x2, xDim);
	}

	/**
	 * Shortest y offset from position y1 to position y2
	 *
	 * @param y1
	 *            y-coord of agent
	 * @param y2
	 *            y-coord of relative target
	 * @return offset from y1 to y2
	 */
	public int shortestYOffset(int y1, int y2) {
		return shortestOffset(y1, y2, yDim);
	}

	/**
	 * Shortest offset from position p1 to position p2 (used on either x-axis or
	 * y-axis)
	 * 
	 * @param p1
	 *            position x or y of agent 1
	 * @param p2
	 *            position x or y of agent 2
	 * @param max
	 *            xDim or yDim, depending on which axis is being used
	 * @return the shortest offset from p1 to p2
	 */
	public static int shortestOffset(int p1, int p2, int max) {
		if (p1 < p2) {
			int plainDis = p2 - p1;
			int wrapDis = max - plainDis;
			if (plainDis < wrapDis) {
				return plainDis;
			} else {
				return -wrapDis;
			}
		} else {
			int plainDis = p1 - p2;
			int wrapDis = max - plainDis;
			if (plainDis < wrapDis) {
				return -plainDis;
			} else {
				return wrapDis;
			}
		}
	}

	/**
	 * 
	 * @return a random cell (or x-y coordinate) within the grid world
	 */
	public int[] randomCell() {
		return new int[] { RandomNumbers.randomGenerator.nextInt(xDim), RandomNumbers.randomGenerator.nextInt(yDim) };
	}

	/**
	 * find a random cell within the grid world that does not have an agent in
	 * it
	 * 
	 * @param agents
	 *            the array of all agents
	 * @return the first random unoccupied cell found
	 */
	public int[] randomUnoccupiedCell(TorusAgent[] agents) {
		int[] candidate = null;
		boolean found = false;
		while (!found) { // Keep generating cells until an empty one is found
			candidate = randomCell(); // Check a random cell
			found = true;
			// See if any agents are in that cell
			for (int i = 0; found && i < agents.length; i++) {
				if (agents[i] != null && candidate[0] == agents[i].getX() && candidate[1] == agents[i].getY()) {
					found = false; // If an agent was in the cell, then try
									// again
				}
			}
		}
		return candidate;
	}
}
