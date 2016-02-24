/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.time;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import java.util.Arrays;
import pacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class LairTimesBlock extends MsPacManSensorBlock {

    private final boolean[] mask;
    private final int absence;

    @Override
    public boolean equals(MsPacManSensorBlock o) {
        if (o != null && o.getClass() == this.getClass()) {
            LairTimesBlock other = (LairTimesBlock) o;
            return Arrays.equals(this.mask, other.mask);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.hashCode(this.mask);
        hash = 97 * hash + super.hashCode();
        return hash;
    }

    public LairTimesBlock(boolean[] mask) {
        this.mask = mask;
        this.absence = Parameters.parameters.booleanParameter("absenceNegative") ? -1 : 0;
    }

    public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
        int[] times = gf.getGhostLairTimes();
        Arrays.sort(times); // smallest to highest
        //System.out.println(Arrays.toString(mask) + Arrays.toString(times) + ":" + Constants.COMMON_LAIR_TIME);
        for (int i = times.length - 1; i >= 0; i--) {
            if (mask[i]) {
                //inputs[in++] = times[i] == 0 ? absence : times[i] / (Constants.COMMON_LAIR_TIME * (Math.pow(Constants.LAIR_REDUCTION, gf.getCurrentLevel())));
                inputs[in++] = times[i] == 0 ? absence : Math.min(times[i], Constants.COMMON_LAIR_TIME) / (Constants.COMMON_LAIR_TIME * 1.0);
            }
        }
        return in;
    }

    public int incorporateLabels(String[] labels, int in) {
        for (int i = 0; i < mask.length; i++) {
            if (mask[i]) {
                labels[in++] = i + " Highest Lair Time";
            }
        }
        return in;
    }

    public int numberAdded() {
        int total = 0;
        for (int i = 0; i < mask.length; i++) {
            if (mask[i]) {
                total++;
            }
        }
        return total;
    }
}
