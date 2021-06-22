package edu.southwestern.evolution.genotypes;
import java.util.ArrayList;

/**
 * When used with Picbreeder, creates a new image with different scale
 * factors and rotation factors.  Since it implements the network 
 * interface, Picbreeder can use the phenotype of this genotype to create 
 * and evolve the images (the BoundedRealValuedGenotype puts bounds on 
 * the scale factor and rotation factor while the TWEANN creates the 
 * image).
 * 
 * @author wickera
 *
 */
public class EnhancedCPPNPictureGenotype extends TWEANNPlusParametersGenotype<ArrayList<Double>> {

	public static final int INDEX_SCALE = 0;
	public static final int INDEX_ROTATION = 1;
	public static final int INDEX_DELTA_X = 2;
	public static final int INDEX_DELTA_Y = 3;
	
	/**
	 * Creates a new TWEANNGenotype and BoundedRealValuedGenotype.
	 * Together, this can create an image which can be scaled and 
	 * rotated within the bounds set by the BoundedRealValuedGenotype.
	 */
	public EnhancedCPPNPictureGenotype() {
		super(new TWEANNGenotype(), new BoundedRealValuedGenotype());
	}
	
}
