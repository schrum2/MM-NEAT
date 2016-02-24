/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class EdibleGhostDistanceDifferenceBlock extends PacManVsThreatDistanceDifferencesBlock {

    public EdibleGhostDistanceDifferenceBlock() {
        this(false, false, 0, false, false);
    }

    public EdibleGhostDistanceDifferenceBlock(boolean ghostDistances, boolean pacmanDistances, int simulationDepth, boolean futurePillsEaten, boolean futurePowerPillsEaten) {
        super(ghostDistances, pacmanDistances, simulationDepth, futurePillsEaten, true, futurePowerPillsEaten);
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations();
    }

    @Override
    public String typeOfTarget() {
        return "Edible Ghost";
    }
}
