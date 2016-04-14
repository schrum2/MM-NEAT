package edu.utexas.cs.nn.evolution.nsga2;

import java.util.Comparator;

/**
 * Allows for easy sorting based on crowding distance, which
 * NSGA2 uses to encourage spreading out across the Pareto front.
 *
 * @author Jacob Schrum
 */
public class CrowdingDistanceComparator implements Comparator<NSGA2Score> {

    public int compare(NSGA2Score o1, NSGA2Score o2) {
        return (int) Math.signum(o1.getCrowdingDistance() - o2.getCrowdingDistance());
    }
}
