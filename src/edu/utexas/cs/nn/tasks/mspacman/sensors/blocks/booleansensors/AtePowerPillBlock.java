package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class AtePowerPillBlock extends BooleanSensorBlock {

    @Override
    public String senseLabel() {
        return "Just Ate Power Pill";
    }

    @Override
    public boolean predicate(GameFacade gf, int lastDirection) {
        return gf.justAtePowerPill();
    }
}
