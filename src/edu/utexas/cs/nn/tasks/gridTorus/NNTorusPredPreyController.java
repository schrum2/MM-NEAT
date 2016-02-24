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

    public NNTorusPredPreyController(Network nn){
        this.nn = nn;
    }
    
    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        double[] inputs = inputs(me,world,preds,prey);
        double[] outputs = nn.process(inputs);
        // Assume one output for each direction
        return actions[StatisticsUtilities.argmax(outputs)];
    }

    public double[] inputs(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        double[] inputs = new double[preds.length * 2];
        for(int i = 0; i < preds.length; i++) {
            inputs[(2*i)] = me.shortestXOffset(preds[i]) / (1.0*world.width());
            inputs[(2*i)+1] = me.shortestYOffset(preds[i]) / (1.0*world.height());
        }
        return inputs;
    }

    public static String[] sensorLabels(int numPreds) {
        String[] result = new String[numPreds * 2];
        for(int i = 0; i < numPreds; i++) {
            result[(2*i)] = "X Offset to Pred " + i;
            result[(2*i)+1] = "Y Offset to Pred " + i;
        }
        return result;
    }
}
