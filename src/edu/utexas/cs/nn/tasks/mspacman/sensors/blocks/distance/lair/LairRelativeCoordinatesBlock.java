package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance.lair;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.MiscUtil;

/**
 *
 * @author Jacob Schrum
 */
public class LairRelativeCoordinatesBlock extends MsPacManSensorBlock {

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int lairExit = gf.getGhostInitialNodeIndex();
        int lairX = gf.getNodeXCoord(lairExit);
        int lairY = gf.getNodeYCoord(lairExit);

        int current = gf.getPacmanCurrentNodeIndex();
        int pacmanX = gf.getNodeXCoord(current);
        int pacmanY = gf.getNodeYCoord(current);

        inputs[in++] = MiscUtil.scaleAndInvert(pacmanX - lairX, GameFacade.MAX_DISTANCE);
        inputs[in++] = MiscUtil.scaleAndInvert(pacmanY - lairY, GameFacade.MAX_DISTANCE);
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "X From Lair";
        labels[in++] = "Y From Lair";
        return in;
    }

    public int numberAdded() {
        return 2;
    }
}
