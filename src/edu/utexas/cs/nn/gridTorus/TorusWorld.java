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

    public TorusWorld(int xDim, int yDim) {
        this.xDim = xDim;
        this.yDim = yDim;
    }

    public int height() {
        return yDim;
    }

    public int width() {
        return xDim;
    }

    public static int bound(int p, int max) {
        return (p < 0 ? p + max : p % max);
    }

    public int boundX(int x) {
        return bound(x, xDim);
    }

    public int boundY(int y) {
        return bound(y, yDim);
    }

    public static int shortestDistance(int p1, int p2, int max) {
        int plainDis = Math.abs(p2 - p1);
        int wrapDis = max - plainDis;
        return Math.min(plainDis, wrapDis);
    }

    public int shortestXDistance(int x1, int x2) {
        return shortestDistance(x1, x2, xDim);
    }

    public int shortestYDistance(int y1, int y2) {
        return shortestDistance(y1, y2, yDim);
    }

    /**
     * Shortest x offset from position x1 to position x2, where a negative value
     * means left, and a positive value means right.
     *
     * @param x1 x-coord of agent
     * @param x2 x-coord of relative target
     * @return offset from x1 to x2
     */
    public int shortestXOffset(int x1, int x2) {
        return shortestOffset(x1, x2, xDim);
    }

    public int shortestYOffset(int y1, int y2) {
        return shortestOffset(y1, y2, yDim);
    }

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

    public int[] randomCell() {
        return new int[]{RandomNumbers.randomGenerator.nextInt(xDim), RandomNumbers.randomGenerator.nextInt(yDim)};
    }

    public int[] randomUnoccupiedCell(TorusAgent[] agents) {
        int[] candidate = null;
        boolean found = false;
        while (!found) {
            candidate = randomCell();
            found = true;
            for (int i = 0; found && i < agents.length; i++) {
                if (agents[i] != null
                        && candidate[0] == agents[i].getX()
                        && candidate[1] == agents[i].getY()) {
                    found = false;
                }
            }
        }
        return candidate;
    }
}
