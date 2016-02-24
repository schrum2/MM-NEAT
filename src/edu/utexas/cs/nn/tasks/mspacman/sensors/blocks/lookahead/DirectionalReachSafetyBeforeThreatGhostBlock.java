/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.lookahead;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.data.NodeCollection;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToTargetThanThreatGhostBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Jacob Schrum
 */
public class DirectionalReachSafetyBeforeThreatGhostBlock extends MsPacManSensorBlock {

    private final int depth;
    private final boolean escapeToPowerPills;
    private final int absence;
    private final NodeCollection escapeNodes;

    public DirectionalReachSafetyBeforeThreatGhostBlock(NodeCollection escapeNodes) {
        this.depth = Parameters.parameters.integerParameter("escapeNodeDepth");
        this.escapeToPowerPills = Parameters.parameters.booleanParameter("escapeToPowerPills");
        this.absence = Parameters.parameters.booleanParameter("absenceNegative")  ? -1 : 0;
        this.escapeNodes = escapeNodes;
    }

    @Override
    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? lastDirection : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        ArrayList<Set<Integer>> safePoints = gf.junctionsAtDepth(current, depth, escapeToPowerPills, escapeNodes.getLastNodeVisited());

        int[] depthSafety = new int[GameFacade.NUM_DIRS];
        for (int i = 1; i <= depth; i++) {
            int[] targets = ArrayUtil.integerSetToArray(safePoints.get(i));
            for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
                int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
                boolean wall = neighbors[dir] == -1;
                if (wall) {
                    inputs[in++] = absence;
                } else {
                    boolean safe = false;
                    if(depthSafety[dir] == i - 1){
                        // Previous depth was safe, so check the next
                        safe = VariableDirectionCloserToTargetThanThreatGhostBlock.canReachAnyTargetSafelyInDirection(gf, targets, dir);
                        if(safe){
                            depthSafety[dir] = i;
                        }
                    }
                    inputs[in++] = safe ? 1 : absence;
                }
            }
        }
        
//        if(CommonConstants.watch){
//            for(int i = safePoints.size() - 1; i >= 0; i--){
//                Set<Integer> s = safePoints.get(i);
//                gf.addPoints(CombinatoricUtilities.colorFromInt(i), s);
//            }
//        }
        
        return in;
    }

    @Override
    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        for (int i = 1; i <= depth; i++) {
            labels[in++] = "D" + i + " Can Safely Reach Safety " + first;
            labels[in++] = "D" + i + " Can Safely Reach Safety  Right";
            labels[in++] = "D" + i + " Can Safely Reach Safety " + last;
            labels[in++] = "D" + i + " Can Safely Reach Safety Left";
        }
        return in;
    }

    @Override
    public int numberAdded() {
        return depth * GameFacade.NUM_DIRS;
    }
}
