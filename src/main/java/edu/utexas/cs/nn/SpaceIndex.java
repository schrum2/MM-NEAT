package edu.utexas.cs.nn;

/**
 * A spatial lookup structure that speeds things up.
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public interface SpaceIndex {

    /**
     * Find the smallest Octree node that contains a given point
     * @param p point to look for
     * @return the smallest Octree node that contains a give point or null if \
     * the point is not in this tree
     */
    SpaceIndex findClosest(Point p);

    /**
     * Select a point at random anywhere within this tree
     * @return a random point from within this tree's bounds
     */
    Point findRandomPoint();

    /**
     * Select the closest point in the tree
     */
    Point findClosestPoint(Point p);

    /**
     * Get the number of points within the bounds of this tree
     * @return the number of points within the bounds of this tree
     */
    int size();

}
