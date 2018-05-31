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
	 * Cosine Similarity is an optional way to measure similarity for behavioral diversity
	 * @param vector that calling vector will be compared with
	 * @return distance between calling vector and param vector 
	 */
	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] shorterAndLonger = DistanceUtil.resizeVector(v, ((RealBehaviorVector) rhs).v);
		return DistanceUtil.getCosineSimilarity(shorterAndLonger[0], shorterAndLonger[1]);
	}
}
