/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock extends VariableDirectionCloserToTargetThanThreatGhostBlock {

    public VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock(int dir) {
        super(dir, true);
    }

    public VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock(int dir, int[] ghosts) {
        super(dir, ghosts, true);
    }

    @Override
    public String getTargetType() {
        return "Depth 2 Junction";
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        int[] junctions = gf.getJunctionIndices();
        Pair<Integer, int[]> closest = gf.getTargetInDir(gf.getPacmanCurrentNodeIndex(), junctions, dir);
        
        // Lazy method: Targets actually include depth 3, 4, etc junctions as well
        // Remove first junction encountered
        //return ArrayUtil.filter(junctions, closest.t1);
        
        int[] neighbors = gf.neighbors(closest.t1);
        ArrayList<Integer> d2 = new ArrayList<Integer>(GameFacade.NUM_DIRS);
        for(int i = 0; i < neighbors.length; i++){
            if(neighbors[i] != -1) {
                Pair<Integer, int[]> closestD2 = gf.getTargetInDir(closest.t1, junctions, i);
                d2.add(closestD2.t1);
            }
        }
        int[] result = ArrayUtil.intArrayFromArrayList(d2);
//        if(CommonConstants.watch) {
//            gf.addPoints(dir == 0 ? Color.WHITE : (dir == 1 ? Color.GRAY : (dir == 2 ? Color.YELLOW : Color.PINK)), result);
//        }
        return result;
    }
}
