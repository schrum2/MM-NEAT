/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.gridTorus;

import edu.utexas.cs.nn.util.CombinatoricUtilities;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;
import java.awt.Color;

/**
 *
 * @author Jacob Schrum
 */
public class TorusAgent implements ILocated2D {

    private final TorusWorld world;
    private int x;
    private int y;
    private final int type;

    public TorusAgent(TorusWorld world, int startX, int startY, int type) {
        this.world = world;
        this.x = startX;
        this.y = startY;
        this.type = type;
    }
    
    public Tuple2D getPosition() {
        return new Tuple2D(x,y);
    }

    /**
     * Returns Manhattan distance
     * @param other
     * @return 
     */
    public double distance(ILocated2D other) {
        return world.shortestXDistance(x, (int) other.getX()) + world.shortestYDistance(y, (int) other.getY());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void move(int dx, int dy) {
        x = world.boundX(x + dx);
        y = world.boundY(y + dy);
    }
    
    public Color getColor(){
        return CombinatoricUtilities.colorFromInt(type);
    }
    
    /**
     * Return true if other agent is in same cell as this agent
     * @param other torus agent
     * @return whether cell is shared by other agent
     */
    public boolean isCoLocated(TorusAgent other){
        return other != null && this.x == other.x && this.y == other.y;
    }

    /**
     * Is this agent in the same spot as any agent in a list?
     * @param others other agents
     * @return whether any share the cell of this agent
     */
    public boolean isCoLocated(TorusAgent[] others){
        for(int i = 0; i < others.length; i++) {
            if(this.isCoLocated(others[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given array of other agents, return the one that is closest to me
     * in Manhattan distance.
     * @param others agents
     * @return the closest agent in others
     */
    public TorusAgent closestAgent(TorusAgent[] others){
        TorusAgent closest = null;
        double closestDistance = Integer.MAX_VALUE;
        for(int i = 0; i < others.length; i++) {
            double manhattanDistance = distance(others[i]);
            if(manhattanDistance < closestDistance) {
                closestDistance = manhattanDistance;
                closest = others[i];
            }
        }
        return closest;
    }  
    
    public double[] distances(TorusAgent[] others){
        double[] ds = new double[others.length];
        for(int i = 0; i < others.length; i++) {
            ds[i] = this.distance(others[i]);
        }
        return ds;
    }

    public double shortestXOffset(TorusAgent torusAgent) {
        return world.shortestXOffset(x, torusAgent.x);
    }

    public double shortestYOffset(TorusAgent torusAgent) {
        return world.shortestYOffset(y, torusAgent.y);
    }
}
