package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.ArrayList;
import edu.southwestern.util.DistanceUtil;

public class CosineDistance extends RealBehaviorVector {
	
	public CosineDistance(ArrayList<Double> v) {
		super(v);
	}

	public CosineDistance(double[] d) {
		super(d);
	}
	
	public CosineDistance(int[] i) {
		super(i);
	}

	/**
	 * Cosine Distance is an optional way to measure similarity for behavioral diversity
	 * @param vector that calling vector will be compared with
	 * @return distance between calling vector and param vector 
	 */
	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] twoVectors = DistanceUtil.resizeVector(v, ((RealBehaviorVector) rhs).v);
		return 1 - DistanceUtil.getCosineSimilarity(twoVectors[0], twoVectors[1]);
	}

}
