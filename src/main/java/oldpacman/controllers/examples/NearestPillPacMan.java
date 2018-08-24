package oldpacman.controllers.examples;

import static oldpacman.game.Constants.*;

import oldpacman.controllers.Controller;
import oldpacman.game.Game;

/*
 * The Class NearestPillPacMan.
 */
public class NearestPillPacMan extends Controller<MOVE> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	public MOVE getMove(Game game, long timeDue) {
		int currentNodeIndex = game.getPacmanCurrentNodeIndex();

		// get all active pills
		int[] activePills = game.getActivePillsIndices();

		// get all active power pills
		int[] activePowerPills = game.getActivePowerPillsIndices();

		// create a target array that includes all ACTIVE pills and power pills
		int[] targetNodeIndices = new int[activePills.length + activePowerPills.length];

		for (int i = 0; i < activePills.length; i++) {
			targetNodeIndices[i] = activePills[i];
		}

		for (int i = 0; i < activePowerPills.length; i++) {
			targetNodeIndices[activePills.length + i] = activePowerPills[i];
		}

		// return the next direction once the closest target has been identified
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				game.getClosestNodeIndexFromNodeIndex(currentNodeIndex, targetNodeIndices, DM.PATH), DM.PATH);
	}
}