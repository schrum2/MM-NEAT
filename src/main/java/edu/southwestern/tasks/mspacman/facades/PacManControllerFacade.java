package edu.southwestern.tasks.mspacman.facades;

/**
 * Facade for pacman controller
 * @author Jacob Schrum
 */
public class PacManControllerFacade {

	//actual pacman controller
	public oldpacman.controllers.NewPacManController oldP = null;
	//TODO: test
	public pacman.controllers.PacmanController poP = null;

	/**
	 * Constructor
	 * @param p pacman controller
	 */
	public PacManControllerFacade(oldpacman.controllers.NewPacManController p) {
		oldP = p;
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
		if(oldP == null) {
			//TODO
			System.out.println("TODO: implement reset() in PacManControllerFacade.java, ln 38");
		} else {
			oldP.reset();	
		}
	}

	@Override
	/**
	 * To string method for pacman controller
	 */
	public String toString() {
		return oldP == null ?
				poP.toString():
				oldP.toString();
	}

	/**
	 * Logs details about pacman controller
	 *  during evaluation
	 */
	public void logEvaluationDetails() {
		if(oldP == null) {
			//TODO
			System.out.println("TODO: implement logEvaluationDetails() in PacManControllerFacade.java, ln 61");
		} else {
			oldP.logEvaluationDetails();
		}
	}
}
