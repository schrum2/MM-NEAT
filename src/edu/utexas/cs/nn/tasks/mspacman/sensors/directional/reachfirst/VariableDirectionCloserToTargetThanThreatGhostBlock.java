/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import java.util.Arrays;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public abstract class VariableDirectionCloserToTargetThanThreatGhostBlock extends VariableDirectionBlock {

    protected int[] ghostsToCheck;
    protected final boolean anyTarget;

    public VariableDirectionCloserToTargetThanThreatGhostBlock(int dir) {
        this(dir, false);
    }

    public VariableDirectionCloserToTargetThanThreatGhostBlock(int dir, boolean any) {
        super(dir);
        anyTarget = any;
        int numActive = Parameters.parameters.integerParameter("numActiveGhosts");
        ghostsToCheck = new int[numActive];
        for (int i = 0; i < numActive; i++) {
            ghostsToCheck[i] = i;
        }
    }

    public VariableDirectionCloserToTargetThanThreatGhostBlock(int dir, int[] ghosts) {
        this(dir, ghosts, false);
    }

    public VariableDirectionCloserToTargetThanThreatGhostBlock(int dir, int[] ghosts, boolean any) {
        super(dir);
        anyTarget = any;
        ghostsToCheck = ghosts;
    }

    /**
     * Returning 1 means it is safe to go to the nearest target in the given
     * direction. Returning 0 means it is not safe to go to the nearest target
     * in the given direction, possibly because there is no available target.
     *
     * @param gf GameFacade
     * @return described above
     */
    @Override
    public double getValue(GameFacade gf) {
        final int[] targets = getTargets(gf);
        if (targets.length == 0) {
            return 0;
        } else if (CommonConstants.reachabilityReportsBuffers && !anyTarget) {
            double safetyBuffer = closestTargetSafetyBufferInDirection(gf, targets, dir, ghostsToCheck);
            return Math.min(safetyBuffer, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
        } else {
            return anyTarget
                    ? (canReachAnyTargetSafelyInDirection(gf, targets, dir, ghostsToCheck) ? 1 : 0)
                    : (canReachClosestTargetSafelyInDirection(gf, targets, dir, ghostsToCheck) ? 1 : 0);
        }
    }

    public double wallValue() {
        return 0;
    }

    @Override
    public String getLabel() {
        return "Closer to " + getTargetType() + " than " + getObstacleType();
    }

    public abstract String getTargetType();

    public abstract int[] getTargets(GameFacade gf);

    public String getObstacleType() {
        return "Threat Ghosts " + Arrays.toString(ghostsToCheck);
    }

    public static boolean canReachAnyTargetSafelyInDirection(GameFacade gf, int[] targets, int dir) {
        int[] ghostsToCheck = new int[gf.getNumActiveGhosts()];
        for (int i = 0; i < ghostsToCheck.length; i++) {
            ghostsToCheck[i] = i;
        }
        return canReachAnyTargetSafelyInDirection(gf, targets, dir, ghostsToCheck);
    }

    public static boolean canReachAnyTargetSafelyInDirection(GameFacade gf, int[] targets, int dir, int[] ghostsToCheck) {
        int current = gf.getPacmanCurrentNodeIndex();
        for (int i = 0; i < targets.length; i++) {
            int[] path = gf.getDirectionalPath(current, targets[i], dir);
            if (path.length > 0 && pathReachesTargetSafelyWithBuffer(gf, targets[i], path, ghostsToCheck) > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean canReachClosestTargetSafelyInDirection(GameFacade gf, int[] targets, int dir, int[] ghostsToCheck) {
        int current = gf.getPacmanCurrentNodeIndex();
        Pair<Integer, int[]> targetPair = gf.getTargetInDir(current, targets, dir);
        return pathReachesTargetSafelyWithBuffer(gf, targetPair.t1, targetPair.t2, ghostsToCheck) > 0;
    }

    public static int closestTargetSafetyBufferInDirection(GameFacade gf, int[] targets, int dir, int[] ghostsToCheck) {
        int current = gf.getPacmanCurrentNodeIndex();
        Pair<Integer, int[]> targetPair = gf.getTargetInDir(current, targets, dir);
        return pathReachesTargetSafelyWithBuffer(gf, targetPair.t1, targetPair.t2, ghostsToCheck);
    }

    /**
     * See if pacman's path to the target is safe. If not safe, return 0.
     * Otherwise, see how close the nearest threat could be after the movement,
     * and return that distance (the buffer).
     *
     * @param gf game facade
     * @param target where pacman and ghost are going to
     * @param path path from pacman to target (path[0] is neighbor of pacman
     * location)
     * @param ghostsToCheck indexes (0-3) of the ghosts to check
     * @return 0 for not safe, else distance to nearest threat after move
     */
    public static int pathReachesTargetSafelyWithBuffer(GameFacade gf, int target, int[] path, int[] ghostsToCheck) {
        assert target == path[path.length - 1] : "Path doesn't lead to target: " + Arrays.toString(path) + ":" + target;
        final int currentPacmanNode = gf.getPacmanCurrentNodeIndex();
        int[] neighbors = gf.neighbors(currentPacmanNode);
        assert ArrayUtil.member(path[0], neighbors) : "Path does not actually start where pacman is! " + currentPacmanNode + ":" + Arrays.toString(path);
        assert ghostsToCheck.length <= gf.getNumActiveGhosts() : "Looking at more ghosts than exist";
        // Special: If last pill will be eaten, then set target sooner
        int[] activePills = gf.getActivePillsIndices();
        if(activePills.length > 0 && gf.getNumActivePowerPills() == 0 && ArrayUtil.subset(activePills, path)){
            int direction = ArrayUtil.position(neighbors, path[0]);
            Pair<Integer, int[]> toLastPill = gf.getTargetInDir(currentPacmanNode, activePills, direction, false); // false for longest path
            target = toLastPill.t1;
            path = toLastPill.t2; // A shorter path
//            if(CommonConstants.watch){
//                gf.addPoints(Color.ORANGE, path);
//            }
        }
        // Check each threat ghost path to closest target
        int dangerDifference = Integer.MAX_VALUE;
        for (int i = 0; i < ghostsToCheck.length; i++) {
            if (gf.isGhostThreat(ghostsToCheck[i])) {
                int ghostLocation = gf.getGhostCurrentNodeIndex(ghostsToCheck[i]);
                int[] ghostPath = gf.getGhostPath(ghostsToCheck[i], target);
                // Special case: ghost is already at destination, but not incoming
                int dir = gf.getNextMoveTowardsTarget(currentPacmanNode, path[0]);
                if (ghostLocation == target && !gf.isGhostIncoming(dir, ghostsToCheck[i])) {
//                    if(CommonConstants.watch) {
//                        System.out.println("Ghost " + ghostsToCheck[i] + " in dir "+dir+" not a threat");
//                        gf.addPoints(Color.BLUE, path);
//                        Executor.hold = true;
//                    }
                    continue;
                }
                // Special case: if ghost is moving away from pacman to the target,
                // then the ghost will reach it first, but it is still safe to go there.
                if (gf.isJunction(ghostLocation) || !(ArrayUtil.subset(ghostPath, path) && ArrayUtil.member(ghostLocation, path))) {
                    // If the ghost and pacman paths collide head-on, then path is unsafe
                    if (ArrayUtil.member(ghostLocation, path) && gf.ghostApproachingPacman(ghostsToCheck[i])) {
//                        if (CommonConstants.watch) {
//                            gf.addLines(Color.MAGENTA, ghostLocation, target);
//                        }
                        return 0;
                    }
                    // If the ghost path to the location reaches it sooner, it is unsafe
                    int obstacleToTargetDistance = ghostPath.length;
                    obstacleToTargetDistance -= (Constants.EAT_DISTANCE); // Need a safe buffer
                    if (obstacleToTargetDistance <= path.length) {
//                        if (CommonConstants.watch) {
//                            gf.addLines(Color.MAGENTA, ghostLocation, target);
//                        }
                        return 0; // Ghost is closer: not safe
                    } else {
                        dangerDifference = Math.min(dangerDifference, obstacleToTargetDistance - path.length);
                    }
                }
            } else if (gf.isGhostEdible(ghostsToCheck[i])) {
                int[] ghostPath = gf.getGhostPath(ghostsToCheck[i], target);
                int ghostLocation = gf.getGhostCurrentNodeIndex(ghostsToCheck[i]);
                // Special case: if ghost is moving away from pacman to the target,
                // then the ghost may reach it first, but it is still safe to go there.
                if (gf.isJunction(ghostLocation) || !(ArrayUtil.subset(ghostPath, path) && ArrayUtil.member(ghostLocation, path))) {
                    // If the ghost and pacman paths collide head-on, then we need
                    // to determine whether or not the ghost will be edible or a threat
                    // at the time
                    int edibleTime = gf.getGhostEdibleTime(ghostsToCheck[i]);
                    if (ArrayUtil.member(ghostLocation, path) && gf.ghostApproachingPacman(ghostsToCheck[i])) {
                        int distanceBetweenPacManAndGhost = (int) gf.getShortestPathDistance(ghostLocation, currentPacmanNode);
                        int timeUntilCollision = Constants.GHOST_SPEED_REDUCTION * ((distanceBetweenPacManAndGhost - Constants.EAT_DISTANCE) / (Constants.GHOST_SPEED_REDUCTION + 1));
                        if (timeUntilCollision >= edibleTime) {
//                            if (CommonConstants.watch) {
//                                gf.addLines(Color.MAGENTA, ghostLocation, target);
//                            }
                            return 0;
                        }
                    }
                    // Will ghost become a threat before pacman reaches the target?
                    if (path.length >= edibleTime) {
                        // Need to translate edible time into distance, and see if the ghost
                        // will become a threat and reach the destination before pacman.
                        int obstacleToTargetDistance = ghostPath.length + (edibleTime * (Constants.GHOST_SPEED_REDUCTION - 1));
                        obstacleToTargetDistance -= (Constants.EAT_DISTANCE); // Need a safe buffer
                        if (obstacleToTargetDistance <= path.length) {
//                            if (CommonConstants.watch) {
//                                gf.addLines(Color.MAGENTA, ghostLocation, target);
//                            }
                            return 0; // Ghost is closer: not safe
                        } else {
                            dangerDifference = Math.min(dangerDifference, obstacleToTargetDistance - path.length);
                        }
                    }
                }
            } else if (gf.ghostInLair(ghostsToCheck[i])) {
                int lairTime = gf.getGhostLairTime(ghostsToCheck[i]);
                // See if ghost will be out of lair before pacman reaches target
                if (lairTime < path.length) {
                    int lairExit = gf.getGhostInitialNodeIndex();
                    int lairToTargetDistance = (int) gf.getShortestPathDistance(lairExit, target);
                    int ghostEffectiveTravelDistance = lairToTargetDistance + lairTime;
                    ghostEffectiveTravelDistance -= (Constants.EAT_DISTANCE); // Need a safe buffer
                    if (ghostEffectiveTravelDistance <= path.length) {
//                        if (CommonConstants.watch) {
//                            gf.addLines(Color.MAGENTA, gf.getGhostInitialNodeIndex(), target);
//                        }
                        return 0; // Ghost is closer: not safe
                    } else {
                        dangerDifference = Math.min(dangerDifference, ghostEffectiveTravelDistance - path.length);
                    }
                }
            }
        }
//        if (CommonConstants.watch && !ArrayUtil.member(target, gf.getActivePowerPillsIndices())) {
//            gf.addLines(Color.WHITE, path[0], target);
//        }
        return dangerDifference; // No threat ghost is closer: safe
    }
}
