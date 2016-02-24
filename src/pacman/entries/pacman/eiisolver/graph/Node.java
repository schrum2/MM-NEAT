package pacman.entries.pacman.eiisolver.graph;

import pacman.game.*;
import static pacman.game.Constants.*;

/**
 * There is one InternalNode for every non-junction Node.
 *
 * @author louis
 *
 */
public class Node {
    //----------------------------------------------------------------------------
    // APPLICABLE TO ALL NODES
    //----------------------------------------------------------------------------

    /**
     * Index of this node in the current maze
     */
    public int index;
    /**
     * The number of neighbours that this node has
     */
    public int nrNeighbours;
    /**
     * Neighbours of this node
     */
    public int[] neighbours;
    /**
     * The move that should be taken to get to a neighbour, i.e.
     * neighbourMoves[0] takes you to neighbours[0]
     */
    public MOVE[] neighbourMoves;
    /**
     * x, y coordinates
     */
    public int x, y;
    /**
     * If onlyGhostMove[lastMove.ordinal()] == -1, then there are several ghost
     * moves possible, but otherwise onlyGhostMove[lastMove.ordinal()] contains
     * the index in neighbours of the only possible ghost move.
     */
    public int[] onlyGhostMove = new int[MOVE.values().length];
    //----------------------------------------------------------------------------
    // APPLICABLE TO INTERNAL (NON-JUNCTION) NODES
    //----------------------------------------------------------------------------
    /**
     * BigEdge on which this node resides (only applicable for internal nodes)
     */
    public BigEdge edge;
    /**
     * distToJunction[0] is pacman distance to BigEdge.endpoints[0] (only
     * applicable for internal nodes)
     */
    public int[] distToJunction;
    /**
     * Pacman distance to closest junction
     */
    public int distToClosestJunction;
    /**
     * True if we can skip pacman turn-around moves at this place
     */
    public boolean skipOpposite;
    /**
     * Index of this node on edge.internalNodes
     */
    public int edgeIndex;
    /**
     * if we move forward through the edge (i.e. along increasing indices in
     * edge), then the last move to get to this node was lastMoveIfForward
     */
    MOVE lastMoveIfForward;
    /**
     * Move from this node to next node on the edge
     */
    MOVE moveToNextNode;
    /**
     * Move from this node to previous node on the edge
     */
    MOVE moveToPrevNode;
    //----------------------------------------------------------------------------
    // APPLICABLE TO INTERNAL JUNCTION NODES
    //----------------------------------------------------------------------------
    /**
     * Junction index of this node in JunctionGraph.junctionNodes (only
     * applicable for junction nodes)
     */
    public int junctionIndex = -1;
    /**
     * all edges connected to this node (only applicable for junction nodes)
     */
    public BigEdge[] edges = new BigEdge[4];
    /**
     * The number of edges connected to this node (only applicable for junction
     * nodes)
     */
    public int nrEdges;

    public boolean isJunction() {
        return nrNeighbours != 2;
    }

    /**
     * Returns the next junction, given that we made lastMove to arrive to this
     * node.
     *
     * @param lastMove
     * @return
     */
    public int getNextJunction(MOVE lastMove) {
        int index = 0;
        if (lastMove == lastMoveIfForward) {
            index = 1;
        }
        return edge.endpoints[index].index;
    }

    /**
     * Returns the distance to the next junction, given that we made lastMove to
     * arrive to this node.
     *
     * @param lastMove
     * @return
     */
    public int getDistToNextJunction(MOVE lastMove) {
        int index = 0;
        if (lastMove == lastMoveIfForward) {
            index = 1;
        }
        return distToJunction[index];
    }

    /**
     * Given that the last move to get here, what will be the move that leads to
     * the next junction? This method provides the answer. NOTE: this node must
     * be a non-junction node
     */
    public MOVE getLastMoveToNextJunction(MOVE lastMove) {
        if (lastMove == lastMoveIfForward) {
            return edge.internalNodes[edge.internalNodes.length - 1].moveToNextNode;
        } else {
            return edge.internalNodes[1].moveToPrevNode;
        }
    }

    /**
     * Checks if the given node is on the path from this node to the endpoint of
     * this edge, given that we made lastMove to arrive to this node.
     *
     * @param nodeOnEdge
     * @param lastMove
     * @return
     */
    public boolean isOnPath(Node nodeOnEdge, MOVE lastMove) {
        if (lastMove == lastMoveIfForward) {
            return nodeOnEdge.edgeIndex > edgeIndex;
        } else {
            return nodeOnEdge.edgeIndex < edgeIndex;
        }
    }

    /**
     * Returns the pacman distance to the other node that must be on the same
     * edge as this node (and must be an internal node).
     *
     * @param nodeOnEdge
     * @return
     */
    public int distOnEdge(Node nodeOnEdge) {
        return Math.abs(edgeIndex - nodeOnEdge.edgeIndex);
    }

    /**
     * Sets onlyMove.
     */
    public void calcOnlyMove() {
        for (int i = 0; i < onlyGhostMove.length; ++i) {
            onlyGhostMove[i] = -1;
        }
        if (!isJunction()) {
            for (int i = 0; i < 2; ++i) {
                MOVE lastMove = neighbourMoves[1 - i].opposite();
                onlyGhostMove[lastMove.ordinal()] = i;
            }
        }
    }

    @Override
    public String toString() {
        return "(" + y + "," + x + ")";
    }
}
