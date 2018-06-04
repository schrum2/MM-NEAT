package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.ArrayList;

import edu.southwestern.util.DistanceUtil;

public class MinkowskiDistance  extends RealBehaviorVector{
	//MINKOWSKI_VAR = 1 is manhattan distance
	//MMINKOWSKI_VAR = 2 is Euclidean distance
	private final double MINKOWSKI_VAR = 1;
	
	public MinkowskiDistance(ArrayList<Double> v) {
		super(v);
	}

	public MinkowskiDistance(double[] d) {
		super(d);
	}
	
	public MinkowskiDistance(int[] i) {
		super(i);
	}
	
	/**
	 * Minkwosi sitance is an optional way to measure similarity for behavioral diversity
	 * @param vector that calling vector will be compared with
	 * @return distance between calling vector and param vector 
	 */
	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] twoVectors = DistanceUtil.resizeVector(v, ((RealBehaviorVector) rhs).v);
		return DistanceUtil.getMinkowskiDistance(twoVectors[0], twoVectors[1], MINKOWSKI_VAR);
	}

}
