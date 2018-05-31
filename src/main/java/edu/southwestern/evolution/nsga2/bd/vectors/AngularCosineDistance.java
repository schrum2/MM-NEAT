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
	
	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] shorterAndLonger = DistanceUtil.getShorterAndLonger(v, ((RealBehaviorVector) rhs).v);
		return 1 / DistanceUtil.getCosineSimilarity(shorterAndLonger[0], shorterAndLonger[1]) * Math.PI;
	}

}
