package edu.southwestern.tasks.mspacman.facades;

import edu.southwestern.parameters.Parameters;


/**
 *
 * @author Jacob Schrum
 */
public class ExecutorFacade {

	oldpacman.Executor oldE = null;
	popacman.CustomExecutor poE = null;

	/**
	 * Constructor that contains excecutor
	 * (thus why called facade)
	 * @param e
	 */
	public ExecutorFacade(oldpacman.Executor e) {
		oldE = e;
	}
	
	/**
	 * Used for Partially Observable Pacman
	 * Constructor that contains excecutor
	 * (thus why called facade)
	 * @param e
	 */
	public ExecutorFacade(popacman.CustomExecutor e) {
		poE = e;
	}

	/**
	 * creates log of given name
	 * @param string name of log
	 */
	public void log(String string) {
		if(oldE == null) {
			System.out.println("TODO: no log method in poE, ExecutorFacade ln 33");
		} else {
			oldE.log(string);
		}
	}

	/**
	 * runs a game timed
	 * @param mspacman facade contianing controller for ms pacman
	 * @param ghosts facade containing controller for ghosts
	 * @param game facade containing game
	 */
	public void runGameTimed(PacManControllerFacade mspacman, GhostControllerFacade ghosts, GameFacade game) {
		if(oldE == null) {
			poE.runGameTimed(mspacman.poP, ghosts.poG, true, game.poG);
		} else {
			oldE.runGameTimed(mspacman.oldP, ghosts.oldG, true, game.oldG);
		}
	}

	/**
	 * runs an experiment
	 * @param mspacman facade contianing controller for ms pacman
	 * @param ghosts facade containing controller for ghosts
	 * @param game facade containing game
	 */
	public void runExperiment(PacManControllerFacade mspacman, GhostControllerFacade ghosts, GameFacade game) {
		if(oldE == null) {
			// 1 means only run 1 trial
			poE.runExperiment(mspacman.poP, ghosts.poG, 1, "desc", game.poG);
		} else {
			oldE.runExperiment(mspacman.oldP, ghosts.oldG, game.oldG);
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
		if(oldE == null) {
			System.out.println("TODO: need to implement runGameTimedRecorded, ExecutorFacade ln 79");
			poE.runGameTimedRecorded(mspacman.poP, ghosts.poG, fileName, game.poG, visual);
		} else {
			oldE.runGameTimedRecorded(game.oldG, mspacman.oldP, ghosts.oldG, visual, fileName);
		}
	}

	/**
	 * Replays given game
	 * @param fileName name of game to replay
	 * @param visual whether or not to visualize game
	 */
	public void replayGame(String fileName, boolean visual) {
		if(oldE == null) {
			System.out.println("TODO: need to implement replayGame, ExecutorFacade ln 92");
			//TODO: poE.replayGame needs a game as aparameter
			//poE.replayGame(fileName, visual, game);
			throw new UnsupportedOperationException("TODO: implement replayGame in ExecutorFacade.java");
		} else {
			oldE.replayGame(fileName, visual, Parameters.parameters.integerParameter("pacmanReplayDelay"));
		}
	}

	/**
	 * runs a timed game with visuals off
	 * @param game facade of game
	 * @param mspacman facade of controller
	 * @param ghosts facade of ghosts
	 */
	public void runGameTimedNonVisual(GameFacade game, PacManControllerFacade mspacman, GhostControllerFacade ghosts) {
		if(oldE == null) {
			//TODO
			System.out.println("TODO: need to implement runGameTimedNonVisual, ExecutorFacade ln 106");
			//TODO: rectify the fact that poE has no speed optomised method
			poE.runGameTimedSpeedOptimised(mspacman.poP, ghosts.poG, false, false, game.poG);
		} else {
			oldE.runGameTimedSpeedOptimised(mspacman.oldP, ghosts.oldG, false, false, game.oldG);
		}
	}
}
