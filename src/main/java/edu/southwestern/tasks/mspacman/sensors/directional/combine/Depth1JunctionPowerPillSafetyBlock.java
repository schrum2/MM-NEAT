/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.combine;

import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToJunctionThanThreatGhostBlock;
import edu.southwestern.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToPowerPillThanThreatGhostBlock;

/**
 *
 * @author Jacob Schrum
 */
public class Depth1JunctionPowerPillSafetyBlock extends VariableDirectionMaxBlock {

	public Depth1JunctionPowerPillSafetyBlock() {
		super(-1, new VariableDirectionBlock[] { new VariableDirectionCloserToJunctionThanThreatGhostBlock(-1),
				new VariableDirectionCloserToPowerPillThanThreatGhostBlock(-1) });
	}
}
