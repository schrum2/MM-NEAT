package edu.utexas.cs.nn.tasks.mspacman.data;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Data representing collection of junction nodes, excluding the one most
 * recently visited or currently occupied by pacman.
 *
 * @author Jacob Schrum
 */
public class JunctionNodes extends NodeCollection {

    public JunctionNodes() {
        super();
    }

    public JunctionNodes(JunctionNodes original) {
        super(original);
    }

    public void updateNodes(GameFacade gs, int current, boolean draw) {
        if (gs.isJunction(current)) {
            //System.out.println("Remove " + current);
            lastNodeVisited = current;
        }
        final int[] junctions = gs.getJunctionIndices();

        // Always update
        // Get most junctions, and then some
        ArrayList<Integer> escapeNodes = new ArrayList<Integer>(junctions.length);
        for (Integer j : junctions) {
            if (j == current || j == lastNodeVisited) { // Can't escape to current location or previous escape node
                continue;
            }
            assert gs.nodeInMaze(j) : "Node " + j + " is not in maze " + gs.getMazeIndex() + ". escapeNodes: " + escapeNodes;
            escapeNodes.add(j);
        }

        addExtraNodes(escapeNodes, current, gs);

        savedNodes = new int[escapeNodes.size()];
        for (int i = 0; i < savedNodes.length; i++) {
            savedNodes[i] = escapeNodes.get(i);
        }

        removeLastNodeVisited(current, junctions, gs);

//        if (CommonConstants.watch && draw) {
//            gs.addPoints(Color.RED, savedNodes);
//        }
    }

    @Override
    public NodeCollection copy() {
        return new JunctionNodes(this);
    }
}
