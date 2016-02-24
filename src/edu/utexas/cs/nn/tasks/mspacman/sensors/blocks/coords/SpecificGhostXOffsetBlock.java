/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.coords;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw.MirroredXPosBlock;

/**
 *
 * @author Jacob
 */
public class SpecificGhostXOffsetBlock extends MsPacManSensorBlock{
    private final int ghostIndex;
    public SpecificGhostXOffsetBlock(int ghostIndex) {
        this.ghostIndex = ghostIndex;
    }

    @Override
    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int pacman = gf.getPacmanCurrentNodeIndex();
        int ghost = gf.getGhostCurrentNodeIndex(ghostIndex);
        int pX = gf.getNodeXCoord(pacman);
        int gX = gf.getNodeXCoord(ghost);
        inputs[in++] = (gX - pX) / (1.0 * MirroredXPosBlock.MAX_X_COORD);
        return in;
    }

    @Override
    public int incorporateLabels(String[] labels, int in) {
        labels[in++] = "X Offset to Ghost " + ghostIndex;
        return in;
    }

    @Override
    public int numberAdded() {
        return 1;
    }
}
