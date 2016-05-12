package edu.utexas.cs.nn.tasks.mspacman.data;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class NodeCollection {

    protected int lastNodeVisited = -1;
    protected int[] savedNodes = null;
    protected final boolean escapeToPowerPills;

    public NodeCollection() {
        escapeToPowerPills = Parameters.parameters.booleanParameter("escapeToPowerPills");
    }

    public NodeCollection(NodeCollection original) {
        this.lastNodeVisited = original.lastNodeVisited;
        this.escapeToPowerPills = original.escapeToPowerPills;
        //this.alwaysUpdate = original.alwaysUpdate;
        if (original.savedNodes != null) {
            this.savedNodes = new int[original.savedNodes.length];
            System.arraycopy(original.savedNodes, 0, this.savedNodes, 0, original.savedNodes.length);
        } else {
            this.savedNodes = null;
        }
    }

    public int getLastNodeVisited() {
        return lastNodeVisited;
    }

    public void updateNodes(GameFacade gs, int current) {
        if (gs.levelJustChanged() || gs.justAtePowerPill()) {
            reset();
        }
        updateNodes(gs, current, true);
    }

    public abstract void updateNodes(GameFacade gs, int current, boolean draw);

    public abstract NodeCollection copy();

    protected void addExtraNodes(ArrayList<Integer> addedNodes, int current, GameFacade gs) {
        // Add last pill, if it is clear
        int[] activePills = gs.getActivePillsIndices();
        int nearestPill = gs.getClosestNodeIndexFromNodeIndex(current, activePills);
        int farthestPill = gs.getFarthestNodeIndexFromNodeIndex(current, activePills);
        int[] tempPath = gs.getShortestPath(nearestPill, farthestPill);
        int[] pillPath = new int[tempPath.length + 1];
        System.arraycopy(tempPath, 0, pillPath, 0, tempPath.length);
        pillPath[tempPath.length] = farthestPill;
        if (ArrayUtil.subset(activePills, pillPath)) {
            // Path will consume all pills
            assert gs.nodeInMaze(farthestPill) : "Farthest pill " + farthestPill + " not in maze";
            addedNodes.add(farthestPill);
        }

        if (escapeToPowerPills) {
            int[] activePowerPills = gs.getActivePowerPillsIndices();
            for (Integer j : activePowerPills) {
                if (j == current || j == lastNodeVisited) { // Can't escape to current location or previous escape node
                    continue;
                }
                assert gs.nodeInMaze(j) : "Power pill " + j + " not in maze";
                addedNodes.add(j);
            }
        }

    }

    protected void removeLastNodeVisited(int current, int[] junctions, GameFacade gs) {
        if (!gs.ghostReversal()) {
            int closestJunction = gs.getClosestNodeIndexFromNodeIndex(current, junctions);
            for (int i = 0; i < savedNodes.length; i++) {
                if (savedNodes[i] == -1 && closestJunction != lastNodeVisited && ArrayUtil.member(lastNodeVisited, junctions)) {
                    savedNodes[i] = lastNodeVisited;
                    lastNodeVisited = -1;
                } else if (savedNodes[i] == this.lastNodeVisited) {
                    savedNodes[i] = -1;
                }
            }
        }
    }

    public void reset() {
        savedNodes = null;
        lastNodeVisited = -1;
    }

    public boolean ready() {
        return savedNodes != null;
    }

    public int[] getNodes() {
        assert (savedNodes != null) : "Why is savedNodes still null?";
        return savedNodes;
    }
}
