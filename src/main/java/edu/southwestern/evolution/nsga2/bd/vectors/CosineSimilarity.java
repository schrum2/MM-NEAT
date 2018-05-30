package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.ArrayList;

import edu.southwestern.util.DistanceUtil;

/**
 * source: https://www.itl.nist.gov/div898/software/dataplot/refman2/auxillar/cosdist.htm
 * @author Devon
 */
public class CosineSimilarity extends RealBehaviorVector{

	public CosineSimilarity(ArrayList<Double> v) {
		super(v);
	}

	public CosineSimilarity(double[] d) {
		super(d);
	}
	
	public CosineSimilarity(int[] i) {
		super(i);
	}
	

	/**
	 * Is finding longer and shorter necessary?
	 * refactor this code into real behavior vector class?
	 * similarityAndDistanceMetric in Parameters?
	 * constructor?
	 */
	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] shorterAndLonger = DistanceUtil.getShorterAndLonger(v, ((RealBehaviorVector) rhs).v);
		return DistanceUtil.getCosineSimilarity(shorterAndLonger[0], shorterAndLonger[1]);
	}
}
