/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 *
 * @author Jacob Schrum
 */
public class NNTorusPredPreyController extends TorusPredPreyController {
    private final Network nn;

    public NNTorusPredPreyController(Network nn){ //this sets up the neural network? -Gab
        this.nn = nn;
    }
    
    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) { //Not 100% what this does, my interpretation is it checks all the inputs and gets results from the current neural network? And this will determine the best course of action? -Gab
        double[] inputs = inputs(me,world,preds,prey); //So "me," "world," "preds," and "prey" are the different affecting agents? Or not agents, but what affects the outputs? -Gab
        double[] outputs = nn.process(inputs);
        // Assume one output for each direction
        return actions[StatisticsUtilities.argmax(outputs)];
    }

    public double[] inputs(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) { //So then, this method calculates those inputs for the neural network in order to figure what action to take in the method above -Gab
        double[] inputs = new double[preds.length * 2];
        for(int i = 0; i < preds.length; i++) {
            inputs[(2*i)] = me.shortestXOffset(preds[i]) / (1.0*world.width());
            inputs[(2*i)+1] = me.shortestYOffset(preds[i]) / (1.0*world.height()); //The +1 here is to place two inputs into the array at a time (I was initially confused as to why it was written this way) -Gab
        }
        return inputs;
    }

    public static String[] sensorLabels(int numPreds) { //What are Sensor Labels used for? -Gab
        String[] result = new String[numPreds * 2];
        for(int i = 0; i < numPreds; i++) {
            result[(2*i)] = "X Offset to Pred " + i;
            result[(2*i)+1] = "Y Offset to Pred " + i; //+1 here to enter two at a time again -Gab
        }
        return result;
    }
}
