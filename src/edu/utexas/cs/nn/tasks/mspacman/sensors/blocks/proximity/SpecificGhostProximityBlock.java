package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.proximity;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.MiscUtil;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class SpecificGhostProximityBlock extends MsPacManSensorBlock {

    private final boolean threats;
    private final int absence;
    private final boolean[] mask;
    private final boolean edible;

    /**
     * Default includes all ghosts
     *
     * @param threats
     */
    public SpecificGhostProximityBlock(boolean threats, boolean edible) {
        this(new boolean[]{true, true, true, true}, threats, edible);
    }

    /**
     * Mask corresponds to actual ghost indices
     *
     * @param mask which ghosts to include (include if index is true)
     * @param threats whether sensing threats or other
     */
    public SpecificGhostProximityBlock(boolean[] mask, boolean threats, boolean edible) {
        this.mask = mask;
        this.threats = threats;
        this.edible = edible;
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            SpecificGhostProximityBlock other = (SpecificGhostProximityBlock) o;
            return this.threats == other.threats && this.edible == other.edible && Arrays.equals(this.mask, other.mask);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.threats ? 1 : 0);
        hash = 43 * hash + (this.edible ? 1 : 0);
        hash = 43 * hash + Arrays.hashCode(this.mask);
        hash = 43 * hash + super.hashCode();
        return hash;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int current = gf.getPacmanCurrentNodeIndex();
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            if (mask[i]) {
                int ghostLoc = gf.getGhostCurrentNodeIndex(i);
                boolean eligible = (threats && gf.isGhostThreat(i)) || (edible && gf.isGhostEdible(i));
                inputs[in++] = eligible ? MiscUtil.scaleAndInvert(gf.getPathDistance(current, ghostLoc), GameFacade.MAX_DISTANCE) : absence;
            }
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        String type = threats && edible ? "" : (threats ? "Threat " : (edible ? "Edible " : "ERROR!"));
        for (int j = 0; j < CommonConstants.numActiveGhosts; j++) {
            if (mask[j]) {
                labels[in++] = type + "Ghost " + j + " Proximity";
            }
        }
        return in;
    }

    public int numberAdded() {
        int count = 0;
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            if (mask[i]) {
                count++;
            }
        }
        return count;
        //return numGhosts;
    }
}
