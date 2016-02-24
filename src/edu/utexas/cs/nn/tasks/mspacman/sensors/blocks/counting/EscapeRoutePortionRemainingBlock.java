package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 *
 * @author Jacob Schrum
 */
public class EscapeRoutePortionRemainingBlock extends TargetPortionRemainingBlock {

    public EscapeRoutePortionRemainingBlock(boolean portion, boolean inverse) {
        super(portion, inverse);
    }

    @Override
    public int getTargetMax(GameFacade gf) {
        int[] neighbors = gf.neighbors(gf.getPacmanCurrentNodeIndex());
        return neighbors.length - ArrayUtil.countOccurrences(-1, neighbors);
    }

    @Override
    public int getTargetCurrent(GameFacade gf) {
        final int current = gf.getPacmanCurrentNodeIndex();
        int[] neighbors = gf.neighbors(current);
        for (int j = 0; j < gf.getNumActiveGhosts(); j++) {
            int[] ghostPath = gf.getGhostPath(j, current);
            for (int i = 0; i < neighbors.length; i++) {
                if (neighbors[i] != -1) {
                    boolean incoming = 0 < ArrayUtil.countOccurrences(neighbors[i], ghostPath);
                    if (incoming) {
                        neighbors[i] = -1;
                    }
                }
            }
        }
        return neighbors.length - ArrayUtil.countOccurrences(-1, neighbors);
    }

    @Override
    public String getTargetType() {
        return "Escape Route";
    }
}
