package edu.southwestern.tasks.mspacman.sensors;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.data.NodeCollection;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.ClassCreation;

/**
 * This is the parent class of the controller sensor blocks. Defines the sensor
 * blocks, inputs and their components
 * 
 * @author Jacob Schrum
 */
public abstract class MsPacManControllerInputOutputMediator {

	protected final int absence;
	// Schrum: 6/11/18: Is it ok for this to be static when mediators are copied for each new NN?
	public static NodeCollection escapeNodes = null;
	private final boolean evolveNetworkSelector;
	private final boolean externalPreferenceNeurons;

	/**
	 * Constructor to set some global variables based on command line parameters
	 */
	public MsPacManControllerInputOutputMediator() {
		if (escapeNodes == null) {
			try {
				escapeNodes = (NodeCollection) ClassCreation.createObject("pacmanEscapeNodeCollection");
			} catch (NoSuchMethodException ex) {
				System.out.println("Cannot initialize escape nodes");
				System.exit(1);
			}
		}
		externalPreferenceNeurons = Parameters.parameters.booleanParameter("externalPreferenceNeurons");
		absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
		evolveNetworkSelector = Parameters.parameters.booleanParameter("evolveNetworkSelector");
	}

	/**
	 * update the nodes in the escapeNodes node collection to include the pacman
	 * node of the current game
	 * 
	 * @param gs
	 */
	public void mediatorStateUpdate(GameFacade gs) {
		int current = gs.getPacmanCurrentNodeIndex();
		assert current != -1 : "current is -1 in MsPacManControllerInputOutputMediator.mediatorStateUpdate()";
		escapeNodes.updateNodes(gs, current);
	}

	/**
	 * returns the inputs in a double array
	 * 
	 * @param gs
	 * @param currentDir
	 * @return sensor inputs
	 */
	public abstract double[] getInputs(GameFacade gs, final int currentDir);

	/**
	 * clears the node collection and resets its visited node reference
	 */
	public void reset() {
		escapeNodes.reset();
	}

	/**
	 * returns the labels in a string array
	 * 
	 * @return sensor labels
	 */
	public abstract String[] sensorLabels();

	/**
	 * returns the output labels in a string array
	 * 
	 * @return output labels
	 */
	public String[] outputLabels() {
		if (evolveNetworkSelector) {
			return new String[] { "Ghost Network", "Pill Network" };
		} else if (CommonConstants.relativePacmanDirections) {
			return new String[] { "Forward", "Turn Right", "Reverse", "Turn Left" };
		} else {
			return new String[] { "Up", "Right", "Down", "Left" };
		}
	}

	/**
	 * returns number of outputs as an integer
	 * 
	 * @return number of outputs
	 */
	public int numOut() {
		// 2 is for turn/thrust
		return evolveNetworkSelector ? MMNEAT.modesToTrack : GameFacade.NUM_DIRS + (externalPreferenceNeurons ? 1 : 0);
	}

	/**
	 * returns number of inputs as an integer
	 * 
	 * @return number of inputs
	 */
	public abstract int numIn();

	/**
	 * There needs to be a way to copy the mediators in order to use multiple
	 * parallel threads for evaluation, since each controller needs to use a different mediator.
	 * In particular, the mediator has internal state ... or rather, some of the sensor blocks do.
	 * @return Copy of the mediator
	 */
	public abstract MsPacManControllerInputOutputMediator copy();
}
