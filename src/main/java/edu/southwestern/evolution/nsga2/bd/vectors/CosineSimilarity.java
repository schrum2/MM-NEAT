package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.ArrayList;

import edu.southwestern.parameters.Parameters;

/**
 * source: https://www.itl.nist.gov/div898/software/dataplot/refman2/auxillar/cosdist.htm
 * @author Devon
 *
 */
public class CosineSimilarity extends RealBehaviorVector{
	private final ArrayList<Double> v = new ArrayList<Double>();
	private enum similarityAndDistanceMetrics {cosineSimilarity, cosineDistance, angularCosineDistance, 
		angularCosineSimilarity, taxiCab, minkowski, hamming, squaredEuclidean};
	private final similarityAndDistanceMetrics CHOICE_METRIC = similarityAndDistanceMetrics.cosineSimilarity;
	
	
	/**
	 * 
	 * @param v
	 */
	public CosineSimilarity(ArrayList<Double> v) {
		super(v);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Is finding longer and shorter necessary?
	 * refactor this code into real behavior vector class?
	 * similarityAndDistanceMetric in Parameters?
	 */
	@Override
	public double distance(BehaviorVector rhs) {
		//figure out which vector is long and which is shorter
		ArrayList<Double> shorter = v;
	//	ArrayList<Double> longer = ((RealBehaviorVector) rhs).v;
		ArrayList<Double> longer = new ArrayList<Double>();
		if (shorter.size() != longer.size()) {
			if (shorter.size() > longer.size()) {
				shorter = longer;
				longer = v;
			}

			for (int i = shorter.size(); i < longer.size(); i++) {
				shorter.add(0.0);
			}
		}
		
		//find designated distance
		double distance = 0;
		switch (CHOICE_METRIC) {
		case cosineSimilarity:
			return getCosineSimilarity(shorter, longer);
		case cosineDistance:
			return 1 - getCosineSimilarity(shorter, longer);
		case angularCosineDistance:
			return 1 / getCosineSimilarity(shorter, longer) * Math.PI;
		case angularCosineSimilarity:
			return 1 - 1 / getCosineSimilarity(shorter, longer) * Math.PI;
		case taxiCab:
			return getMinkowskiDistance(shorter, longer, 1);
		case minkowski:
			final double MINKOWSKI_VAR = 3;
			return getMinkowskiDistance(shorter, longer,  MINKOWSKI_VAR);
		case hamming:
			for (int i = 0; i < longer.size(); i++) {
				distance += longer.get(i) == shorter.get(i)? 0 : 1;
			}
			return distance;
		default:
			//you broke it
			return Double.MAX_VALUE;
		}
	}
	
	
	private double getCosineSimilarity(ArrayList<Double> shorter, ArrayList<Double> longer) {
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
	
	private double getMinkowskiDistance(ArrayList<Double> shorter, ArrayList<Double> longer, double minkowskiVar) {
		double distance = 0;
		for (int i = 0; i < longer.size(); i++) {
			distance += Math.abs(Math.pow(shorter.get(i) - longer.get(i), minkowskiVar));
		}
		return Math.pow(distance, 1 / minkowskiVar);
	}
}
