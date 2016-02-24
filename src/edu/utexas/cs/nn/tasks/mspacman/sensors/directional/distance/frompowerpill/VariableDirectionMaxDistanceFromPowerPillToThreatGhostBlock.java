/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill;

import edu.utexas.cs.nn.util.stats.Max;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionMaxDistanceFromPowerPillToThreatGhostBlock extends VariableDirectionDistanceFromPowerPillToThreatGhostBlock {
    
    public VariableDirectionMaxDistanceFromPowerPillToThreatGhostBlock(int dir) {
        super(dir, new Max());
    }
}
