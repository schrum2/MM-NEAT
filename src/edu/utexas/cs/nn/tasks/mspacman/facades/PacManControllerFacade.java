package edu.utexas.cs.nn.tasks.mspacman.facades;

import pacman.controllers.NewPacManController;

/**
 *
 * @author Jacob Schrum
 */
public class PacManControllerFacade {

    public NewPacManController newP = null;

    public PacManControllerFacade(NewPacManController p) {
        newP = p;
    }

    public void reset() {
        newP.reset();
    }

    @Override
    public String toString() {
        return newP.toString();
    }

    public void logEvaluationDetails() {
        newP.logEvaluationDetails();
    }
}
