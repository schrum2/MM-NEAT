package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.raw;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 *
 * @author Jacob Schrum
 */
public class CurrentSectorBlock extends MsPacManSensorBlock {

    private final int horizontal;
    private final int vertical;

    public CurrentSectorBlock(int horizontal, int vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int current = gf.getPacmanCurrentNodeIndex();
        int pX = gf.getNodeXCoord(current);
        int pY = gf.getNodeYCoord(current);
        double xSize = 110.0 / horizontal; // 0 - 108
        double ySize = 120.0 / vertical; // 4 - 116

        for (int i = 0; i < horizontal; i++) {
            for (int j = 0; j < vertical; j++) {
                inputs[in++] = (i * xSize <= pX && pX < (i + 1) * xSize && j * ySize <= pY && pY < (j + 1) * ySize ? 1 : 0);
                //if(inputs[in-1] == 1) System.out.println("Sector ("+i+","+j+")");
            }
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        for (int i = 0; i < horizontal; i++) {
            for (int j = 0; j < vertical; j++) {
                labels[in++] = "In Sector (" + i + "," + j + ")";
            }
        }
        return in;
    }

    public int numberAdded() {
        return horizontal * vertical;
    }

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o instanceof CurrentSectorBlock) {
            CurrentSectorBlock other = (CurrentSectorBlock) o;
            return other.horizontal == this.horizontal && other.vertical == this.vertical;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.horizontal;
        hash = 37 * hash + this.vertical;
        hash = 37 * hash + super.hashCode();
        return hash;
    }
}
