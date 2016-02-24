/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.Min;
import edu.utexas.cs.nn.util.stats.Statistic;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionDistanceFromPowerPillToThreatGhostBlock extends VariableDirectionBlock {
    private final int numActiveGhosts;
    private final Statistic stat;
    
    public VariableDirectionDistanceFromPowerPillToThreatGhostBlock(int dir, Statistic stat) {
        super(dir);
        this.stat = stat;
        this.numActiveGhosts = Parameters.parameters.integerParameter("numActiveGhosts");
    }

    public double getValue(GameFacade gf) {
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] pps = gf.getActivePowerPillsIndices();
        if (pps.length == 0) {
            return 1.0; // Distance is "infinity"   
        } else {
            Pair<Integer, int[]> ppPair = gf.getTargetInDir(current, pps, dir);
            double[] distances = new double[numActiveGhosts];
            boolean maxedOut = false;
            for(int i = 0; i < numActiveGhosts; i++){
                if(gf.isGhostThreat(i)) {
                    distances[i] = gf.getShortestPathDistance(ppPair.t1, gf.getGhostCurrentNodeIndex(i));
                    if(distances[i] > GameFacade.MAX_DISTANCE) {
                        distances[i] = GameFacade.MAX_DISTANCE;
                        maxedOut = true;
                    }
                } else {
                    distances[i] = GameFacade.MAX_DISTANCE;
                    maxedOut = true;
                }
            }            
            if(!(stat instanceof Min) && maxedOut) {
                return 1.0; // Even an average inclusing infinity averages to infinity
            } else {
                return stat.stat(distances) / GameFacade.MAX_DISTANCE;
            }
        }
    }

    public double wallValue() {
        return 1;
    }

    @Override
    public String getLabel() {
        return stat.getClass().getSimpleName() + " Distance from Power Pill to Threat Ghost";
    }
}
