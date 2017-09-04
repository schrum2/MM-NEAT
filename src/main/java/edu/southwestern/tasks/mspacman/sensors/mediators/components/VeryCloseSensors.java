/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.mediators.components;

import edu.southwestern.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToEdibleGhost;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToPill;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToPowerPill;
import edu.southwestern.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToThreatGhost;

/**
 *
 * @author Jacob Schrum
 */
public class VeryCloseSensors extends BlockLoadedInputOutputMediator {

	public VeryCloseSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPills) {
		super();
		blocks.add(new IsCloseToThreatGhost());
		if (sensePills) {
			blocks.add(new IsCloseToPill());
		}
		if (senseEdibleGhosts) {
			blocks.add(new IsCloseToEdibleGhost());
		}
		if (sensePowerPills) {
			blocks.add(new IsCloseToPowerPill());
		}
	}
}