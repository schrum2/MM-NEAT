package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * Picks mode to use based on NN output
 *
 * @author Jacob Schrum
 */
public class NetworkModeSelector<T extends Network> extends MsPacManModeSelector {

    private final T n;
    private final MsPacManControllerInputOutputMediator mediator;

    public NetworkModeSelector(T n) {
        this(n, MMNEAT.pacmanInputOutputMediator);
    }

    public NetworkModeSelector(T n, MsPacManControllerInputOutputMediator mediator) {
        this.n = n;
        this.mediator = mediator;
    }

    public int mode() {
        mediator.mediatorStateUpdate(gs);
        double[] inputs = mediator.getInputs(gs, gs.getPacmanLastMoveMade());
        double[] outputs = n.process(inputs);
        // Choose one network
        return StatisticsUtilities.argmax(outputs);
    }

    public int numModes() {
        return n.numOutputs();
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        for (int i = 0; i < result.length; i++) {
            result[i] = NO_PREFERENCE;
        }
        return result;
    }

    @Override
    public void reset() {
        mediator.reset();
        n.flush();
    }
}
