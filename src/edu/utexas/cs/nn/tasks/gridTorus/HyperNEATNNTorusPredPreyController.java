package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * Controller for pred/prey that uses hyperNEAT instead of standard inputs
 *
 * @author gillespl
 *
 */
public class HyperNEATNNTorusPredPreyController extends NNTorusPredPreyController {

    public static final int SUBSTRATE_UP_INDEX = 1;
    public static final int SUBSTRATE_LEFT_INDEX = 3;
    public static final int SUBSTRATE_NOTHING_INDEX = 4;
    public static final int SUBSTRATE_RIGHT_INDEX = 5;
    public static final int SUBSTRATE_DOWN_INDEX = 7;

    public static final int NUM_OUTPUTS_WITH_NO_ACTION = 5;

    private final int numOutputs;

    public HyperNEATNNTorusPredPreyController(Network nn, boolean isPredator) {
        super(nn, isPredator);
        numOutputs = isPredator ? predatorActions().length : preyActions().length;
    }

    @Override
    public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
        double[] inputs = inputs();
        double[] outputs = nn.process(inputs);
        double[] modifiedOutputs = mapSubstrateOutputsToStandardOutputs(outputs);
        // Assume one output for each direction
        return isPredator ? predatorActions()[StatisticsUtilities.argmax(modifiedOutputs)] : preyActions()[StatisticsUtilities.argmax(modifiedOutputs)];
    }

    public double[] mapSubstrateOutputsToStandardOutputs(double[] outputs) {
        double[] modifiedOutputs = new double[numOutputs];
        if (numOutputs == NUM_OUTPUTS_WITH_NO_ACTION) {
            modifiedOutputs[NOTHING_INDEX] = outputs[SUBSTRATE_NOTHING_INDEX];
        }
        modifiedOutputs[TorusPredPreyController.UP_INDEX] = outputs[SUBSTRATE_UP_INDEX];
        modifiedOutputs[TorusPredPreyController.RIGHT_INDEX] = outputs[SUBSTRATE_RIGHT_INDEX];
        modifiedOutputs[TorusPredPreyController.DOWN_INDEX] = outputs[SUBSTRATE_DOWN_INDEX];
        modifiedOutputs[TorusPredPreyController.LEFT_INDEX] = outputs[SUBSTRATE_LEFT_INDEX];
        return modifiedOutputs;
    }

    public double[] inputs() {
        HyperNEATTask hnt = (HyperNEATTask) MMNEAT.task;
        double[] inputs = hnt.getSubstrateInputs(hnt.getSubstrateInformation());
        return inputs;
    }
}
