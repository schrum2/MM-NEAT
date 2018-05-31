package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.ArrayList;
import edu.southwestern.util.DistanceUtil;

public class AngularCosineDistance extends RealBehaviorVector{
	
	public AngularCosineDistance(ArrayList<Double> v) {
		super(v);
	}

	public AngularCosineDistance(double[] d) {
		super(d);
	}
	
	public AngularCosineDistance(int[] i) {
		super(i);
	}
	
	/**
	 * Angular Cosine Distance is an optional way to measure similarity for behavioral diversity
	 * @param vector that calling vector will be compared with
	 * @return distance between calling vector and param vector 
	 */
	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] shorterAndLonger = DistanceUtil.resizeVector(v, ((RealBehaviorVector) rhs).v);
		return 1 / DistanceUtil.getCosineSimilarity(shorterAndLonger[0], shorterAndLonger[1]) * Math.PI;
	}

}
