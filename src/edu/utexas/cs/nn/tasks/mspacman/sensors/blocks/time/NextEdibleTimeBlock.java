package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import pacman.game.Constants;

/**
 * How long ghosts will be edible for the next time a power pill is eaten. Helps
 * pacman be aware of fact that in higher levels, the edible time is shorter
 *
 * @author Jacob Schrum
 */
public class NextEdibleTimeBlock extends MsPacManSensorBlock {

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        inputs[in++] = gf.getNextEdibleTime() * 1.0 / Constants.EDIBLE_TIME;
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "Next Edible Time";
        return in;
    }

    public int numberAdded() {
        return 1;
    }
}
