/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.nearestfarthest;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class NearestFarthestEdibleGhostBlock extends NearestFarthestIndexBlock {

    private final boolean[] include;

    public NearestFarthestEdibleGhostBlock(boolean nearest) {
        this(nearest, Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0);
    }

    public NearestFarthestEdibleGhostBlock(boolean nearest, boolean[] include) {
        this(nearest, Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0, include);
    }

    public NearestFarthestEdibleGhostBlock(boolean nearest, int absence) {
        this(nearest, absence, new boolean[]{true, true, true, true});
    }

    public NearestFarthestEdibleGhostBlock(boolean nearest, int absence, boolean[] include) {
        super(nearest, absence);
        this.include = include;
    }

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getEdibleGhostLocations(include);
    }

    @Override
    public String typeOfTarget() {
        return "Edible Ghost";
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            NearestFarthestEdibleGhostBlock other = (NearestFarthestEdibleGhostBlock) o;
            return super.equals(other) && Arrays.equals(this.include, other.include);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Arrays.hashCode(this.include);
        hash = 11 * hash + super.hashCode();
        return hash;
    }
}
