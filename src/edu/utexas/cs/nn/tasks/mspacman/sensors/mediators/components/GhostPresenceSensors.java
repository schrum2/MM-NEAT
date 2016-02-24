package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.AllThreatsPresentBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.AnyEdibleGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.CountEdibleGhostsBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.counting.CountThreatGhostsBlock;

/**
 * Counts of pills
 *
 * @author Jacob Schrum
 */
public class GhostPresenceSensors extends BlockLoadedInputOutputMediator {

    public GhostPresenceSensors(boolean canEatGhosts) {
        super();
        blocks.add(new CountThreatGhostsBlock(true, false));
        if (canEatGhosts) {
            blocks.add(new CountEdibleGhostsBlock(true, false));
            blocks.add(new AllThreatsPresentBlock());
            blocks.add(new AnyEdibleGhostBlock());
        }
    }
}
