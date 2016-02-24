/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class GhostReversalBlock extends BooleanSensorBlock {

    @Override
    public String senseLabel() {
        return "Ghosts Reversed?";
    }

    @Override
    public boolean predicate(GameFacade gf, int lastDirection) {
        return gf.ghostReversal();
    }
    
    // Don't need caching for simple computations
    @Override
    public int retrieveSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        return incorporateSensors(inputs, in, gf, lastDirection);
    }
}
