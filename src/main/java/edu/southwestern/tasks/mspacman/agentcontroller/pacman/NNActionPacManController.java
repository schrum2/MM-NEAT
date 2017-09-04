package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.agentcontroller.pacman.actions.MsPacManAction;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.ActionBlockLoadedInputOutputMediator;
import edu.southwestern.util.CombinatoricUtilities;
import edu.southwestern.util.stats.StatisticsUtilities;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class NNActionPacManController extends NNPacManController {

	private final ArrayList<MsPacManAction> actions;

	/**
	 * The input output mediator must be a ActionBlockLoadedInputOutputMediator
	 * for this controller to work.
	 *
	 * @param n
	 *            network that picks actions
	 */
	public NNActionPacManController(Network n) {
		super(n);
		this.actions = ((ActionBlockLoadedInputOutputMediator) inputMediator).actions;
	}

	/**
	 * Returns direction that pacman should move in
	 *
	 * @param gs
	 *            game state in facade
	 * @param decisionPoint
	 *            whether this is a decision point
	 * @return direction to move in
	 */
	public int getDirection(GameFacade gs) {
		double[] actionPreferences = getActionPreferences(gs);

		if (CommonConstants.watch && (nn instanceof TWEANN) && ((TWEANN) nn).numModules() > 1) {
			// Need to have a better indicator of current mode
			gs.addPoints(CombinatoricUtilities.colorFromInt(((TWEANN) nn).chosenModule),
					new int[] { gs.getPacmanCurrentNodeIndex() });
		}

		// Actions can veto by returning a -1, in which case the next best
		// action is chosen
		int move = -1;
		int attempts = 0;
		while (move == -1 && attempts < actionPreferences.length) {
			int action = CommonConstants.probabilisticSelection ? StatisticsUtilities.probabilistic(actionPreferences)
					: (CommonConstants.softmaxSelection
							? StatisticsUtilities.softmax(actionPreferences, CommonConstants.softmaxTemperature)
							: StatisticsUtilities.argmax(actionPreferences));
			actionPreferences[action] = -Integer.MAX_VALUE; // Prevent being
															// chosen again
			move = actions.get(action).getMoveAction(gs);
			attempts++;
		}
		if (move == -1) {
			// All actions failed
			return gs.getPacmanLastMoveMade();
		}
		return move;
	}

	/**
	 * Pick the high-level action with the highest network activation
	 *
	 * @param gf
	 *            game state in facade
	 * @return preferences for performing each action
	 */
	public double[] getActionPreferences(GameFacade gf) {
		double[] inputs = inputMediator.getInputs(gf, gf.getPacmanLastMoveMade());
		if (nn.isMultitask()) {
			ms.giveGame(gf);
			nn.chooseMode(ms.mode());
		}
		double[] outputs = nn.process(inputs);
		return outputs;
	}
}
