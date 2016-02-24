/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.ghosts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.util.Comparator;

/**
 * Sort ghosts based on proximity to pacman, with the option to focus only on
 * edible or non-edible ghosts
 *
 * @author Jacob Schrum
 */
public class GhostComparator implements Comparator<Integer> {

    private final GameFacade gs;
    private final int current;
    private final int sign;
    private final boolean proximityOnly;

    public GhostComparator(GameFacade gs, boolean edibleClose, boolean proximityOnly) {
        this.gs = gs;
        this.current = gs.getPacmanCurrentNodeIndex();
        this.sign = edibleClose ? 1 : -1;
        this.proximityOnly = proximityOnly;
    }

    public int compare(Integer o1, Integer o2) {
        if (!proximityOnly && gs.isGhostEdible(o1) && !gs.isGhostEdible(o2)) {
            return -1 * sign;
        } else if (!proximityOnly && !gs.isGhostEdible(o1) && gs.isGhostEdible(o2)) {
            return 1 * sign;
        } else {
            double o1Dist = gs.getPathDistance(current, gs.getGhostCurrentNodeIndex(o1));
            double o2Dist = gs.getPathDistance(current, gs.getGhostCurrentNodeIndex(o2));
            if (o2Dist == -1 && o1Dist > -1) {
                return -1;
            } else if (o1Dist == -1 && o2Dist > -1) {
                return 1;
            }
            return (int) Math.signum(o1Dist - o2Dist);
        }
    }
}
