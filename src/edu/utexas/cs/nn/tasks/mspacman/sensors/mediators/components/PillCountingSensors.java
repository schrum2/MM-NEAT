package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.PowerPillsClearedBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PillsRemainingBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.PowerPillsRemainingBlock;

/**
 * Counts of pills
 *
 * @author Jacob Schrum
 */
public class PillCountingSensors extends BlockLoadedInputOutputMediator {

    public PillCountingSensors(boolean pillsPresent, boolean canEatGhosts) {
        super();
        if (pillsPresent) {
            blocks.add(new PillsRemainingBlock(true, false));
        }
        if (canEatGhosts) {
            blocks.add(new PowerPillsRemainingBlock(true, false));
            blocks.add(new PowerPillsClearedBlock()); // redundant?
        }
    }
}
