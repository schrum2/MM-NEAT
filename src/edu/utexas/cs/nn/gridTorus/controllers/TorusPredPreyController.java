/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.gridTorus.controllers;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorld;

/**
 *
 * @author Jacob Schrum
 */
public abstract class TorusPredPreyController {

    public static int[][] actions = new int[][]{new int[]{0,1}, new int[]{1,0}, new int[]{0,-1}, new int[]{-1,0}};
    
    public int[] getAction(TorusAgent me, TorusPredPreyGame game){
        return getAction(me, game.getWorld(), game.getPredators(), game.getPrey());
    }
    
    public abstract int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey);
}
