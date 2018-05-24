package edu.southwestern.tasks.mspacman.facades;

import edu.southwestern.parameters.Parameters;


/**
 *
 * @author Jacob Schrum
 */
public class ExecutorFacade {

	oldpacman.Executor newE = null;
	pacman.Executor poE = null;

	/**
	 * Constructor that contains excecutor
	 * (thus why called facade)
	 * @param e
	 */
	public ExecutorFacade(oldpacman.Executor e) {
		newE = e;
	}
	
	public ExecutorFacade(pacman.Executor e) {
		poE = e;
	}

	/**
	 * creates log of given name
	 * @param string name of log
	 */
	public void log(String string) {
		if(newE == null) {
			System.out.println("no log method in poE, ExecutorFacade ln 33");
		} else {
			newE.log(string);
		}
	}

	/**
	 * runs a game timed
	 * @param mspacman facade contianing controller for ms pacman
	 * @param ghosts facade containing controller for ghosts
	 * @param game facade containing game
	 */
	public void runGameTimed(PacManControllerFacade mspacman, GhostControllerFacade ghosts, GameFacade game) {
		if(newE == null) {
			System.out.println("TODO: need to implement runGameTimed, ExecutorFacade ln 48");
		} else {
			newE.runGameTimed(mspacman.newP, ghosts.newG, true, game.newG);
		}
	}

	/**
	 * runs an experiment
	 * @param mspacman facade contianing controller for ms pacman
	 * @param ghosts facade containing controller for ghosts
	 * @param game facade containing game
	 */
	public void runExperiment(PacManControllerFacade mspacman, GhostControllerFacade ghosts, GameFacade game) {
		if(newE == null) {
			System.out.println("TODO: need to implement runExperiment, ExecutorFacade ln 62");
		} else {
			newE.runExperiment(mspacman.newP, ghosts.newG, game.newG);
		}
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
		if(newE == null) {
			System.out.println("TODO: need to implement runGameTimedRecorded, ExecutorFacade ln 79");
		} else {
			newE.runGameTimedRecorded(game.newG, mspacman.newP, ghosts.newG, visual, fileName);
		}
	}

	/**
	 * Replays given game
	 * @param fileName name of game to replay
	 * @param visual whether or not to visualize game
	 */
	public void replayGame(String fileName, boolean visual) {
		if(newE == null) {
			System.out.println("TODO: need to implement replayGame, ExecutorFacade ln 92");
		} else {
			newE.replayGame(fileName, visual, Parameters.parameters.integerParameter("pacmanReplayDelay"));
		}
	}

	/**
	 * runs a timed game with visuals off
	 * @param game facade of game
	 * @param mspacman facade of controller
	 * @param ghosts facade of ghosts
	 */
	public void runGameTimedNonVisual(GameFacade game, PacManControllerFacade mspacman, GhostControllerFacade ghosts) {
		if(newE == null) {
			System.out.println("TODO: need to implement runGameTimedNonVisual, ExecutorFacade ln 106");
		} else {
			newE.runGameTimedSpeedOptimised(mspacman.newP, ghosts.newG, false, false, game.newG);
		}
	}
}
