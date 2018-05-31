package edu.southwestern.evolution.nsga2.bd.vectors;

import java.util.ArrayList;

import edu.southwestern.util.DistanceUtil;

public class MinkowskiDistance  extends RealBehaviorVector{
	//MINKOWSKI_VAR = 1 is manhattan distance
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
	
	@Override
	public double distance(BehaviorVector rhs) {
		ArrayList<Double>[] shorterAndLonger = DistanceUtil.getShorterAndLonger(v, ((RealBehaviorVector) rhs).v);
		return DistanceUtil.getMinkowskiDistance(shorterAndLonger[0], shorterAndLonger[1], MINKOWSKI_VAR);
	}

}
