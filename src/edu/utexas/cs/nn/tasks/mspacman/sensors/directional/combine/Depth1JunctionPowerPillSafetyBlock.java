/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.combine;

import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToJunctionThanThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToPowerPillThanThreatGhostBlock;

/**
 *
 * @author Jacob Schrum
 */
public class Depth1JunctionPowerPillSafetyBlock extends VariableDirectionMaxBlock {

    public Depth1JunctionPowerPillSafetyBlock() {
        super(-1,
                new VariableDirectionBlock[]{
                    new VariableDirectionCloserToJunctionThanThreatGhostBlock(-1),
                    new VariableDirectionCloserToPowerPillThanThreatGhostBlock(-1)
                });
    }
}
