package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.distance;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.Arrays;

/**
 *
 * @author Jacob Schrum
 */
public class SpecificGhostDistancesBlock extends MsPacManSensorBlock {

    // selecting both threats and edible treats both types the same
    private final boolean threats;
    private final boolean edible;
    private final boolean[] mask;

    /**
     * Default includes all ghosts
     *
     * @param threats
     */
    public SpecificGhostDistancesBlock(boolean threats, boolean edible) {
        this(new boolean[]{true, true, true, true}, threats, edible);
    }

    /**
     * Mask corresponds to actual ghost indices
     *
     * @param mask which ghosts to include (include if index is true)
     * @param threats whether sensing threats or other
     */
    public SpecificGhostDistancesBlock(boolean[] mask, boolean threats, boolean edible) {
        assert threats || edible : "Have to be able to sense at least one classification of ghost";

        this.mask = mask;
        this.threats = threats;
        this.edible = edible;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int current = gf.getPacmanCurrentNodeIndex();
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            if (mask[i]) {
                boolean present = (threats && gf.isGhostThreat(i)) || (edible && gf.isGhostEdible(i));
                //double distance = present ? gf.getShortestPathDistance(current, gf.getGhostCurrentNodeIndex(i)) : GameFacade.MAX_DISTANCE;
                // Try using ghost paths instead
                double distance = present ? gf.getGhostPathDistance(i, current) : GameFacade.MAX_DISTANCE;
                assert gf.getGhostPath(i, current).length == gf.getGhostPathDistance(i, current) : "Different methods of calculating ghost path length should be the same";
                //inputs[in++] = Util.scaleAndInvert(distance, GameFacade.MAX_DISTANCE);
                inputs[in++] = Math.min(distance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            }
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        String type = threats && edible ? "" : (threats ? "Threat " : (edible ? "Edible " : "ERROR!"));
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            if (mask[i]) {
                labels[in++] = type + "Ghost " + i + " Distance";
            }
        }
        return in;
    }

    public int numberAdded() {
        return ArrayUtil.countOccurrences(true, mask);
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            SpecificGhostDistancesBlock other = (SpecificGhostDistancesBlock) o;
            return other.threats == this.threats && other.edible == this.edible && Arrays.equals(this.mask, other.mask);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.threats ? 1 : 0);
        hash = 79 * hash + (this.edible ? 1 : 0);
        hash = 79 * hash + Arrays.hashCode(this.mask);
        hash = 79 * hash + super.hashCode();
        return hash;
    }
}
