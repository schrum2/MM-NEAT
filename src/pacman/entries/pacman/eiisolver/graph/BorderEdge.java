package pacman.entries.pacman.eiisolver.graph;

import pacman.game.Constants.MOVE;

/**
 * An edge that is on the border between pacman's and the ghosts' territory
 *
 * @author louis
 *
 */
public class BorderEdge {

    public BigEdge edge;
    public Node pacmanJunction;
    /**
     * the junction that is closer to the ghosts
     */
    public Node ghostJunction;
    /**
     * First move from ghostJunction towards pacman
     */
    public MOVE firstMoveFromGhost;
    /**
     * pacman distance to ghost junction
     */
    public int pacmanDist;
    public int[] ghostDist = new int[4];
    /**
     * bit mask of all ghosts that are closer than pacman to ghost junction
     */
    public int closerGhosts;
    public BorderEdge parent;

    /**
     * Returns the number of moves that pacman could wait on ghostJunction
     * (which in reality should be closer to pacman than to any ghost) before
     * being eaten by a ghost.
     */
    public int getPacmanSlack() {
        int shortestGhostDist = ghostDist[0];
        for (int i = 1; i < ghostDist.length; ++i) {
            if (ghostDist[i] < shortestGhostDist) {
                shortestGhostDist = ghostDist[i];
            }
        }
        return shortestGhostDist - pacmanDist - 3;
    }

    public String toString() {
        return "[" + pacmanJunction + "-" + ghostJunction + "], pacmanDist: " + pacmanDist + ", closerGhosts: " + closerGhosts;
    }
}
