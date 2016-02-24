/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.ghosts.GhostSensorBlock;

/**
 * @author Jacob Schrum
 */
public class VariableDirectionGhostBlockLoadedInputOutputMediator extends GhostBlockLoadedInputOutputMediator {

    public VariableDirectionGhostBlockLoadedInputOutputMediator() {
        super();
        // Cannot cache inputs because the same sensor blocks will run multiple
        // times with different orientations, resulting in different values
        CommonConstants.pacManSensorCaching = false;
        Parameters.parameters.setBoolean("pacManSensorCaching", false);
    }

    public void setDirection(int dir) {
        for (GhostSensorBlock block : this.blocks) {
            block.setDirection(dir);
        }
    }

    @Override
    public String[] outputLabels() {
        return new String[]{"Preference"};
    }

    @Override
    public int numOut() {
        return 1; // Just the preference for the currently checked direction
    }
}
