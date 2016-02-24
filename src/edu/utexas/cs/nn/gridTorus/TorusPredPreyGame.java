/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.gridTorus;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 *
 * @author Jacob Schrum
 */
public class TorusPredPreyGame {
    
    public static final int AGENT_TYPE_PRED = 0;
    public static final int AGENT_TYPE_PREY = 1;
    
    private final TorusWorld world;
    private final TorusAgent[] preds;
    private final TorusAgent[] preys;
    
    private boolean gameOver;
    private int time;
    private int timeLimit;

    public TorusPredPreyGame(int xDim, int yDim, int numPred, int numPrey){
        gameOver = false;
        time = 0;
        timeLimit = Parameters.parameters.integerParameter("torusTimeLimit");
        
        world = new TorusWorld(xDim, yDim);
        preds = new TorusAgent[numPred];
        preys = new TorusAgent[numPrey];
        // Place predators
        for(int i = 0; i < numPred; i++) {
            int[] pos = world.randomCell();
            preds[i] = new TorusAgent(world, pos[0], pos[1], AGENT_TYPE_PRED);
        }
        // Place prey where predators aren't
        for(int i = 0; i < numPrey; i++) {
            int[] pos = world.randomUnoccupiedCell(preds);
            preys[i] = new TorusAgent(world, pos[0], pos[1], AGENT_TYPE_PREY);
        }
    }
    
    public TorusWorld getWorld(){
        return world;
    }
    
    public TorusAgent[][] getAgents(){
        return new TorusAgent[][]{preds,preys};
    }

    public TorusAgent[] getPredators(){
        return preds;
    }
    
    public TorusAgent[] getPrey(){
        return preys;
    }
    
    public void advance(int[][] predMoves, int[][] preyMoves) {
        moveAll(predMoves, preds);
        moveAll(preyMoves, preys);
        eat(preds, preys);
        time++;
        gameOver = ArrayUtil.countOccurrences(null, preys) == preys.length ||
                time >= timeLimit;
    }
    
    public int getTime(){
        return time;
    }
    
    private static void moveAll(int[][] moves, TorusAgent[] agents) {
        assert moves.length == agents.length : "Moves and Agents don't match up: " + moves.length +" != "+ agents.length;
        for(int i = 0; i < agents.length; i++) {
            if(agents[i] != null) {
                agents[i].move(moves[i][0], moves[i][1]);
            }
        }
    }
    
    private static void eat(TorusAgent[] preds, TorusAgent[] preys) {
        for(int i = 0; i < preys.length; i++) {
            if(preys[i] != null && preys[i].isCoLocated(preds)){ // Prey is eaten
                preys[i] = null;
            }
        }
    }

    boolean gameOver() {
        return gameOver;
    }
}
