/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.util.stats.Average;

/**
 *
 * @author Jacob Schrum
 */
public abstract class AverageDistanceBlock extends StatDistanceBlock {

    public AverageDistanceBlock() {
        super(new Average(), GameFacade.MAX_DISTANCE);
    }
}
