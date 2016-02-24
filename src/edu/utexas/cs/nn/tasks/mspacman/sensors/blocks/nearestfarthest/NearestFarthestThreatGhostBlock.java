package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class NearestFarthestThreatGhostBlock extends NearestFarthestIndexBlock {

    public NearestFarthestThreatGhostBlock(boolean nearest) {
        super(nearest);
    }

    public NearestFarthestThreatGhostBlock(boolean nearest, int absence) {
        super(nearest, absence);
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        ArrayList<Integer> ghostPositions = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            if (gf.isGhostThreat(i)) {
                ghostPositions.add(gf.getGhostCurrentNodeIndex(i));
            } else if (!gf.isGhostEdible(i) && gf.getGhostLairTime(i) < GameFacade.DANGEROUS_TIME) {
                ghostPositions.add(gf.getGhostInitialNodeIndex());
            }
        }
        return ArrayUtil.intArrayFromArrayList(ghostPositions);
    }

    @Override
    public String typeOfTarget() {
        return "Threatening Ghost";
    }
}
