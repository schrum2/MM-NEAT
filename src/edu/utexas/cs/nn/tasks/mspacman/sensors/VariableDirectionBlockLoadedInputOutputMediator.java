/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;

/**
 * @author Jacob Schrum
 */
public class VariableDirectionBlockLoadedInputOutputMediator extends BlockLoadedInputOutputMediator {

    public VariableDirectionBlockLoadedInputOutputMediator() {
        super();
        // Cannot cache inputs because the same sensor blocks will run multiple
        // times with different orientations, resulting in different values
        CommonConstants.pacManSensorCaching = false;
        Parameters.parameters.setBoolean("pacManSensorCaching", false);
    }

    public void setDirection(int dir) {
        for (MsPacManSensorBlock block : this.blocks) {
            if (block instanceof VariableDirectionBlock) {
                ((VariableDirectionBlock) block).setDirection(dir);
            }
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
