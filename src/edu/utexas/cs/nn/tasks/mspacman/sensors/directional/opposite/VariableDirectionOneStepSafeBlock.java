/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.opposite;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToTargetThanThreatGhostBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import pacman.game.Constants;

/**
 * Although continuing in a given direction until a junction or power pill is
 * reached may not be possible, that does not mean the direction is unsafe. It
 * may be possible to take one step in the given direction and then turn around.
 * So, one step in a direction is safe, if turning around will be safe after one
 * time step.
 *
 * @author Jacob
 */
public class VariableDirectionOneStepSafeBlock extends VariableDirectionBlock {

    public final int[] ghostsToCheck;

    public VariableDirectionOneStepSafeBlock() {
        this(-1);
    }

    public VariableDirectionOneStepSafeBlock(int dir) {
        super(dir);
        int numActive = Parameters.parameters.integerParameter("numActiveGhosts");
        ghostsToCheck = new int[numActive];
        for (int i = 0; i < numActive; i++) {
            ghostsToCheck[i] = i;
        }
    }

    @Override
    public double wallValue() {
        return 0;
    }

    @Override
    public double getValue(GameFacade gf) {
        int current = gf.getPacmanCurrentNodeIndex();
        int[] threats = gf.getThreatGhostLocations();
        // Deal with very close threats
        if (threats.length > 0) {
            // There are threats
            Pair<Integer, int[]> threatPair = gf.getTargetInDir(current, threats, dir);
            int[] ghostIndexes = gf.getGhostIndexOfGhostAt(threatPair.t1);
            for (int i = 0; i < ghostIndexes.length; i++) {
                // Check each ghost at that location
                if (threatPair.t2.length <= Constants.EAT_DISTANCE + 2 && gf.isGhostIncoming(dir, ghostIndexes[i])) {
                    //System.out.println("Ghost is close and incoming, so direction "+dir+" is dangerous");
                    return 0;
                }
            }
        }
        int[] neighbors = gf.neighbors(current);
        int[] safePointBuffers = new int[neighbors.length];
        int[] safeTargets = ArrayUtil.combineIntArrays(gf.getJunctionIndices(), gf.getActivePowerPillsIndices());
        for (int i = 0; i < safePointBuffers.length; i++) {
            if (neighbors[i] != -1) {
                Pair<Integer, int[]> pair = gf.getTargetInDir(current, safeTargets, i);
                safePointBuffers[i] = VariableDirectionCloserToTargetThanThreatGhostBlock.pathReachesTargetSafelyWithBuffer(gf, pair.t1, pair.t2, ghostsToCheck);
                if (dir == i && safePointBuffers[i] > 0) { // early exit
                    //System.out.println("\tDir safe:"+dir+": because next point is reachable");
                    return 1.0; // can reach junction or power pill in desired direction
                }
            }
        }
        // Deal with threats exiting lair
        // This isn't coded right ... it never registers
//        if (gf.anyActiveGhostInLair()) {
//            int timeUntilExit = gf.timeUntilNextLairExit();
//            if (timeUntilExit <= 1 + Constants.EAT_DISTANCE) {
//                int lairExitNode = gf.getGhostInitialNodeIndex();
//                Pair<Integer, int[]> lairPair = gf.getTargetInDir(current, new int[]{lairExitNode}, dir);
//                if (lairPair.t2.length - 1 <= Constants.EAT_DISTANCE) { // -1 for a single step
//                    System.out.println("One step brings pacman within eating distance of lair exit");
//                    Executor.hold = true;
//                    return 0;
//                }
//            }
//        }
        // Does another route have a viable escape?
        for (int i = 0; i < safePointBuffers.length; i++) {
            if (i != dir && neighbors[i] != -1 && safePointBuffers[i] > 2) { // Already checked dir
                //System.out.println("\tDir safe:"+dir+": because can backtrack in dir " +i);
                // One step by the ghost and another step by pacman shift the buffer by 2
                return 1.0; // Turn around after one step is safe
            }
        }
        //System.out.println("\tDir NOT safe:" + dir);
        return 0;
    }

    @Override
    public String getLabel() {
        return "One Step OK";
    }
}
