package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw.MirroredXPosBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw.MirroredYPosBlock;

/**
 * Contains diverse information, but all about a single index-specific ghost
 *
 * @author Jacob Schrum
 */
public class SpecificGhostBlock extends MsPacManSensorBlock {

    protected final int ghostIndex;
    protected final int absence;
    private final boolean proximityOrder;

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o instanceof SpecificGhostBlock) {
            SpecificGhostBlock other = (SpecificGhostBlock) o;
            return this.ghostIndex == other.ghostIndex;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.ghostIndex;
        hash = 79 + hash + super.hashCode();
        return hash;
    }

    public SpecificGhostBlock(int index) {
        this.ghostIndex = index;
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
        this.proximityOrder = Parameters.parameters.booleanParameter("specificGhostProximityOrder");
    }

    @Override
    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        final int pacmanNode = gf.getPacmanCurrentNodeIndex();
        final int pacmanNearestPowerPill = gf.getNumActivePowerPills() == 0 ? -1 : gf.getClosestNodeIndexFromNodeIndex(pacmanNode, gf.getActivePowerPillsIndices());
        final int ghostNode = proximityOrder ? gf.ghostLocationByProximity(ghostIndex) : gf.getGhostCurrentNodeIndex(ghostIndex);
        // edible time 
        int edibleTime = gf.getGhostEdibleTime(ghostIndex);
        boolean edible = edibleTime > 0;
        inputs[in++] = edible ? 1 : absence;
        // lair time
        int lairTime = gf.getGhostLairTime(ghostIndex);
        int lairExit = gf.getGhostInitialNodeIndex();
        // Distance (scaled)
        in = distances(inputs, in, gf, lairTime, pacmanNearestPowerPill, pacmanNode, ghostNode, lairExit);
        // Nearest direction
        final int referenceDir = CommonConstants.relativePacmanDirections ? lastDirection : 0;
        final int[] neighbors = gf.neighbors(pacmanNode);

        int targetDir = lairTime == 0 ? gf.getNextPacManDirTowardsTarget(ghostNode) : -1;
        in = directions(inputs, in, referenceDir, neighbors, lairTime, targetDir, edible);
        // Relative XY
        int ghostX = gf.getNodeXCoord(ghostNode);
        int ghostY = gf.getNodeYCoord(ghostNode);
        int lairX = gf.getNodeXCoord(lairExit);
        int lairY = gf.getNodeYCoord(lairExit);
        in = coordOffset(inputs, in, ghostX, ghostY, lairX, lairY, edible);
        return in;
    }

    public int coordOffset(double[] inputs, int in, int ghostX, int ghostY, int lairX, int lairY, boolean edible) {
        inputs[in++] = (ghostX - lairX) / (MirroredXPosBlock.MAX_X_COORD * 1.0);
        inputs[in++] = (ghostY - lairY) / (MirroredYPosBlock.MAX_Y_COORD * 1.0);
        return in;
    }

    public int distances(double[] inputs, int in, GameFacade gf, int lairTime, int pacmanNearestPowerPill, int pacmanNode, int ghostNode, int lairExit) {
        if (lairTime == 0) {
            double pacmanGhostDistance = gf.getShortestPathDistance(pacmanNode, ghostNode);
            inputs[in++] = Math.min(pacmanGhostDistance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = Math.min(gf.getShortestPathDistance(lairExit, ghostNode), GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = pacmanNearestPowerPill == -1 ? 1 : Math.min(gf.getShortestPathDistance(pacmanNearestPowerPill, ghostNode), GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = pacmanGhostDistance < 10 ? 1 : 0;
        } else {
            inputs[in++] = 1; // Infinite distance
            inputs[in++] = 0; // Lair distance
            inputs[in++] = 1; // Infinite distance
            inputs[in++] = 0; // Not close
        }
        return in;
    }

    public int directions(double[] inputs, int in, int referenceDir, int[] neighbors, int lairTime, int targetDir, boolean edible) {
        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall) {
                inputs[in++] = absence;
            } else if (lairTime > 0) {
                inputs[in++] = 0;
            } else {
                inputs[in++] = (targetDir == dir) ? 1 : 0;
            }
        }
        return in;
    }

    @Override
    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "Ghost " + ghostIndex + " Edible?";
        // Distance (scaled)
        in = distanceLabels(ghostIndex, labels, in);
        // Nearest direction
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";
        in = directionLabels(ghostIndex, labels, in, first, last);
        // Relative XY
        in = coordLabels(ghostIndex, labels, in);
        return in;
    }

    @Override
    public int numberAdded() {
        return 11;
    }

    protected int distanceLabels(int ghostIndex, String[] labels, int in) {
        labels[in++] = "Ghost " + ghostIndex + " Distance";
        labels[in++] = "Ghost " + ghostIndex + " Lair Distance";
        labels[in++] = "Ghost " + ghostIndex + " PacMan's Nearest Power Pill Distance";
        labels[in++] = "Ghost " + ghostIndex + " Very Close";
        return in;
    }

    protected int directionLabels(int ghostIndex, String[] labels, int in, String first, String last) {
        labels[in++] = "Ghost " + ghostIndex + " " + first + "?";
        labels[in++] = "Ghost " + ghostIndex + " Right?";
        labels[in++] = "Ghost " + ghostIndex + " " + last + "?";
        labels[in++] = "Ghost " + ghostIndex + " Left?";
        return in;
    }

    protected int coordLabels(int ghostIndex, String[] labels, int in) {
        labels[in++] = "Ghost " + ghostIndex + " Lair Relative X Coord";
        labels[in++] = "Ghost " + ghostIndex + " Lair Relative Y Coord";
        return in;
    }
}
