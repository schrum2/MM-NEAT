/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.lookahead.DirectionalEdibleGhostsStaticLookAheadBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.lookahead.DirectionalPillsStaticLookAheadBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.lookahead.DirectionalPowerPillsStaticLookAheadBlock;
import edu.southwestern.tasks.mspacman.sensors.blocks.lookahead.DirectionalThreatGhostsStaticLookAheadBlock;

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
