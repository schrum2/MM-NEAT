package edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts.GhostSensorBlock;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public abstract class GhostBlockLoadedInputOutputMediator extends GhostControllerInputOutputMediator {

    public ArrayList<GhostSensorBlock> blocks;
    private int numSensors = 0;

    public GhostBlockLoadedInputOutputMediator() {
        super();
        blocks = new ArrayList<GhostSensorBlock>();
    }

    @Override
    public double[] getInputs(GameFacade gs, int ghostIndex) {
        double[] inputs = new double[numIn()];
        int in = 0;
        for (int i = 0; i < blocks.size(); i++) {
            in = blocks.get(i).incorporateSensors(inputs, in, gs, ghostIndex);
        }
        assert (in == numIn()) : "Improper inputs for Ghost. Only " + in + " inputs: " + Arrays.toString(inputs);
        return inputs;
    }

    @Override
    public String[] sensorLabels() {
        String[] labels = new String[numIn()];
        int in = 0;
        for (int i = 0; i < blocks.size(); i++) {
            in = blocks.get(i).incorporateLabels(labels, in);
        }
        assert (in == numIn()) : "Improper inputs for Ghost. Only " + in + " inputs: " + Arrays.toString(labels);
        return labels;
    }

    /**
     * Save result of calculation and reuse instead of repeating
     *
     * @return
     */
    @Override
    public int numIn() {
        if (numSensors == 0) {
            for (int i = 0; i < blocks.size(); i++) {
                numSensors += blocks.get(i).numberAdded();
            }
        }
        return numSensors;
    }

    @Override
    public void reset() {
        super.reset();
        for (GhostSensorBlock b : blocks) {
            b.reset();
        }
    }
}
