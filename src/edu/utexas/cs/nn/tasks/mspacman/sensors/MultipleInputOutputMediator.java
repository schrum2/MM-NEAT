package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 * Combine any set of mediators into one
 *
 * @author Jacob Schrum
 */
public class MultipleInputOutputMediator extends MsPacManControllerInputOutputMediator {

	protected MsPacManControllerInputOutputMediator[] mediators;

	/**
	 * constructor to obtain the list of mediators in order to later combine
	 * them into one
	 * 
	 * @param mediators,
	 *            array of MsPacManControllerInputOutputMediator's
	 */
	public MultipleInputOutputMediator(MsPacManControllerInputOutputMediator[] mediators) {
		this.mediators = mediators;
	}

	@Override
	/**
	 * update the state of the mediator given the game state
	 * 
	 * @param gs,
	 *            instance of game
	 */
	public void mediatorStateUpdate(GameFacade gs) {
		for (int i = 0; i < mediators.length; i++) {
			mediators[i].mediatorStateUpdate(gs);
		}
	}

	@Override
	/**
	 * clear and reset each MsPacManControllerInputOutputMediator the array of
	 * mediators
	 */
	public void reset() {
		for (int i = 0; i < mediators.length; i++) {
			mediators[i].reset();
		}
	}

	@Override
	/**
	 * retrieves and returns the sensor values based on all of the different
	 * mediators provided/requested (combined into one)
	 * 
	 * @param gs,
	 *            the game instance
	 * @param currentDir,
	 *            the current direction
	 * @return the sensor inputs
	 */
	public double[] getInputs(GameFacade gs, int currentDir) {
		double[] inputs = new double[numIn()];
		int in = 0;
		for (int i = 0; i < mediators.length; i++) {
			double[] subIn = mediators[i].getInputs(gs, currentDir);
			for (int j = 0; j < subIn.length; j++) {
				inputs[in++] = subIn[j];
			}
		}
		return inputs;
	}

	@Override
	/**
	 * retrieves and returns the sensor labels based on all of the different
	 * sensor blocks provided/requested
	 */
	public String[] sensorLabels() {
		String[] labels = new String[numIn()];
		int in = 0;
		for (int i = 0; i < mediators.length; i++) {
			String[] subLabels = mediators[i].sensorLabels();
			for (int j = 0; j < subLabels.length; j++) {
				labels[in++] = i + ":" + subLabels[j];
			}
		}
		return labels;
	}

	@Override
	/**
	 * find and return the number of inputs based on the number of inputs in
	 * each mediator (finds the sum)
	 * 
	 * @return number of inputs
	 */
	public int numIn() {
		int in = 0;
		for (int i = 0; i < mediators.length; i++) {
			int subIn = mediators[i].numIn();
			for (int j = 0; j < subIn; j++) {
				in++;
			}
		}
		return in;
	}
}
