package edu.southwestern.evolution.nsga2.bd.vectors;

import edu.southwestern.util.CartesianGeometricUtilities;
import java.util.ArrayList;

/**
 * Behavior is characterized by a sequence of real-valued numbers.
 * 
 * @author Jacob Schrum
 */
public class RealBehaviorVector implements BehaviorVector {

	private final ArrayList<Double> v;

	/**
	 * Characterization of agent behavior consisting of a fixed-length
	 * number of real (double) values. For use with the Behavioral Diversity
	 * approach.
	 * 
	 * @param v An ArrayList of Doubles
	 */
	public RealBehaviorVector(ArrayList<Double> v) {
		this.v = v;
	}

	/**
	 * Takes the provided array values and places them
	 * in the array list of the behavior vector.
	 * 
	 * @param i An Array of Integers
	 */
	public RealBehaviorVector(int[] i) {
		this.v = new ArrayList<Double>(i.length);
		for (int j = 0; j < i.length; j++) {
			v.add(new Double(i[j]));
		}
	}

	/**
	 * Takes the provided array values and places them
	 * in the array list of the behavior vector
	 * 
	 * @param d An Array of Doubles
	 */
	public RealBehaviorVector(double[] d) {
		this.v = new ArrayList<Double>(d.length);
		for (int j = 0; j < d.length; j++) {
			v.add(new Double(d[j]));
		}
	}

	/**
	 * Calculates the Euclidean distance between this
	 * behavior and the provided behavior vector. The
	 * shorter of the two vectors is padded with zeros
	 * first if necessary. The resulting return value is
	 * the distance between the two vectors in behavior
	 * space.
	 * 
	 * @return The distance between the two vectors in behavior space.
	 * 
	 */
	public double distance(BehaviorVector rhs) {
		ArrayList<Double> shorter = v;
		ArrayList<Double> longer = ((RealBehaviorVector) rhs).v;
		if (shorter.size() != longer.size()) {
			if (shorter.size() > longer.size()) {
				shorter = longer;
				longer = v;
			}

			for (int i = shorter.size(); i < longer.size(); i++) {
				shorter.add(0.0);
			}
		}
		return CartesianGeometricUtilities.euclideanDistance(shorter, longer);
	}
}
