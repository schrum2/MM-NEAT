package edu.southwestern.util.util2D;

import java.util.Comparator;

/**
 *
 * @author Jacob Schrum
 */
public class Distance2DComparator implements Comparator<ILocated2D> {

	private final ILocated2D reference;

	/**
	 * constructor
	 * @param reference
	 * 				any object that has a 2d location
	 */
	public Distance2DComparator(ILocated2D reference) {
		this.reference = reference;
	}
	
	@Override
	/**
	 * compares two distances, each between the reference and a given point, 
	 * 
	 * @param o1 
	 * 			object in 2d space 
	 * @param o2 
	 * 			another object in 2d space
	 * @return comparison between (distance between (o1, reference)) and (distance between (o2, reference))
	 * 		1 : o1 is farther,
	 * 		0 : they are equally far from reference,
	 * 	   -1 : o2 is farther.
	 */
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
