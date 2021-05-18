package edu.southwestern.tasks.functionoptimization;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * Binning scheme for Rastrigin function,
 * based on the description from
 * https://arxiv.org/pdf/1912.02400.pdf
 * 
 * @author Maxx Batterton
 *
 */
public class FunctionOptimizationRastriginBinLabels implements BinLabels {
	
	List<String> labels = null;
	private int binsPerDimension = 500;
	private int n = 20;
	private double rastriginRange = 5.12;
	
	@Override
	public List<String> binLabels() {
		double b = 5.12*n/binsPerDimension; // Calculate bin size
		if(labels == null) {
			int size = binsPerDimension*binsPerDimension;
			labels = new ArrayList<String>(size);
			for (int y = (binsPerDimension/2)-1; y >= -(binsPerDimension/2); y--) {
				for (int x = -(binsPerDimension/2); x < (binsPerDimension/2); x++) {
					labels.add("bin("+x+", "+y+")["+x*b+" to "+(x+1)*b+"]["+y*b+" to "+(y+1)*b+"]");
					// bin(-250, 249)[-51.2 to -50.9952][50.9952 to 51.2]
					// bin(0, 0)[0 to 0.2048][0 to 0.2048]
					// bin(249, -250)[50.9952 to 51.2][-51.2 to -50.9952]
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		// TODO Auto-generated method stub
		return 0;
	}
}
