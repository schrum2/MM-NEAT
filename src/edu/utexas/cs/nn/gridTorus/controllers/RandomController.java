/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.gridTorus.controllers;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 *
 * @author Jacob Schrum
 */
public class RandomController extends TorusPredPreyController {

    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        return actions[RandomNumbers.randomGenerator.nextInt(actions.length)];
    }

}
