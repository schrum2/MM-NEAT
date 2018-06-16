package edu.southwestern.util;

import java.util.ArrayList;

public class DistanceUtil {

	/**
	 * @param vec1 first vector to be compared
	 * @param vec2 second vector to be compared
	 * @return the cosine similarity between vec1 and vec 2
	 */
	public static double getCosineSimilarity(ArrayList<Double> vec1, ArrayList<Double> vec2) {
		if (vec1.size() != vec2.size()) {
			throw new IllegalArgumentException();
		}
		double dividend = 0;
		double shorterMagnitude = 0; //magnitude of the shorter vector
		double longerMagnitude = 0; //magnitude of the longer vector
		for(int i = 0; i < vec2.size(); i++) {
			dividend += (vec1.get(i) * vec2.get(i));
			shorterMagnitude += (vec1.get(i) * vec1.get(i));
			longerMagnitude += (vec2.get(i) * vec2.get(i));
		}
		return dividend / Math.sqrt(shorterMagnitude) * Math.sqrt(longerMagnitude);
	}

	/**
	 * @param vec1 first vector to be compared
	 * @param vec2 second vector to be compared
	 * @param minkowskiVar parameter of Minkowski distance
	 * @return the Minkowski distance between vec1 and vec 2
	 */
	public static double getMinkowskiDistance(ArrayList<Double> vec1, ArrayList<Double> vec2, double minkowskiVar) {
		if (vec1.size() != vec2.size()) {
			throw new IllegalArgumentException();
		}
		double distance = 0;
		for (int i = 0; i < vec2.size(); i++) {
			distance += Math.abs(Math.pow(vec1.get(i) - vec2.get(i), minkowskiVar));
		}
		return Math.pow(distance, 1 / minkowskiVar);
	}

	/**
	 * resize either vector so that they are the same size
	 * @param shorter Any vector. Will ultimately hold the shorter vector
	 * @param longer Any vector. Will ultimately hold the longer vector
	 * @return array such that the first index is the shorter vector and the second index is the longer vector
	 */
	public static ArrayList<Double>[] resizeVector (ArrayList<Double> shorter, ArrayList<Double> longer) {
		
		if (shorter.size() != longer.size()) {
			if (shorter.size() > longer.size()) {
				ArrayList<Double> v = new ArrayList<Double>();
				v = shorter;
				shorter = longer;
				longer = v;
			}

			for (int i = shorter.size(); i < longer.size(); i++) {
				shorter.add(0.0);
			}
		}
		@SuppressWarnings("unchecked")
		ArrayList<Double>[] list = (ArrayList<Double>[]) new ArrayList[2];
		list[0] = shorter;
		list[1] = longer;
		return list;
	}
}
