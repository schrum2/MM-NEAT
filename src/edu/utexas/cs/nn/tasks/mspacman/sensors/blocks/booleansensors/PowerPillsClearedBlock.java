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
public class PowerPillsClearedBlock extends BooleanSensorBlock {

    @Override
    public String senseLabel() {
        return "Power Pills Cleared";
    }

    @Override
    public boolean predicate(GameFacade gf, int lastDirection) {
        return gf.getNumActivePowerPills() == 0;
    }
}
