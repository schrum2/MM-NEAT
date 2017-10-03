package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;

import edu.southwestern.networks.TWEANN;

public class ShapeInnovationGenotype extends CombinedGenotype<TWEANN, ArrayList<Double>>{

	public ShapeInnovationGenotype() {
		super(new TWEANNGenotype(), // Generates the shape 
			  new BoundedRealValuedGenotype(new double[]{0,0,0,0,0}, new double[]{1,1,1,1,1})); // Background color (first three) and pitch, heading
	}

}
