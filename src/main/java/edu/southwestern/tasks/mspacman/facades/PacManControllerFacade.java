package edu.utexas.cs.nn.tasks.mspacman.facades;

import pacman.controllers.NewPacManController;

/**
 * Facade for pacman controller
 * @author Jacob Schrum
 */
public class PacManControllerFacade {

	//actual pacman controller
	public NewPacManController newP = null;

	/**
	 * Constructor
	 * @param p pacman controller
	 */
	public PacManControllerFacade(NewPacManController p) {
		newP = p;
	}

	/**
	 * Resets pacman controller by
	 * resetting thread
	 */
	public void reset() {
		newP.reset();
	}

	@Override
	/**
	 * To string method for pacman controller
	 */
	public String toString() {
		return newP.toString();
	}

	/**
	 * Logs details about pacman controller
	 *  during evaluation
	 */
	public void logEvaluationDetails() {
		newP.logEvaluationDetails();
	}
}
