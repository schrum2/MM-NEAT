package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.ArrayList;
import edu.southwestern.util.DistanceUtil;

public class AngularCosineSimilarity extends RealBehaviorVector {

	public AngularCosineSimilarity(ArrayList<Double> v) {
		super(v);
	}

	public AngularCosineSimilarity(double[] d) {
		super(d);
	}
	
	public AngularCosineSimilarity(int[] i) {
		super(i);
	}
	
	/**
	 * Angular Cosine Similarity is an optional way to measure similarity for behavioral diversity
	 * @param vector that calling vector will be compared with
	 * @return distance between calling vector and param vector 
	 */
	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] twoVectors = DistanceUtil.resizeVector(v, ((RealBehaviorVector) rhs).v);
		return 1 - 1 / DistanceUtil.getCosineSimilarity(twoVectors[0], twoVectors[1]) * Math.PI;
	}
}
