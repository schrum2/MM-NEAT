/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distancedifference;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class PillDistanceDifferenceBlock extends PacManVsThreatDistanceDifferencesBlock {

    public PillDistanceDifferenceBlock() {
        this(false, false, 0, false);
    }

    public PillDistanceDifferenceBlock(boolean ghostDistances, boolean pacmanDistances, int simulationDepth, boolean futurePillsEaten) {
        super(ghostDistances, pacmanDistances, simulationDepth, futurePillsEaten, !Parameters.parameters.booleanParameter("noPowerPills"), !Parameters.parameters.booleanParameter("noPowerPills"));
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePillsIndices();
    }

    @Override
    public String typeOfTarget() {
        return "Pill";
    }
}
