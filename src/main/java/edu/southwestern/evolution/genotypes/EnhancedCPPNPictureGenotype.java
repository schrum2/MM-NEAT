package edu.southwestern.evolution.genotypes;
import java.util.ArrayList;

import com.twelvemonkeys.image.GraphicsUtil;

public class EnhancedCPPNPictureGenotype extends TWEANNPlusParametersGenotype<ArrayList<Double>> {

	public EnhancedCPPNPictureGenotype() {
		super(new TWEANNGenotype(), new BoundedRealValuedGenotype(new double[] {0, 2*Math.PI}, new double[] {0.1, 10}));	// Scale and rotation bounds
	}
	
}
