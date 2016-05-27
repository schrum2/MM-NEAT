package edu.utexas.cs.nn.tasks.mspacman.facades;

import edu.utexas.cs.nn.parameters.Parameters;
import java.util.EnumMap;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

/**
 *
 * @author Jacob Schrum
 */
public class ExecutorFacade {

	Executor newE = null;

	public ExecutorFacade(Executor e) {
		newE = e;
	}

	public void log(String string) {
		newE.log(string);
	}

	public void runGameTimed(PacManControllerFacade mspacman, GhostControllerFacade ghosts, GameFacade game) {
		newE.runGameTimed(mspacman.newP, ghosts.newG, true, game.newG);
	}

	public void runExperiment(PacManControllerFacade mspacman, GhostControllerFacade ghosts, GameFacade game) {
		newE.runExperiment(mspacman.newP, ghosts.newG, game.newG);
	}

	public void runGameTimedRecorded(GameFacade game, PacManControllerFacade mspacman, GhostControllerFacade ghosts,
			boolean visual, String fileName) {
		newE.runGameTimedRecorded(game.newG, mspacman.newP, ghosts.newG, visual, fileName);
	}

	public void replayGame(String fileName, boolean visual) {
		newE.replayGame(fileName, visual, Parameters.parameters.integerParameter("pacmanReplayDelay"));
	}

	public void runGameTimedNonVisual(GameFacade game, PacManControllerFacade mspacman, GhostControllerFacade ghosts) {
		newE.runGameTimedSpeedOptimised(mspacman.newP, ghosts.newG, false, false, game.newG);
	}
}
