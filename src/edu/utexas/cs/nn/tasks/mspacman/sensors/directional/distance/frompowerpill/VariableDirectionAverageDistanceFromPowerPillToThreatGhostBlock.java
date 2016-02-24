/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.directional.distance.frompowerpill;

import edu.utexas.cs.nn.util.stats.Average;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionAverageDistanceFromPowerPillToThreatGhostBlock extends VariableDirectionDistanceFromPowerPillToThreatGhostBlock {
    
    public VariableDirectionAverageDistanceFromPowerPillToThreatGhostBlock(int dir) {
        super(dir, new Average());
    }
}
