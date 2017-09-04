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

	/**
	 * constructs this mode selector and sets the class network and mediator
	 * 
	 * @param n,
	 *            network
	 */
	public NetworkModeSelector(T n) {
		this(n, MMNEAT.pacmanInputOutputMediator);
	}

	/**
	 * constructs this mode selector and sets the class network and mediator
	 * 
	 * @param n,
	 *            network
	 * @param mediator
	 */
	public NetworkModeSelector(T n, MsPacManControllerInputOutputMediator mediator) {
		this.n = n;
		this.mediator = mediator;
	}

	/**
	 * A Mode selector which selects modes from a specific number of modes based
	 * on the number of outputs of the network by finding the maximum of the
	 * outputs
	 * 
	 * @return mode
	 */
	public int mode() {
		mediator.mediatorStateUpdate(gs);
		double[] inputs = mediator.getInputs(gs, gs.getPacmanLastMoveMade());
		double[] outputs = n.process(inputs);
		// Choose one network
		return StatisticsUtilities.argmax(outputs);
	}

	/**
	 * returns the number of Modes for this mode selector, which is dependent
	 * upon the number of outputs in the network
	 * 
	 * @return numModes
	 */
	public int numModes() {
		return n.numOutputs();
	}

	@Override
	/**
	 * gets the associated fitness scores with this mode selector
	 * 
	 * @return an int array holding the associated scores. One index per mode,
	 *         which is based off of the number of network outputs
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		for (int i = 0; i < result.length; i++) {
			result[i] = NO_PREFERENCE;
		}
		return result;
	}

	@Override
	/**
	 * defines the reset so that the mediator and network are reset
	 */
	public void reset() {
		mediator.reset();
		n.flush();
	}
}
