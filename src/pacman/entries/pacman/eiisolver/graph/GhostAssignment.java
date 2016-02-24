package pacman.entries.pacman.eiisolver.graph;

import java.util.*;

/**
 * Calculates how ghosts should be assigned to border edges to maximize the
 * number of edges covered by the ghosts.
 *
 * @author louis
 *
 */
public class GhostAssignment {

    public int bestNrAssignedGhosts;
    /**
     * Contains for every ghost the border index to which this ghost should
     * move, -1 if not assigned
     */
    public int[] bestAssignedBorders = new int[4];
    private int nrAssignedGhosts;
    private int[] assignedBorders = new int[4];

    /**
     * Count the number of set bits in a byte;
     *
     * @param x the byte to have its bits counted
     * @returns the number of bits set in x
     * @author Tim Tyler tt@iname.com
     */
    public static int bitCount(int y) {
        int temp = 0x55;
        y = (y & temp) + (y >>> 1 & temp);
        temp = 0x33;
        y = (y & temp) + (y >>> 2 & temp);
        return (y & 0x07) + (y >>> 4);
    }

    public static int lowestSetGhostBit(int x) {
        if (x == 1) {
            return 0;
        }
        if (x == 2) {
            return 1;
        }
        if (x == 4) {
            return 2;
        }
        return 3;
    }

    public void calcAssignment(BorderEdge[] borders, int nrBorders) {
        for (int i = 0; i < 4; ++i) {
            assignedBorders[i] = -1;
        }
        nrAssignedGhosts = 0;
        int ghostMask = 0xf;
        int edgeMask = (1 << nrBorders) - 1;
        /**
         * search for edges that are only covered by 1 single ghost
         */
        for (int i = 0; i < nrBorders; ++i) {
            int edgeM = 1 << i;
            if ((edgeMask & edgeM) != 0) {
                int mask = borders[i].closerGhosts;
                int h = mask;
                if (bitCount(h) == 1) {
                    int ghost = lowestSetGhostBit(h);
                    if (assignedBorders[ghost] < 0) {
                        ++nrAssignedGhosts;
                        assignedBorders[ghost] = i;
                        ghostMask &= ~h;
                        edgeMask &= ~edgeM;
                    }
                }
            }
        }
        if (ghostMask != 0 || edgeMask != 0) {
            /**
             * search for edges that are only covered by 2 single ghosts
             */
            boolean foundAssignment = false;
            do {
                foundAssignment = false;
                for (int i = 0; i < nrBorders; ++i) {
                    int edgeM = 1 << i;
                    if ((edgeMask & edgeM) != 0) {
                        int mask = borders[i].closerGhosts;
                        int h = mask & ghostMask;
                        if (bitCount(h) == 1) {
                            int ghost = lowestSetGhostBit(h);
                            if (assignedBorders[ghost] < 0) {
                                ++nrAssignedGhosts;
                            }
                            assignedBorders[ghost] = i;
                            ghostMask &= ~h;
                            edgeMask &= ~edgeM;
                            foundAssignment = true;
                        }
                    }
                }
            } while (foundAssignment);
        }
        bestNrAssignedGhosts = nrAssignedGhosts;
        for (int i = 0; i < assignedBorders.length; ++i) {
            bestAssignedBorders[i] = assignedBorders[i];
        }
        match(borders, nrBorders, ghostMask, edgeMask, nrAssignedGhosts);
    }

    private int match(BorderEdge[] borders, int nrBorders, int ghostMask, int edgeMask, int nrAssigned) {
        if (ghostMask == 0 || edgeMask == 0) {
            return nrAssigned;
        }
        int bestResult = nrAssigned;
        for (int i = 0; i < nrBorders; ++i) {
            if ((edgeMask & (1 << i)) != 0) {
                for (int j = 0; j < 4; ++j) {
                    if ((ghostMask & (1 << j)) != 0) {
                        if ((borders[i].closerGhosts & (1 << j)) != 0) {
                            edgeMask &= ~(1 << i);
                            ghostMask &= ~(1 << j);
                            assignedBorders[j] = i;
                            int result = match(borders, nrBorders, ghostMask, edgeMask, nrAssigned + 1);
                            if (result > bestNrAssignedGhosts) {
                                bestNrAssignedGhosts = result;
                                for (int b = 0; b < assignedBorders.length; ++b) {
                                    bestAssignedBorders[b] = assignedBorders[b];
                                }
                            }
                            assignedBorders[j] = -1;
                            edgeMask |= (1 << i);
                            ghostMask |= (1 << j);
                        }
                    }
                }
            }
        }
        return bestResult;
    }

    @Override
    public String toString() {
        return "GhostAssignment [bestNrAssignedGhosts=" + bestNrAssignedGhosts
                + ", bestAssignedBorders="
                + Arrays.toString(bestAssignedBorders) + "]";
    }
}
