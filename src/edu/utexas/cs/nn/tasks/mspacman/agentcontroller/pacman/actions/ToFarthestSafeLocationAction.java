package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.data.NodeCollection;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.facades.GhostControllerFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class ToFarthestSafeLocationAction implements MsPacManAction {

    private final int depth;
    private final NodeCollection nodes;
    private final GhostControllerFacade ghostModel;
    private int lastTarget;
    private int lastMove;

    public ToFarthestSafeLocationAction(int depth, NodeCollection nodes, GhostControllerFacade ghostModel) {
        this.depth = depth;
        this.nodes = nodes;
        this.ghostModel = ghostModel;
        this.lastTarget = -1;
        this.lastMove = -1;
    }

    public int getMoveAction(GameFacade gf) {
        HashMap<Integer, Triple<Integer, Integer, Integer>> targets = getTargets(gf);
        if (targets.isEmpty()) {
            return -1; // cede control to other action
        } else {
            int[] arrayTargets = ArrayUtil.integerSetToArray(targets.keySet());
            if (CommonConstants.watch) {
                gf.addPoints(Color.GREEN, arrayTargets);
            }
            int current = gf.getPacmanCurrentNodeIndex();
            int currentDir = gf.getPacmanLastMoveMade();
            int farthest = -Integer.MAX_VALUE;
            double farthestDistance = 0;
            for (Entry<Integer, Triple<Integer, Integer, Integer>> e : targets.entrySet()) {
                if (compareDistance(currentDir, e.getKey(), e.getValue(), gf) > farthestDistance) {
                    farthest = e.getKey();
                    farthestDistance = compareDistance(currentDir, e.getKey(), e.getValue(), gf);
                }
            }
            if (farthest == -Integer.MAX_VALUE) {
                return -1;
            }
            if (CommonConstants.watch) {
                gf.addPoints(Color.RED, new int[]{farthest});
            }
            int move = targets.get(farthest).t1; // The direction to go in to reach farthest safely
            //System.out.println("currentDir:" + currentDir + ":farthest:" + farthest + ":move:" + move + ":distance:" + targets.get(farthest).t2 + ":metric:" + compareDistance(currentDir, farthest, targets.get(farthest)) + ":triple:" + targets.get(farthest));
            // Don't suddenly try to reach the same destination by a different route
            if (farthest == lastTarget && !gf.isJunction(current) && move == GameFacade.getReverse(lastMove)) {
                //System.out.print(move + ":force:");
                move = gf.getRestrictedNextDir(current, farthest, lastMove);
            }
            lastTarget = farthest;
            lastMove = move;
            //System.out.println(move + ":" + farthest);
            return move;
        }
    }

    /**
     * Given a pair of both the direction and distance to reach a location,
     * return a metric similar to distance, but that treats routes in the same
     * direction currently being followed as farther, and thus more likely to be
     * picked.
     *
     * @param directionDistancePair
     * @return
     */
    public double compareDistance(int currentDir, int destination, Triple<Integer, Integer, Integer> directionDistanceNumPowerPillsTriple, GameFacade gf) {
        double result = //directionDistanceNumPowerPillsTriple.t2  // distance traveled
                gf.getEuclideanDistance(gf.getPacmanCurrentNodeIndex(), destination)
                + (directionDistanceNumPowerPillsTriple.t1 == currentDir && !ArrayUtil.member(destination, gf.getActivePowerPillsIndices()) ? 50 : 0) // paths in same direction are more favorable
                + directionDistanceNumPowerPillsTriple.t3 * 200; // paths that consume fewer power pills are more favorable
        return result;
    }

    public HashMap<Integer, Triple<Integer, Integer, Integer>> getTargets(GameFacade gf) {
        int current = gf.getPacmanCurrentNodeIndex();
        HashMap<Integer, Triple<Integer, Integer, Integer>> safeDirs = new HashMap<Integer, Triple<Integer, Integer, Integer>>();
        int[] neighbors = gf.neighbors(current);
        disallowNeighbors(neighbors, gf);
        getSafeLocationsFrom(gf, gf.copy(), current, 0, neighbors, safeDirs, depth, -1);
        return safeDirs;
    }

    private void getSafeLocationsFrom(GameFacade original, GameFacade gf, int current, int currentLength, int[] neighbors, HashMap<Integer, Triple<Integer, Integer, Integer>> safe, int remainingDepth, int originalDir) {
        // Only simulate so many node jumps ahead
        if (remainingDepth == 0 || original.getCurrentLevel() != gf.getCurrentLevel()) {
            return;
        }
        //int[] checkNeighbors = gf.neighbors(current);
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != -1) {
                assert (gf.neighbors(current)[i] == neighbors[i]) : (current + " neighbors are " + Arrays.toString(gf.neighbors(current)) + ", not " + Arrays.toString(neighbors));
                nodes.updateNodes(gf, gf.getPacmanCurrentNodeIndex(), false);
                int[] targets = nodes.getNodes();
                Pair<Integer, int[]> pair = gf.getTargetInDir(current, targets, i);
                int nearestInDir = pair.t1;
                GameFacade next = gf.simulateToNextTarget(i, ghostModel, nearestInDir);
                // Pacman was not eaten by ghost
                if (next != null) {
//                    if(next.getPacmanCurrentNodeIndex() != nearestInDir){
//                        System.out.println("Warning: next loc " + next.getPacmanCurrentNodeIndex() + " != " + nearestInDir + " path result");
//                        System.out.println("original time: " + gf.getTotalTime());
//                        System.out.println("next time: " + next.getTotalTime());
//                    }
                    if (CommonConstants.watch) {
                        original.addLines(Color.WHITE, current, nearestInDir);
                    }
                    // The node was safely reached
                    boolean contains = safe.containsKey(nearestInDir);
                    int newLength = currentLength + pair.t2.length;
                    if (!contains || newLength < safe.get(nearestInDir).t2) {
                        int thisDir = originalDir == -1 ? i : originalDir;
                        safe.put(nearestInDir, new Triple<Integer, Integer, Integer>(thisDir, newLength, next.getNumActivePowerPills()));
                        // Don't need to check lives == 0 here since gameOver covers it
                        if (original.getCurrentLevel() != next.getCurrentLevel() || next.gameOver()) {
                            return;
                        }

                        int lastBeforeTarget = pair.t2.length - 2 < 0 ? current : pair.t2[pair.t2.length - 2];
                        int[] junctionNeighbors = gf.neighbors(next.getPacmanCurrentNodeIndex());
                        // No reversal
                        boolean found = false;
                        for (int j = 0; !found; j++) {
//                            if(j == 4){
//                                System.out.println("Path to point: " + Arrays.toString(pair.t2));
//                                System.out.println("junctionNeighbors: " + Arrays.toString(junctionNeighbors));
//                                System.out.println("lastBeforeTarget:"+lastBeforeTarget);
//                                System.out.println("current:"+current);
//                                System.out.println("nearestInDir:"+nearestInDir);
//                            }
                            if (junctionNeighbors[j] == lastBeforeTarget) {
                                junctionNeighbors[j] = -1;
                                found = true; // should always find
                            }
                        }
                        disallowNeighbors(junctionNeighbors, next);
                        getSafeLocationsFrom(original, next, nearestInDir, newLength, junctionNeighbors, safe, remainingDepth - 1, thisDir);
                    }
                }
            }
        }

    }

    /**
     * Non-neg values in neighbors must be neighbors of the current pacman
     * position in gf, but not all neighbors must be present. Some may be
     * disabled. Even more may be disabled within the function.
     *
     * @param neighbors allowable safe paths, to be modified
     * @param gf
     */
    private void disallowNeighbors(int[] neighbors, GameFacade gf) {
        // Need to disallow some neighbors because the nodes they lead to are threatened by ghosts
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != -1) {
                // Consider
                final int current = gf.getPacmanCurrentNodeIndex();
                nodes.updateNodes(gf, gf.getPacmanCurrentNodeIndex(), false);
                assert gf.neighbors(current)[i] != -1 : ("Neighbors don't correspond to " + current + ":neighbors=" + Arrays.toString(neighbors) + ":checkNeighbors=" + Arrays.toString(gf.neighbors(current)));
                Pair<Integer, int[]> pair = gf.getTargetInDir(current, nodes.getNodes(), i);
                Pair<Double, Double> dis = gf.closestThreatToPacmanPath(pair.t2, pair.t1);
                double pacManDistance = dis.t1;
                double closestThreatDistance = dis.t2;
                double diff = closestThreatDistance - pacManDistance - (Constants.EAT_DISTANCE + 1);
                if (diff < 0) {
                    neighbors[i] = -1;
                }
            }
        }
    }
}
