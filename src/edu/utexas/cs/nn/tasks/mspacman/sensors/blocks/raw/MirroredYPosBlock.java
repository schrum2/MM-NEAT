package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public class MirroredYPosBlock extends MsPacManSensorBlock {

    public static final int MAX_Y_COORD = 120;

    public int incorporateSensors(double[] inputs, int startPoint, GameFacade gf, int lastDirection) {
        inputs[startPoint++] = (2.0 * gf.getNodeYCoord(gf.getPacmanCurrentNodeIndex()) / MAX_Y_COORD) - 1;
        return startPoint;
    }

    public int incorporateLabels(String[] labels, int startPoint) {
        labels[startPoint++] = "Mirrored Y Coord";
        return startPoint;
    }

    public int numberAdded() {
        return 1;
    }
}
