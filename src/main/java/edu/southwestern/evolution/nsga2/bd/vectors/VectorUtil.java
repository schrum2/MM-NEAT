package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.ArrayList;

import edu.southwestern.util.datastructures.Pair;

public class VectorUtil {
	/**
	 * @param vector to be 
	 * @return A pair containing (index, absoluteValue) where absoluteValue = the maximum
	 * 		absolute value of the vector and index = value's index. If either
	 * 		is negative there was an error
	 */
	public static Pair<Integer, Double> maxAbsoluteValue(ArrayList<Double> vector) {
		Double maxValue = new Double(-1);
		Integer maxIndex = new Integer(-1);
		for(int i = 0; i < vector.size(); i++) {
			if (Math.abs(vector.get(i)) > maxValue) {
				maxValue = Math.abs(vector.get(i));
				maxIndex = i;
			}
		}
		return new Pair<Integer, Double>(maxIndex, maxValue);
	}
}
