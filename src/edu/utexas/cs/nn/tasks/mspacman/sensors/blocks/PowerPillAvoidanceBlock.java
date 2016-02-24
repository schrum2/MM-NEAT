package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.awt.Color;

/**
 * Block informs Ms. Pac-Man which direction to move in to go away from the
 * nearest Power pill, when very close to one.
 *
 * @author Jacob Schrum
 */
public class PowerPillAvoidanceBlock extends MsPacManSensorBlock {

    public static final int CLOSE_DISTANCE = 50;
    private final int absence;

    public PowerPillAvoidanceBlock() {
        this(Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0);
    }

    public PowerPillAvoidanceBlock(int absence) {
        this.absence = absence;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? lastDirection : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        int numPowerPills = gf.getNumActivePowerPills();
        int dirNearestPowerPill = -1;
        int dirAwayFromNearestPowerPill = -1;
        if (numPowerPills > 0) {
            int closestPowerPill = gf.getClosestNodeIndexFromNodeIndex(current, gf.getActivePowerPillsIndices());
            double closestPowerPillDistance = gf.getShortestPathDistance(current, closestPowerPill);
            // Only try to avoid Power Pill if close to it
            if (closestPowerPillDistance < CLOSE_DISTANCE) {
                if (CommonConstants.watch) {
                    gf.addLines(Color.WHITE, current, closestPowerPill);
                }
                dirNearestPowerPill = gf.getNextPacManDirTowardsTarget(closestPowerPill);
                dirAwayFromNearestPowerPill = gf.getNextPacManDirAwayFromTarget(closestPowerPill);
            }
        }

        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall) {
                // Walls are not an option
                inputs[in++] = absence;
            } else if (dir == dirNearestPowerPill) {
                // Cancel out direction to power pill
                inputs[in++] = absence;
            } else if (dir == dirAwayFromNearestPowerPill) {
                // Heighten preference for escape direction
                inputs[in++] = 1;
            } else {
                // No power pills, or some other direction
                inputs[in++] = 0;
            }
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = "Avoid Power Pill " + first + "?";
        labels[in++] = "Avoid Power Pill Right?";
        labels[in++] = "Avoid Power Pill " + last + "?";
        labels[in++] = "Avoid Power Pill Left?";

        return in;
    }

    public int numberAdded() {
        return GameFacade.NUM_DIRS;
    }
}
