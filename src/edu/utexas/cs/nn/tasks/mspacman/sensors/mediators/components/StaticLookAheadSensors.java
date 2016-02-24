/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.lookahead.DirectionalEdibleGhostsStaticLookAheadBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.lookahead.DirectionalPillsStaticLookAheadBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.lookahead.DirectionalPowerPillsStaticLookAheadBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.lookahead.DirectionalThreatGhostsStaticLookAheadBlock;

/**
 *
 * @author Jacob
 */
public class StaticLookAheadSensors extends BlockLoadedInputOutputMediator {

    public StaticLookAheadSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPills) {
        super();
        blocks.add(new DirectionalThreatGhostsStaticLookAheadBlock());
        if (sensePills) {
            blocks.add(new DirectionalPillsStaticLookAheadBlock());
        }
        if (senseEdibleGhosts) {
            blocks.add(new DirectionalEdibleGhostsStaticLookAheadBlock());
        }
        if (sensePowerPills) {
            blocks.add(new DirectionalPowerPillsStaticLookAheadBlock());
        }
    }
}
