/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public abstract class BooleanSensorBlock extends MsPacManSensorBlock {

    private final int absence;

    public BooleanSensorBlock() {
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    public int incorporateSensors(double[] inputs, int startPoint, GameFacade gf, int lastDirection) {
        inputs[startPoint++] = predicate(gf, lastDirection) ? 1 : absence;
        return startPoint;
    }

    public int incorporateLabels(String[] labels, int startPoint) {
        labels[startPoint++] = senseLabel();
        return startPoint;
    }

    public int numberAdded() {
        return 1;
    }

    public abstract String senseLabel();

    public abstract boolean predicate(GameFacade gf, int lastDirection);
}
