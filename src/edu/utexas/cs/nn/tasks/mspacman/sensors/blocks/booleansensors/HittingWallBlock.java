package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.awt.Color;

/**
 *
 * @author Jacob Schrum
 */
public class HittingWallBlock extends BooleanSensorBlock {

    @Override
    public String senseLabel() {
        return "Hitting Wall";
    }

    @Override
    public boolean predicate(GameFacade gf, int lastDirection) {
        boolean hittingWall = gf.pacmanHittingWall();
        if (CommonConstants.watch && hittingWall) {
            gf.addLines(Color.yellow, gf.getPacmanCurrentNodeIndex(), gf.getGhostInitialNodeIndex());
        }
        return hittingWall;
    }
}
