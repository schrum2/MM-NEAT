package edu.utexas.cs.nn.tasks.mspacman.objectives;

import edu.utexas.cs.nn.networks.Network;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public class TimeFramesGhostScore<T extends Network> extends TimeFramesScore<T> {

    public TimeFramesGhostScore(int mode) {
        super(mode);
    }

    @Override
    public List<Integer> eatTimes() {
        return g.getGhostEatTimes();
    }
}
