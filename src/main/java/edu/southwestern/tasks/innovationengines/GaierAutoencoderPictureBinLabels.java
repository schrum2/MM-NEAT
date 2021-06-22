package edu.southwestern.tasks.innovationengines;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * This class provides a different bining scheme for
 * PictureTargetTask.  This class bins base off of neurons
 * and the loss value, and was inspired by the link below
 * (from the 2019 GECCO conference).
 * 
 * Adam Gaier, Alexander Asteroth, Jean-Baptiste Mouret, "Are quality diversity algorithms better at generating stepping stones than objective-based search?"
 * GECCO '19: Proceedings of the Genetic and Evolutionary Computation Conference Companion, July 2019. Pages 115–116
 * https://dl.acm.org/doi/10.1145/3319619.3321897
 * 
 * And, more details are in chapter 4 of Adam Gaier's dissertation:
 * https://hal.univ-lorraine.fr/tel-02964666/document
 * 
 * @author Anna Wicker
 *
 * @param <T>
 */
public class GaierAutoencoderPictureBinLabels implements BinLabels  {
	
	List<String> labels = null;
	
	public static final int BIN_INDEX_NODES = 0;
	public static final int BIN_INDEX_LOSS = 1;
	public static int numLossBins;
	
	public GaierAutoencoderPictureBinLabels() {
		if(!Parameters.parameters.booleanParameter("trainingAutoEncoder")) {
			throw new IllegalStateException("You can't use the GaierAutoencoderPictureBinLabels binning scheme without training an autoencoder");
		}
		numLossBins = Parameters.parameters.integerParameter("numReconstructionLossBins");
	}

	/**
	 * Creates the bin labels using the number of neurons and 
	 * the number of loss bins (1 / numLossBins).
	 * 
	 * @return A String list of the bin labels
	 */
	@Override
	public List<String> binLabels() {
		if(labels ==  null) {
			int size = (Parameters.parameters.integerParameter("maxNumNeurons") - CPPNComplexityBinLabels.MIN_NUM_NEURONS + 1) * numLossBins;
			System.out.println("Archive Size: " + size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int i = CPPNComplexityBinLabels.MIN_NUM_NEURONS; i <= Parameters.parameters.integerParameter("maxNumNeurons"); i++) {
				for(int j = 0; j < numLossBins; j++) {
//					BigDecimal secondSegment = new BigDecimal(j);
//					BigDecimal thirdSegment = new BigDecimal(j + 1);
//					secondSegment = secondSegment.divide(new BigDecimal(numLossBins));
//					thirdSegment = thirdSegment.divide(new BigDecimal(numLossBins));
//					labels.add("Neurons" + i + "loss" + secondSegment.setScale(4, RoundingMode.HALF_UP).doubleValue() + "-"+ thirdSegment.setScale(4, RoundingMode.HALF_UP).doubleValue());

					
					// Because the loss range can dynamically readjust, we just label each bin with an integer for simplicity
					labels.add("Neurons" + i + "loss" + j);
					count++;
				}
			}
			assert count == size : "Incorrect number of bins created in archive: " + count;
			
			// Not needed when bin labels associated with loss are simple integers
//			for(int i = 0; i < labels.size(); i++) {
//				// The \\. is an escape sequence for \., which in a regex is an escape sequence for just .
//				// Replaces periods with underscores
//				labels.set(i, labels.get(i).replaceAll("\\.", "_"));
//			}
		}
		return labels;
	}

	/**
	 * Calculating the index of the correct bin label
	 * in an int array multi.  
	 * 
	 * @param multi An array containing all of the possible bin labels
	 * @return The index in multi containing the correct bin
	 */
	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = ((multi[BIN_INDEX_NODES] - CPPNComplexityBinLabels.MIN_NUM_NEURONS) * numLossBins + (multi[BIN_INDEX_LOSS]));
		assert binIndex >= 0 : "Negative index " + Arrays.toString(multi) + " -> " + binIndex;
		return binIndex;
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Neurons", "Reconstruction Loss"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("maxNumNeurons") - CPPNComplexityBinLabels.MIN_NUM_NEURONS + 1, numLossBins};
	}
}
