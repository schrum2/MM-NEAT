package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class CountLairGhostsBlock extends TargetPortionRemainingBlock {

    public CountLairGhostsBlock(boolean portion, boolean inverse) {
        super(portion, inverse);
    }

    @Override
    public int getTargetMax(GameFacade gf) {
        return Constants.NUM_GHOSTS;
    }

    @Override
    public int getTargetCurrent(GameFacade gf) {
        return gf.getNumberOfLairGhosts();
    }

    @Override
    public String getTargetType() {
        return "Lair Ghost";
    }
}
