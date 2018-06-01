package edu.southwestern.util;

import java.util.ArrayList;



public class DistanceUtil {

	public static double getCosineSimilarity(ArrayList<Double> shorter, ArrayList<Double> longer) {
		double dividend = 0;
		double shorterMagnitude = 0; //magnitude of the shorter vector
		double longerMagnitude = 0; //magnitude of the longer vector
		for(int i = 0; i < longer.size(); i++) {
			dividend += (shorter.get(i) * longer.get(i));
			shorterMagnitude += (shorter.get(i) * shorter.get(i));
			longerMagnitude += (longer.get(i) * longer.get(i));
		}
		return dividend / Math.sqrt(shorterMagnitude) * Math.sqrt(longerMagnitude);
	}

	/**
	 * 
	 * @param shorter the shorter o
	 * @param longer
	 * @param minkowskiVar
	 * @return
	 */
	public static double getMinkowskiDistance(ArrayList<Double> shorter, ArrayList<Double> longer, double minkowskiVar) {
		double distance = 0;
		for (int i = 0; i < longer.size(); i++) {
			distance += Math.abs(Math.pow(shorter.get(i) - longer.get(i), minkowskiVar));
		}
		return Math.pow(distance, 1 / minkowskiVar);
	}

	/**
	 * figure out which vector is long and which is shorter
	 * @param shorter Any vector. Will ultimately hold the shorter vector
	 * @param longer Any vector. Will ultimately hold the longer vector
	 * @return array such that the first index is the shorter vector and the second index is the longer vector
	 */
	public static ArrayList<Double>[] getShorterAndLonger (ArrayList<Double> shorter, ArrayList<Double> longer) {
		
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
