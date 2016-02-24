package pacman.entries.pacman.eiisolver.graph;

import pacman.game.Constants.MOVE;

/**
 * An edge between two junction nodes, containing zero or more non-junction
 * nodes.
 *
 * @author louis
 *
 */
public class BigEdge {

    /**
     * Endpoints of the edge (both are junctions)
     */
    public Node[] endpoints = new Node[2];
    // the indices of the internal nodes that are on the big edge
    public Node[] internalNodes;
    /**
     * The distance between the end points
     */
    public int length;
    boolean containsPowerPill;
    /**
     * firstMoveToOtherEnd[0] is first move from endpoints[0] if you go to
     * endpoints[1]
     */
    public MOVE[] firstMoveToOtherEnd = new MOVE[2];
    /**
     * unique id of this edge, can be used as array index in arrays containing
     * all edges
     */
    public int id;

    /**
     * Returns the other junction, given a junction that is an endpoint of this
     * edge.
     *
     * @param junction
     * @return
     */
    public Node getOtherJunction(Node junction) {
        if (endpoints[0] == junction) {
            return endpoints[1];
        }
        return endpoints[0];
    }

    /**
     * Gets the first move to be made to the junction at the one side of the
     * edge in order to get to the given junction
     *
     * @param junction must be one of the end points of this edge
     * @return
     */
    public MOVE getFirstMoveToOtherEnd(Node oppositeJunction) {
        if (oppositeJunction == endpoints[0]) {
            return firstMoveToOtherEnd[1];
        } else {
            return firstMoveToOtherEnd[0];
        }
    }

    /**
     * Gets the first move from the given junction to walk this edge
     *
     * @param junction must be one of the end points of this edge
     * @return
     */
    public MOVE getFirstMove(Node junction) {
        if (junction == endpoints[1]) {
            return firstMoveToOtherEnd[1];
        } else {
            return firstMoveToOtherEnd[0];
        }
    }

    /**
     * Return length from n1 (can be junction or internal node on this edge) to
     * junction (which must be one of the endpoints of this edge).
     *
     * @param n1
     * @param junction
     * @return
     */
    public int getDistanceToJunction(Node n1, Node junction) {
        if (n1 == junction) {
            return 0;
        } else if (n1.isJunction()) {
            return length;
        } else if (junction == endpoints[0]) {
            return n1.edgeIndex + 1;
        } else {
            return length - 1 - n1.edgeIndex;
        }
    }

    @Override
    public String toString() {
        return endpoints[0].toString() + "-" + endpoints[1].toString();
    }
}
