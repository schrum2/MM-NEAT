/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class SpecificGhostsDirectionBlock extends MsPacManSensorBlock {

    private final int absence;
    private final boolean threats;
    private final boolean towards;

    public SpecificGhostsDirectionBlock(boolean threats, boolean towards) {
        this.towards = towards;
        this.threats = threats;
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int currentDir) {
        final int referenceDir = CommonConstants.relativePacmanDirections ? currentDir : 0;
        final int[] neighbors = gf.neighbors(gf.getPacmanCurrentNodeIndex());

        for (int i = 0; i < gf.getNumActiveGhosts(); i++) {
            int ghostLoc = gf.getGhostCurrentNodeIndex(i);
            boolean lair = gf.getGhostLairTime(i) > 0;
            int ghostDir = lair ? -1 : (towards ? gf.getNextPacManDirTowardsTarget(ghostLoc) : gf.getNextPacManDirAwayFromTarget(ghostLoc));
            boolean eligible = threats ? gf.isGhostThreat(i) : gf.isGhostEdible(i);
            for (int j = 0; j < GameFacade.NUM_DIRS; j++) {
                int dir = (referenceDir + j) % GameFacade.NUM_DIRS;
                boolean wall = neighbors[dir] == -1;
                if (wall) {
                    inputs[in++] = absence;
                } else {
                    inputs[in++] = eligible && ghostDir == dir ? 1 : 0;
                }
            }
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        String first = CommonConstants.relativePacmanDirections ? "Ahead" : "Up";
        String last = CommonConstants.relativePacmanDirections ? "Behind" : "Down";
        String type = threats ? "Threat" : "Edible";
        String dir = towards ? "Towards" : "Away";

        for (int j = 0; j < CommonConstants.numActiveGhosts; j++) {
            labels[in++] = "Ghost " + j + " " + type + " " + dir + " " + first + "?";
            labels[in++] = "Ghost " + j + " " + type + " " + dir + " Right?";
            labels[in++] = "Ghost " + j + " " + type + " " + dir + " " + last + "?";
            labels[in++] = "Ghost " + j + " " + type + " " + dir + " Left?";
        }
        return in;
    }

    public int numberAdded() {
        return GameFacade.NUM_DIRS * CommonConstants.numActiveGhosts;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o instanceof SpecificGhostsDirectionBlock) {
            SpecificGhostsDirectionBlock other = (SpecificGhostsDirectionBlock) o;
            return other.threats == this.threats && other.towards == this.towards;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.threats ? 1 : 0);
        hash = 61 * hash + (this.towards ? 1 : 0);
        hash = 61 * hash + super.hashCode();
        return hash;
    }
}
