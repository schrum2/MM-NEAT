/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill;

import edu.utexas.cs.nn.util.stats.Min;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionMinDistanceFromPowerPillToThreatGhostBlock extends VariableDirectionDistanceFromPowerPillToThreatGhostBlock {
    
    public VariableDirectionMinDistanceFromPowerPillToThreatGhostBlock(int dir) {
        super(dir, new Min());
    }
}
