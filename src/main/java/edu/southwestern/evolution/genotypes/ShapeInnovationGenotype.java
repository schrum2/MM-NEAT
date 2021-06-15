package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;

import edu.southwestern.networks.TWEANN;

/**
 * Used to evolve 3D shapes with an innovation engine. Two genotypes are combined.
 * The TWEANN is actually a CPPN, that generates a shape in the manner of the
 * ThreeDimensionalObjectBreeder. The second part of the genome is just a
 * vector that specifies some details of how the shape is displayed within
 * a 2D picture. 
 * 
 * @author Jacob Schrum
 *
 */
public class ShapeInnovationGenotype extends CombinedGenotype<TWEANN, ArrayList<Double>>{

	/**
	 * Default constructor
	 */
	public ShapeInnovationGenotype() {
		super(new TWEANNGenotype(), // Generates the shape 
			  new BoundedRealValuedGenotype()); 
	}

}
