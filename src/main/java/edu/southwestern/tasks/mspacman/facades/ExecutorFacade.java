package edu.southwestern.tasks.mspacman.facades;

import edu.southwestern.parameters.Parameters;
import pacman.Executor;

/**
 *
 * @author Jacob Schrum
 */
public class ExecutorFacade {

	Executor newE = null;

	/**
	 * Constructor that contains excecutor
	 * (thus why called facade)
	 * @param e
	 */
	public ExecutorFacade(Executor e) {
		newE = e;
	}

	/**
	 * creates log of given name
	 * @param string name of log
	 */
	public void log(String string) {
		newE.log(string);
	}

	/**
	 * runs a game timed
	 * @param mspacman facade contianing controller for ms pacman
	 * @param ghosts facade containing controller for ghosts
	 * @param game facade containing game
	 */
	public void runGameTimed(PacManControllerFacade mspacman, GhostControllerFacade ghosts, GameFacade game) {
		newE.runGameTimed(mspacman.newP, ghosts.newG, true, game.newG);
	}

	/**
	 * runs an experiment
	 * @param mspacman facade contianing controller for ms pacman
	 * @param ghosts facade containing controller for ghosts
	 * @param game facade containing game
	 */
	public void runExperiment(PacManControllerFacade mspacman, GhostControllerFacade ghosts, GameFacade game) {
		newE.runExperiment(mspacman.newP, ghosts.newG, game.newG);
	}

	/**
	 * runs a timed game and records it
	 * @param game facade of game 
	 * @param mspacman facade of controller 
	 * @param ghosts facade of ghots 
	 * @param visual whether or not to visualize run
	 * @param fileName name of file to store
	 */
	public void runGameTimedRecorded(GameFacade game, PacManControllerFacade mspacman, GhostControllerFacade ghosts,
			boolean visual, String fileName) {
		newE.runGameTimedRecorded(game.newG, mspacman.newP, ghosts.newG, visual, fileName);
	}

	/**
	 * Replays given game
	 * @param fileName name of game to replay
	 * @param visual whether or not to visualize game
	 */
	public void replayGame(String fileName, boolean visual) {
		newE.replayGame(fileName, visual, Parameters.parameters.integerParameter("pacmanReplayDelay"));
	}

	/**
	 * runs a timed game with visuals off
	 * @param game facade of game
	 * @param mspacman facade of controller
	 * @param ghosts facade of ghosts
	 */
	public void runGameTimedNonVisual(GameFacade game, PacManControllerFacade mspacman, GhostControllerFacade ghosts) {
		newE.runGameTimedSpeedOptimised(mspacman.newP, ghosts.newG, false, false, game.newG);
	}
}
