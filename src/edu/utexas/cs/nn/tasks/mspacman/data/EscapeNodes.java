/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.data;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class EscapeNodes extends NodeCollection {

    public EscapeNodes() {
        super();
    }

    public EscapeNodes(EscapeNodes original) {
        super(original);
    }

    public void updateNodes(GameFacade gs, int current, boolean draw) {
        if (gs.isJunction(current)) {
            //System.out.println("Remove " + current);
            lastNodeVisited = current;
        }
        final int[] junctions = gs.getJunctionIndices();

        // Always update
        // Get all ghost paths
        ArrayList<ArrayList<Integer>> ghostPathsToPacMan = new ArrayList<ArrayList<Integer>>(gs.getNumActiveGhosts());
        ArrayList<ArrayList<Integer>> ghostJunctionsToPacMan = new ArrayList<ArrayList<Integer>>(gs.getNumActiveGhosts());
        for (int i = 0; i < gs.getNumActiveGhosts(); i++) {
            if (gs.isGhostThreat(i)) {
                //System.out.println(Arrays.toString(gs.getGhostLairTimes())+Arrays.toString(gs.getThreatGhostLocations()));
                //int[] temp = gs.getGhostPath(i, current);
                int[] temp = gs.getAllGhostPathNodes(i, current);

                ArrayList<Integer> path = new ArrayList<Integer>(temp.length);
                ArrayList<Integer> js = new ArrayList<Integer>(temp.length);
                for (Integer x : temp) {
                    if (gs.isJunction(x)) {
                        js.add(x);
                    }
                    path.add(x);
                }
                ghostJunctionsToPacMan.add(js);
                ghostPathsToPacMan.add(path);
                if (CommonConstants.watch && draw) {
                    gs.addPoints(Color.YELLOW, temp);
                }
            } else {
                // So spacing works out
                ghostJunctionsToPacMan.add(null);
                ghostPathsToPacMan.add(null);
            }
        }

        // Get all escape nodes
        ArrayList<Integer> escapeNodes = new ArrayList<Integer>(junctions.length);
        for (Integer j : junctions) {
            if (j == current || j == lastNodeVisited) { // Can't escape to current location or previous escape node
                continue;
            }
            boolean[] neighbourCanEscape = new boolean[4];
            int[] junctionNeighbors = gs.neighbors(j);
            for (int i = 0; i < neighbourCanEscape.length; i++) {
                neighbourCanEscape[i] = junctionNeighbors[i] != -1;
            }
            for (int k = 0; k < gs.getNumActiveGhosts(); k++) {
                if (gs.isGhostThreat(k)) { // Ghost is a threat
                    boolean junctionOnThisPath = ghostJunctionsToPacMan.get(k).contains(j);
                    if (junctionOnThisPath) {
                        for (int i = 0; i < neighbourCanEscape.length; i++) {
                            boolean neighbourInPath = ghostPathsToPacMan.get(k).contains(junctionNeighbors[i]);
                            neighbourCanEscape[i] = neighbourCanEscape[i] && !neighbourInPath;
                        }
                    }
                }
            }
            for (int i = 0; i < neighbourCanEscape.length; i++) {
                if (neighbourCanEscape[i]) {
                    assert gs.nodeInMaze(j) : "Node " + j + " is not in maze " + gs.getMazeIndex() + ". escapeNodes: " + escapeNodes;
                    escapeNodes.add(j);
                    break;
                }
            }
        }

        addExtraNodes(escapeNodes, current, gs);

        savedNodes = new int[escapeNodes.size()];
        for (int i = 0; i < savedNodes.length; i++) {
            savedNodes[i] = escapeNodes.get(i);
        }

        removeLastNodeVisited(current, junctions, gs);

        if (CommonConstants.watch && draw) {
            gs.addPoints(Color.RED, savedNodes);
        }
    }

    @Override
    public NodeCollection copy() {
        return new EscapeNodes(this);
    }
}
