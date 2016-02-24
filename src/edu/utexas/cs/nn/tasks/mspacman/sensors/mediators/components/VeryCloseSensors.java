/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.components;

import edu.utexas.cs.nn.tasks.mspacman.sensors.BlockLoadedInputOutputMediator;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToEdibleGhost;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToPill;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToPowerPill;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.booleansensors.veryclose.IsCloseToThreatGhost;

/**
 *
 * @author Jacob Schrum
 */
public class VeryCloseSensors extends BlockLoadedInputOutputMediator {

    public VeryCloseSensors(boolean sensePills, boolean senseEdibleGhosts, boolean sensePowerPills) {
        super();
        blocks.add(new IsCloseToThreatGhost());
        if (sensePills) {
            blocks.add(new IsCloseToPill());
        }
        if (senseEdibleGhosts) {
            blocks.add(new IsCloseToEdibleGhost());
        }
        if (sensePowerPills) {
            blocks.add(new IsCloseToPowerPill());
        }
    }
}