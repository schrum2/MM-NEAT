package edu.southwestern.tasks.mspacman.agentcontroller.ghosts;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.ghosts.VariableDirectionGhostBlockLoadedInputOutputMediator;
import oldpacman.game.Constants.GHOST;

import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class SharedNNCheckEachDirectionGhostsController extends SharedNNDirectionalGhostsController {

	public SharedNNCheckEachDirectionGhostsController(Network n) {
		super(n);
	}

	@Override
	public double[] getDirectionPreferences(GameFacade gf, int ghostIndex) {
		double[] preferences = new double[GameFacade.NUM_DIRS];
                // -1 is lowest possible value after activation function scaling
		Arrays.fill(preferences, -1); 
		final int current = gf.getGhostCurrentNodeIndex(ghostIndex);
		final int[] neighbors = gf.neighbors(current);
		for (int i = 0; i < neighbors.length; i++) {
			if (neighbors[i] != -1) {
				((VariableDirectionGhostBlockLoadedInputOutputMediator) this.inputMediator).setDirection(i);
				double[] inputs = this.inputMediator.getInputs(gf, ghostIndex);
				double[] outputs = nn.process(inputs);
				assert outputs.length == 1 : "Network should have a lone output for the utility of the move in the given direction";
				preferences[i] = outputs[0];
			}
		}
		return preferences;
	}

	@Override
	/**
	 * Should never be used but is necessary to compile. this method is required
	 * for the PO Ghost team to interface with the game facade.
	 */
	public int getAction(GameFacade gs, long timeDue, GHOST ghost) {
		return 0;
	}
}
