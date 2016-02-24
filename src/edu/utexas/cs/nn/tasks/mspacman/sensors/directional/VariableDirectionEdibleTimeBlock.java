/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionEdibleTimeBlock extends VariableDirectionBlock {

    public VariableDirectionEdibleTimeBlock() {
        this(-1);
    }

    public VariableDirectionEdibleTimeBlock(int dir) {
        super(dir);
    }

    @Override
    public double getValue(GameFacade gf) {
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] targets = gf.getActiveGhostLocations();
        if (targets.length == 0) {
            return 0; // no ghost sensed   
        } else {
            Pair<Integer, int[]> pair = gf.getTargetInDir(current, targets, dir);
            int ghostLocation = pair.t1;
            int[] ghostIndexes = gf.getGhostIndexOfGhostAt(ghostLocation);
            assert ghostIndexes.length > 0 : "There should be a ghost at the specified location";
            double[] edibleTimes = new double[ghostIndexes.length];
            for(int i = 0; i < ghostIndexes.length; i++){
                edibleTimes[i] = gf.getGhostEdibleTime(ghostIndexes[i]); // will be 0 if ghost is a threat 
            }
            double edibleTime = StatisticsUtilities.minimum(edibleTimes);
            return edibleTime / Constants.EDIBLE_TIME;
        }
    }

    @Override
    public String getLabel() {
        return "Edible Time of First Ghost";
    }

    @Override
    public double wallValue() {
        return 0;
    }
}
