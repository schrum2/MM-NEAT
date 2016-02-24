package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import java.util.Arrays;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class IncomingGhostSensorBlock extends MsPacManSensorBlock {

    private final boolean threats;
    private final boolean opposite;

    public IncomingGhostSensorBlock(boolean threats) {
        this(threats, false);
    }

    public IncomingGhostSensorBlock(boolean threats, boolean opposite) {
        this.opposite = opposite;
        this.threats = threats;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int currentDir) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? currentDir : 0;
        final int current = gf.getPacmanCurrentNodeIndex();
        final int[] neighbors = gf.neighbors(current);

        boolean[] approachingFromDirection = new boolean[GameFacade.NUM_DIRS];
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            if (threats ? gf.isGhostThreat(i) : gf.isGhostEdible(i)) {
                int[] ghostPathToPacMan = gf.getGhostPath(i, current);
                assert gf.getGhostCurrentNodeIndex(i) == current || ghostPathToPacMan.length > 0 : "Ghost Path too short: ghost index: " + i + ":ghost loc:" + gf.getGhostCurrentNodeIndex(i) + ":pacman loc:" + current + ":threats:" + threats;
                assert ghostPathToPacMan[ghostPathToPacMan.length - 1] == current : "Ghost path does not end in pacman's location: " + current + ":" + Arrays.toString(ghostPathToPacMan);
                //gf.addPoints(CombinatoricUtilities.colorFromInt(i), ghostPathToPacMan);
                int source = ghostPathToPacMan.length > 1 ? ghostPathToPacMan[ghostPathToPacMan.length - 2] : gf.getGhostCurrentNodeIndex(i);
                int dir = gf.getNextPacManDirTowardsTarget(source);
                // Compare shortest distance to path distance to see if approaching via shortest route
                double distanceFromPacMan = gf.getShortestPathDistance(gf.getGhostCurrentNodeIndex(i), current);
                approachingFromDirection[dir] = distanceFromPacMan == ghostPathToPacMan.length;
            }
        }

        for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
            int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
            boolean wall = neighbors[dir] == -1;
            if (wall) {
                inputs[in++] = -1;
            } else {
                inputs[in++] = (!opposite && approachingFromDirection[dir]) || (opposite && !approachingFromDirection[dir]) ? 1 : 0;
            }
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        String type = threats ? "Threat" : "Edible";

        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";

        labels[in++] = (opposite ? "Not " : "") + type + " Ghost Approaching From " + first + "?";
        labels[in++] = (opposite ? "Not " : "") + type + " Ghost Approaching From Right?";
        labels[in++] = (opposite ? "Not " : "") + type + " Ghost Approaching From " + last + "?";
        labels[in++] = (opposite ? "Not " : "") + type + " Ghost Approaching From Left?";

        return in;
    }

    public int numberAdded() {
        return GameFacade.NUM_DIRS;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o instanceof IncomingGhostSensorBlock) {
            IncomingGhostSensorBlock other = (IncomingGhostSensorBlock) o;
            return other.threats == this.threats && other.opposite == this.opposite;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.threats ? 1 : 0);
        hash = 97 * hash + (this.opposite ? 1 : 0);
        hash = 97 * hash + super.hashCode();
        return hash;
    }
}
