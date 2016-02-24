package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import java.util.*;
import pacman.Executor;
import pacman.game.Constants;

/**
 * @author Jacob Schrum
 */
public class DangerousAreaModeSelector extends MsPacManModeSelector {

    public static HashMap<Integer, HashMap<Integer, Integer>> scentMaps = null;
    public static double[] mazeAverages = null;
    public static final int DANGEROUS = 0;
    public static final int SAFE = 1;

    public DangerousAreaModeSelector() {
        super();
        scentMaps = new HashMap<Integer, HashMap<Integer, Integer>>();
        updateScentMaps();
    }

    public static void updateScentMaps() {
        if (scentMaps != null) {
            scentMaps = Executor.deaths.deathCount();
            mazeAverages = new double[Constants.NUM_MAZES];
            for (int i = 0; i < mazeAverages.length; i++) {
                if (scentMaps.containsKey(i)) { // maze present
                    HashMap<Integer, Integer> mazeDeaths = scentMaps.get(i); // (loc,count) pairs
                    double[] counts = ArrayUtil.doubleArrayFromList(Collections.list(Collections.enumeration(mazeDeaths.values())));
                    mazeAverages[i] = StatisticsUtilities.average(counts);
                }
            }
            System.out.println("Updating scent maps from file: " + Arrays.toString(mazeAverages));
        }

    }

    /**
     * Mode depends on the death count of pacman's current location, and whether
     * or not that count is above average for the level.
     *
     * @return
     */
    public int mode() {
        int maze = gs.getMazeIndex();
        if (scentMaps.containsKey(maze)) {
            HashMap<Integer, Integer> mazeDeaths = scentMaps.get(maze);
            int current = gs.getPacmanCurrentNodeIndex();
            if (mazeDeaths.containsKey(current)) {
                double count = mazeDeaths.get(current);
                int n = 1;
                // Counts from neighbors
                int[] neighbors = gs.neighbors(current);
                for (int i = 0; i < neighbors.length; i++) {
                    if (neighbors[i] != -1) {
                        if (mazeDeaths.containsKey(neighbors[i])) {
                            count += mazeDeaths.get(neighbors[i]);
                        }
                        n++;
                    }
                }
                if (count/n > mazeAverages[maze]) {
                    //System.out.println(count +" > " +mazeAverages[maze] + " deaths");
                    return DANGEROUS;
                }
            }
        }
        return SAFE;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[DANGEROUS] = PILL_SCORE; // Should switch to time
        result[SAFE] = GAME_SCORE;
        return result;
    }
}
