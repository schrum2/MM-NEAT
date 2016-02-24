package edu.utexas.cs.nn.util.util2D;

import java.util.Comparator;

/**
 *
 * @author Jacob Schrum
 */
public class Distance2DComparator implements Comparator<ILocated2D> {

    private final ILocated2D reference;

    public Distance2DComparator(ILocated2D reference) {
        this.reference = reference;
    }

    public int compare(ILocated2D o1, ILocated2D o2) {
        if (reference == null || (o1 == null && o2 == null)) {
            return 0;
        } else if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        }
        return (int) Math.signum(o1.distance(reference) - o2.distance(reference));
    }
}
