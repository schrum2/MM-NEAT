
package edu.utexas.cs.nn.tasks.gridTorus;

import edu.utexas.cs.nn.graphics.DrawingPanel;
/**
 * Imports needed parts to initialize the Controller, as in Torus agent and world, the controller, network, and statistic utilities.
 */
import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.gridTorus.controllers.TorusPredPreyController;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.sensors.TorusPredPreySensorBlock;
import edu.utexas.cs.nn.tasks.gridTorus.sensors.TorusPredatorsByIndexSensorBlock;
import edu.utexas.cs.nn.tasks.gridTorus.sensors.TorusPredatorsByProximitySensorBlock;
import edu.utexas.cs.nn.tasks.gridTorus.sensors.TorusPreyByIndexSensorBlock;
import edu.utexas.cs.nn.tasks.gridTorus.sensors.TorusPreyByProximitySensorBlock;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * The following class
 * extends the normal TorusPredPreyController to allow for a neural
 * network.
 * 
 * @author Jacob Schrum, Gabby Gonzalez, Alex Rollins 
 */
public class NNTorusPredPreyController extends TorusPredPreyController {

	private final TorusPredPreySensorBlock[] sensorBlocks;
	private int numInputs;

	public DrawingPanel networkInputs = null;

	/**
	 * Initializes the network to be used.
	 */
	public final Network nn;
	// true if this agent is a predator
	protected final boolean isPredator;

	/**
	 * Takes in network and connects it to the controller
	 * 
	 * @param nn
	 * @param isPredator
	 */
	public NNTorusPredPreyController(Network nn, boolean isPredator) {
		this.nn = nn;
		this.isPredator = isPredator;

		boolean byProximity = Parameters.parameters.booleanParameter("torusSenseByProximity");

		if (Parameters.parameters.booleanParameter("torusSenseTeammates")) {
			sensorBlocks = new TorusPredPreySensorBlock[] {
					(byProximity ? new TorusPreyByProximitySensorBlock()      : new TorusPreyByIndexSensorBlock()),
					(byProximity ? new TorusPredatorsByProximitySensorBlock() : new TorusPredatorsByIndexSensorBlock()) };
		} else {
			sensorBlocks = new TorusPredPreySensorBlock[] { isPredator
					? (byProximity ? new TorusPreyByProximitySensorBlock()      : new TorusPreyByIndexSensorBlock())
					: (byProximity ? new TorusPredatorsByProximitySensorBlock() : new TorusPredatorsByIndexSensorBlock()) };
		}

		numInputs = 0;
		for (TorusPredPreySensorBlock block : sensorBlocks) {
			numInputs += block.numSensors();
		}

	}

	public int getNumInputs() {
		return numInputs;
	}

	/**
	 * Takes in all agents (me, world, preds, prey) to allow agent me to return
	 * best possible actions
	 * 
	 * @param me
	 *            this agent
	 * @param world
	 *            torus grid world
	 * @param preds
	 *            predator agents
	 * @param prey
	 *            prey agents
	 * @return actions array
	 */
	@Override
	public int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		double[] inputs = inputs(me, world, preds, prey);
		if(Parameters.parameters.booleanParameter("torusInvertSensorInputs"));{
			for(int i = 0; i < inputs.length; i++){
				inputs[i] = MiscUtil.unitInvert(inputs[i]);
			}
		}
		if (networkInputs != null) {
			TWEANN.inputPanel = networkInputs;
		}
		double[] outputs = nn.process(inputs);
		// Assume one output for each direction
		return isPredator ? predatorActions()[StatisticsUtilities.argmax(outputs)]
				: preyActions()[StatisticsUtilities.argmax(outputs)];
	}

	/**
	 * gets the offsets from this agent to all given agents
	 * 
	 * @param me
	 *            this agent
	 * @param world
	 *            torus grid world
	 * @param agents
	 *            other agents
	 * @return the offsets to the other agents provided
	 */
	public static double[] getAgentOffsets(TorusAgent me, TorusWorld world, TorusAgent[] agents) {
		double[] agentInputs = new double[agents.length * 2];
		for (int i = 0; i < agents.length; i++) {
			agentInputs[(2 * i)] = me.shortestXOffset(agents[i]) / (1.0 * world.width());
			agentInputs[(2 * i) + 1] = me.shortestYOffset(agents[i]) / (1.0 * world.height());
		}
		return agentInputs;
	}

	/**
	 * Calculates inputs for the neural network in order to figure what action
	 * to take in getAction.
	 * 
	 * @param me
	 *            this agent
	 * @param world
	 *            torus grid world
	 * @param preds
	 *            predator agents
	 * @param prey
	 *            prey agents
	 * @return inputs for the network
	 */
	public double[] inputs(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		double[] inputs = new double[numInputs];
		int startPosition = 0;
		for (TorusPredPreySensorBlock block : sensorBlocks) {
			System.arraycopy(block.sensorValues(me, world, preds, prey), 0, inputs, startPosition, block.numSensors());
			startPosition += block.numSensors();
		}
		return inputs;
	}

	/**
	 * Sets up the sensor labels for sensors to be used in network
	 * visualization.
	 * 
	 * @param numAgents
	 *            number of agents to sense
	 * @param type
	 *            is the type of genotype (predator or a prey) that will be
	 *            sensed
	 * @return sensor labels for this genotype
	 */
	public static String[] sensorLabels(int numAgents, String type) {
		String[] result = new String[numAgents * 2];
		for (int i = 0; i < numAgents; i++) {
			result[(2 * i)] = "X Offset to " + type + " " + i;
			result[(2 * i) + 1] = "Y Offset to " + type + " " + i;
		}
		return result;
	}

	/**
	 * Finds the sensor labels for this agent and returns them in an array of
	 * Strings The sensor labels will depend upon which type of agent this is
	 * (which agent is being evolved) and if the agent is allowed to
	 * senseTeammates in addition to enemies.
	 * 
	 * @return the sensorLabels for this agent
	 */
	public String[] sensorLabels() {
		String[] labels = new String[numInputs];
		int startPosition = 0;
		for (TorusPredPreySensorBlock block : sensorBlocks) {
			System.arraycopy(block.sensorLabels(), 0, labels, startPosition, block.numSensors());
			startPosition += block.numSensors();
		}
		return labels;
	}

}
