package edu.southwestern.tasks.mspacman.facades;

/**
 * Facade for pacman controller
 * @author Jacob Schrum
 */
public class PacManControllerFacade {

	//actual pacman controller
	public oldpacman.controllers.NewPacManController newP = null;
	//TODO: test
	public pacman.controllers.PacmanController poP = null;

	/**
	 * Constructor
	 * @param p pacman controller
	 */
	public PacManControllerFacade(oldpacman.controllers.NewPacManController p) {
		newP = p;
	}
	
	/**
	 * Used for popacman
	 * Constructor
	 * @param p pacman controller
	 */
	public PacManControllerFacade(pacman.controllers.PacmanController p) {
		poP = p;
	}

	/**
	 * Resets pacman controller by
	 * resetting thread
	 */
	public void reset() {
		if(newP == null) {
			//TODO
			System.out.println("TODO: implement reset() in PacManControllerFacade.java, ln 38");
		} else {
			newP.reset();	
		}
	}

	@Override
	/**
	 * To string method for pacman controller
	 */
	public String toString() {
		return newP == null ?
				poP.toString():
				newP.toString();
	}

	/**
	 * Logs details about pacman controller
	 *  during evaluation
	 */
	public void logEvaluationDetails() {
		if(newP == null) {
			//TODO
			System.out.println("TODO: implement logEvaluationDetails() in PacManControllerFacade.java, ln 61");
		} else {
			newP.logEvaluationDetails();
		}
	}
}
