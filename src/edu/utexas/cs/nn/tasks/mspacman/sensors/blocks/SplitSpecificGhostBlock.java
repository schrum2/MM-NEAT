package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw.MirroredXPosBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw.MirroredYPosBlock;

/**
 * Just like SpecificGhostBlock, but there are separate sensors for when the
 * target ghost is edible or a threat.
 *
 * @author Jacob Schrum
 */
public class SplitSpecificGhostBlock extends SpecificGhostBlock {

    public SplitSpecificGhostBlock(int index) {
        super(index);
    }

    @Override
    public int coordOffset(double[] inputs, int in, int ghostX, int ghostY, int lairX, int lairY, boolean edible) {
        // Sense direction when ghost is edible
        inputs[in++] = !edible ? 1 : (ghostX - lairX) / (MirroredXPosBlock.MAX_X_COORD * 1.0);
        inputs[in++] = !edible ? 1 : (ghostY - lairY) / (MirroredYPosBlock.MAX_Y_COORD * 1.0);
        // Sense direction when ghost is a threat
        inputs[in++] = edible ? 1 : (ghostX - lairX) / (MirroredXPosBlock.MAX_X_COORD * 1.0);
        inputs[in++] = edible ? 1 : (ghostY - lairY) / (MirroredYPosBlock.MAX_Y_COORD * 1.0);
        return in;
    }

    @Override
    public int distances(double[] inputs, int in, GameFacade gf, int lairTime, int pacmanNearestPowerPill, int pacmanNode, int ghostNode, int lairExit) {
        int edibleTime = gf.getGhostEdibleTime(ghostIndex);
        boolean edible = edibleTime > 0;
        if (lairTime == 0) {
            double pacmanGhostDistance = gf.getShortestPathDistance(pacmanNode, ghostNode);
            // Sense distances when ghost is edible
            inputs[in++] = !edible ? 1 : Math.min(pacmanGhostDistance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = !edible ? 1 : Math.min(gf.getShortestPathDistance(lairExit, ghostNode), GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = !edible ? 1 : pacmanNearestPowerPill == -1 ? 1 : Math.min(gf.getShortestPathDistance(pacmanNearestPowerPill, ghostNode), GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = edible && pacmanGhostDistance < 10 ? 1 : 0;
            // Sense distances when ghost is a threat
            inputs[in++] = edible ? 1 : Math.min(pacmanGhostDistance, GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = edible ? 0 : Math.min(gf.getShortestPathDistance(lairExit, ghostNode), GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = edible ? 1 : pacmanNearestPowerPill == -1 ? 1 : Math.min(gf.getShortestPathDistance(pacmanNearestPowerPill, ghostNode), GameFacade.MAX_DISTANCE) / GameFacade.MAX_DISTANCE;
            inputs[in++] = !edible && pacmanGhostDistance < 10 ? 1 : 0;
        } else {
            // Sense distances when ghost is edible
            inputs[in++] = 1; // Infinite distance
            inputs[in++] = 1; // Lair distance
            inputs[in++] = 1; // Infinite distance
            inputs[in++] = 0; // Not close
            // Sense distances when ghost is a threat
            inputs[in++] = 1; // Infinite distance
            inputs[in++] = 0; // Lair distance
            inputs[in++] = 1; // Infinite distance
            inputs[in++] = 0; // Not close
        }
        return in;
    }

    @Override
    public int directions(double[] inputs, int in, int referenceDir, int[] neighbors, int lairTime, int targetDir, boolean edible) {
        // Sense direction when ghost is edible
        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall || !edible) {
                inputs[in++] = absence;
            } else if (lairTime > 0) {
                inputs[in++] = 0;
            } else {
                inputs[in++] = (targetDir == dir) ? 1 : 0;
            }
        }
        // Sense direction when ghost is a threat
        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall || edible) {
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
    public int numberAdded() {
        return 21;
    }

    @Override
    protected int distanceLabels(int ghostIndex, String[] labels, int in) {
        labels[in++] = "Edible Ghost " + ghostIndex + " Distance";
        labels[in++] = "Edible Ghost " + ghostIndex + " Lair Distance";
        labels[in++] = "Edible Ghost " + ghostIndex + " PacMan's Nearest Power Pill Distance";
        labels[in++] = "Edible Ghost " + ghostIndex + " Very Close";

        labels[in++] = "Threat Ghost " + ghostIndex + " Distance";
        labels[in++] = "Threat Ghost " + ghostIndex + " Lair Distance";
        labels[in++] = "Threat Ghost " + ghostIndex + " PacMan's Nearest Power Pill Distance";
        labels[in++] = "Threat Ghost " + ghostIndex + " Very Close";
        return in;
    }

    @Override
    protected int directionLabels(int ghostIndex, String[] labels, int in, String first, String last) {
        labels[in++] = "Edible Ghost " + ghostIndex + " " + first + "?";
        labels[in++] = "Edible Ghost " + ghostIndex + " Right?";
        labels[in++] = "Edible Ghost " + ghostIndex + " " + last + "?";
        labels[in++] = "Edible Ghost " + ghostIndex + " Left?";

        labels[in++] = "Threat Ghost " + ghostIndex + " " + first + "?";
        labels[in++] = "Threat Ghost " + ghostIndex + " Right?";
        labels[in++] = "Threat Ghost " + ghostIndex + " " + last + "?";
        labels[in++] = "Threat Ghost " + ghostIndex + " Left?";
        return in;
    }

    @Override
    protected int coordLabels(int ghostIndex, String[] labels, int in) {
        labels[in++] = "Edible Ghost " + ghostIndex + " Lair Relative X Coord";
        labels[in++] = "Edible Ghost " + ghostIndex + " Lair Relative Y Coord";

        labels[in++] = "Threat Ghost " + ghostIndex + " Lair Relative X Coord";
        labels[in++] = "Threat Ghost " + ghostIndex + " Lair Relative Y Coord";
        return in;
    }
}
