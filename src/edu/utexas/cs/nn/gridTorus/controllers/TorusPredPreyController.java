
package edu.utexas.cs.nn.gridTorus.controllers;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusPredPreyGame;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 *
 * @author Jacob Schrum
 * 
 *         This abstract class that provides the framework for the other
 *         controller methods in this package.
 * 
 */
public abstract class TorusPredPreyController {

	public static final int UP_INDEX = 0;
	public static final int RIGHT_INDEX = 1;
	public static final int DOWN_INDEX = 2;
	public static final int LEFT_INDEX = 3;
	public static final int NOTHING_INDEX = 4;

	public TorusPredPreyController() {
	}

	/**
	 * 
	 * @return Movement offsets for each available action: UP, RIGHT, DOWN,
	 *         LEFT, or possibly Nothing
	 */
	public static int[][] predatorActions() {
		int[][] predAct = Parameters.parameters.booleanParameter("allowDoNothingActionForPredators")
				? new int[][] { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }, { 0, 0 } }
				: new int[][] { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
		return predAct;
	}

	/**
	 * 
	 * @return Movement offsets for each available action: UP, RIGHT, DOWN,
	 *         LEFT, or possibly Nothing
	 */
	public static int[][] preyActions() {
		int[][] preyAct = Parameters.parameters.booleanParameter("allowDoNothingActionForPreys")
				? new int[][] { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }, { 0, 0 } }
				: new int[][] { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
		return preyAct;
	}

	/**
	 * 
	 * @param me:
	 *            the controlled agent
	 * @param game:
	 *            the domain in which the game is being played
	 * @return: calls the other getAction method in order to with the world,
	 *          predators, and prey so the controller classes can use them.
	 */
	public int[] getAction(TorusAgent me, TorusPredPreyGame game) {
		return getAction(me, game.getWorld(), game.getPredators(), game.getPrey());
	}

	/**
	 * 
	 * @param me:
	 *            the controller
	 * @param world:
	 *            the domain on which the game is being played
	 * @param preds:
	 *            the predators
	 * @param prey:
	 *            the prey agents
	 * @return: an int array corresponding to the movement agent me should take.
	 */
	public abstract int[] getAction(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey);
}
