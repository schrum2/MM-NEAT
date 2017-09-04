package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.ghosts;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts.VariableDirectionGhostBlockLoadedInputOutputMediator;
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
}
