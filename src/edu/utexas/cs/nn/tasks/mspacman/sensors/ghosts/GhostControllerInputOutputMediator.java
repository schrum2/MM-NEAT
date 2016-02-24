package edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public abstract class GhostControllerInputOutputMediator {

    protected final int absence;

    public GhostControllerInputOutputMediator() {
        absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    public abstract double[] getInputs(GameFacade gs, final int ghostIndex);

    public abstract String[] sensorLabels();

    public abstract String[] outputLabels();
    
    public void reset() {};
    
    public abstract int numOut();

    public abstract int numIn();
}
