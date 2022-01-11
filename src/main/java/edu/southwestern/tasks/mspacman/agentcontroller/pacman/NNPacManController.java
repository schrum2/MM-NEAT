package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.MsPacManTask;
import edu.southwestern.tasks.mspacman.data.ScentPath;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import oldpacman.controllers.NewPacManController;
import oldpacman.game.Game;

import java.util.Arrays;

/**
 * defines the evolved pac man controller
 * 
 * @author Jacob Schrum
 */
public abstract class NNPacManController extends NewPacManController {

	protected Network nn;
	public int lives = -1;
	public MsPacManControllerInputOutputMediator inputMediator;
	private int maxLevel;
	protected MsPacManModeSelector ms = null;
	// Accessed and reset by Performance log
	public static int timesAllLevelsBeaten = 0;
	public static int timesTimeLimitReached = 0;
	public static int timesDied = 0;

	/**
	 * Called once a generation by Performance log resets the data for the times
	 * all levels are beaten, times the time limit is reached, and times died
	 */
	public static void resetTimes() {
		timesAllLevelsBeaten = 0;
		timesTimeLimitReached = 0;
		timesDied = 0;
	}

	/**
	 * constructs a pacman controller for the evolved pacman based on parameters
	 * 
	 * @param n,
	 *            network
	 */
	public NNPacManController(Network n) {
		nn = n;
		maxLevel = Parameters.parameters.integerParameter("pacmanMaxLevel");

		inputMediator = MsPacManTask.pacmanInputOutputMediator;
		if (inputMediator != null) {
			inputMediator.reset();
		}

		if (nn != null && nn.isMultitask()) {
			ms = MsPacManTask.pacmanMultitaskScheme;
		}
	}

	/**
	 * Finds the action to do based off of the game and the game's various
	 * current factors, such as the number of ghosts eaten, pacman's lives,
	 * direction, and location
	 * 
	 * @param gs
	 *            the gameFacade
	 * @param timeDue
	 * @return the action to take or a number indicating the end of the game
	 */
	public int getAction(final Game gs, long timeDue) {
		return getAction(new GameFacade(gs), timeDue);
	}

	/**
	 * Finds the action to do based off of the game and the game's various
	 * current factors, such as the number of ghosts eaten, pacman's lives,
	 * direction, and location
	 * 
	 * @param gs
	 *            the gameFacade
	 * @param timeDue
	 * @return the action to take or a number indicating the end of the game
	 */
	public int getAction(GameFacade gs, long timeDue) {
		ScentPath.scents.visit(gs, gs.getPacmanCurrentNodeIndex());
		int curLevel = gs.getCurrentLevel();
		if (curLevel >= maxLevel) {
			// System.out.println("Beat all levels");
			int ghostsEaten = gs.getNumEatenGhosts();
			if (ghostsEaten >= CommonConstants.ghostsForBonus * curLevel) {
				System.out.println("Extra eval for eating " + ghostsEaten + " ghosts");
				maxLevel++;
			} else {
				if (MMNEAT.evalReport != null) {
					MMNEAT.evalReport.log("Reached MAX Level");
					MMNEAT.evalReport.log("");
				}
				if (CommonConstants.watch) {
					System.out.println("Reached MAX Level");
				}
				return END_GAME_CODE;
			}
		}
		int checkLives = gs.getPacmanNumberOfLivesRemaining();
		if (gs.levelJustChanged() || checkLives < lives) {
			reset();
		}
		lives = checkLives; // always do this in case lives increases
		inputMediator.mediatorStateUpdate(gs);
		int levelTime = gs.getCurrentLevelTime();
		if (CommonConstants.pacmanFatalTimeLimit && levelTime >= CommonConstants.pacManLevelTimeLimit) {
			timesTimeLimitReached++;
			if (MMNEAT.evalReport != null) {
				MMNEAT.evalReport.log("Level Time Limit Reached");
				MMNEAT.evalReport.log("");
			}
			if (CommonConstants.watch) {
				System.out.println("Level Time Limit Reached");
			}
			return END_GAME_CODE;
		}

		if (gs.getPacmanCurrentNodeIndex() == -1) {
			System.out.println("Ms. Pac-Man has no location");
			return -1; // A neutral action
		}
		return getDirection(gs);
	}

	@Override
	/**
	 * resets all of the controller's various variables and object values
	 */
	public void reset() {
		super.reset();
		if(nn != null) nn.flush();//might be null when using hyperNEAT w/ msPacMan
		inputMediator.reset();
		ScentPath.scents.reset();
		if (ScentPath.modeScents != null) {
			for (int i = 0; i < ScentPath.modeScents.length; i++) {
				ScentPath.modeScents[i].reset();
			}
		}
	}

	/**
	 * find the direction that pacman is facing based off of various game data
	 * and return it
	 * 
	 * @param gs
	 *            the gameFacade
	 * @return direction
	 */
	public abstract int getDirection(GameFacade gs);

	@Override
	/**
	 * log various details about the controller in the evalReport
	 */
	public void logEvaluationDetails() {
		MMNEAT.evalReport.log("Network Details:");
		MMNEAT.evalReport.log("\tNum Nodes: " + ((TWEANN) nn).nodes.size());
		MMNEAT.evalReport.log("\tNum Modes: " + ((TWEANN) nn).numModules());
		MMNEAT.evalReport.log("\tNum Outputs: " + ((TWEANN) nn).numOutputs());
		MMNEAT.evalReport.log("\tNeurons Per Mode: " + ((TWEANN) nn).neuronsPerModule());
		MMNEAT.evalReport.log("\tMode Usage: " + Arrays.toString(((TWEANN) nn).moduleUsage));
		MMNEAT.evalReport.log("");
	}
}
