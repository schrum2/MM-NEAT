package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * @author Jacob Schrum
 * One module if any pills are present,
 * and another module otherwise.
 * Only makes sense given modified domain that allows
 * evaluation in absence of pills.
 */
public class AnyPillsModeSelector extends MsPacManModeSelector {

    public static final int PILLS_PRESENT = 0;
    public static final int NO_PILLS_PRESENT = 1;

    public AnyPillsModeSelector() {
    }

    public int mode() {
        return gs.getNumActivePills() > 0 || gs.getNumActivePowerPills() > 0 ? PILLS_PRESENT : NO_PILLS_PRESENT;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[PILLS_PRESENT] = PILL_SCORE;
        result[NO_PILLS_PRESENT] = GHOST_SCORE;
        return result;
    }
}
