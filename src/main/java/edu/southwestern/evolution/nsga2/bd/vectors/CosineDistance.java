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

	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] shorterAndLonger = DistanceUtil.getShorterAndLonger(v, ((RealBehaviorVector) rhs).v);
		return 1 - DistanceUtil.getCosineSimilarity(shorterAndLonger[0], shorterAndLonger[1]);
	}

}
