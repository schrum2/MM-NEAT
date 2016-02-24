/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.BiasBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference.EscapeNodeDistanceDifferenceBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.lookahead.DirectionalReachSafetyBeforeThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestFarthestEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPillBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest.NearestPowerPillBlock;

/**
 * The simple mediator I was using to get results with the old version of
 * pacman.
 *
 * @author Jacob Schrum
 */
public class TweakMediator extends BlockLoadedInputOutputMediator {

//    private ArrayList<ArrayList<Long>> blockTiming = new ArrayList<ArrayList<Long>>();
    
    public TweakMediator() {
        super();
        
        // All that is needed to clear levels well
//        blocks.add(new BiasBlock());
//        blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false, Parameters.parameters.integerParameter("escapeNodeDepth"), false, false, false));
//        blocks.add(new NearestPillBlock());
//        blocks.add(new NearestPowerPillBlock());
//        blocks.add(new NearestFarthestEdibleGhostBlock(true));

        // Replacing the forward simulation
        blocks.add(new BiasBlock());
        blocks.add(new EscapeNodeDistanceDifferenceBlock(escapeNodes, false, false, 0, false, false, false));
        blocks.add(new DirectionalReachSafetyBeforeThreatGhostBlock(escapeNodes));
        blocks.add(new NearestPillBlock());
        blocks.add(new NearestPowerPillBlock());
        blocks.add(new NearestFarthestEdibleGhostBlock(true));
     
//        for(int i = 0; i < blocks.size(); i++){
//            blockTiming.add(new ArrayList<Long>());
//        }
    }
    
//    @Override
//    public double[] getInputs(GameFacade gs, int currentDir, int[] neighbors) {
//        double[] inputs = new double[numIn()];
//        int in = 0;
//        for (int i = 0; i < blocks.size(); i++) {
//            long start = System.currentTimeMillis();
//            in = blocks.get(i).incorporateSensors(inputs, in, gs, currentDir);
//            long end = System.currentTimeMillis();
//            blockTiming.get(i).add(end - start);
//        }
//        assert (in == numIn()) : "Improper inputs for Ms Pac-Man. Only " + in + " inputs: " + Arrays.toString(inputs);
//        return inputs;
//    }
//    
//    public void finish(){
//        for (int i = 0; i < blocks.size(); i++) {
//            ArrayList<Long> vals = blockTiming.get(i);
//            double[] values = new double[vals.size()];
//            for(int j = 0; j < values.length; j++){
//                values[j] = vals.get(j);
//            }
//            System.out.println(blocks.get(i).getClass().getSimpleName() + ": " + StatisticsUtilities.average(values));
//        }
//        System.out.println("----------------------------");
//    }
}
