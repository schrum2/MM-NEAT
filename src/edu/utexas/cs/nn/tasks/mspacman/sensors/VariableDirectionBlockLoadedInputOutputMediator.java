package edu.utexas.cs.nn.tasks.mspacman.sensors;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.VariableDirectionBlock;

/**
 * An extension of the BlockLoadedInputOutputMediator which allows for variations in direction
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

    /**
     * changes the sensor direction for each block in the sensor blocks arrayList
     * @param dir
     */
    public void setDirection(int dir) {
        for (MsPacManSensorBlock block : this.blocks) {
            if (block instanceof VariableDirectionBlock) {
                ((VariableDirectionBlock) block).setDirection(dir);
            }
        }
    }

    @Override
    /**
     * @return a string array with the output labels
     */
    public String[] outputLabels() {
        return new String[]{"Preference"};
    }

    @Override
    /**
     * @return the number of outputs, which is just 1 because it is just the preference
     * for the currently checked direction
     */
    public int numOut() {
        return 1; // Just the preference for the currently checked direction
    }
}
