package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class GaierAutoencoderNeuronLossScaleRotationDeltaXDeltaYBinLabels implements BinLabels {
List<String> labels = null;
	
	// Call these from CPPNNeuronScaleRotationDeltaXDeltaYBinLabels?
	public static final int BIN_INDEX_NODES = 0;
	public static final int BIN_INDEX_LOSS = 1;
	public static final int BIN_INDEX_SCALE = 2;
	public static final int BIN_INDEX_ROTATION = 3;
	public static final int BIN_INDEX_DELTA_X = 4;
	public static final int BIN_INDEX_DELTA_Y = 5;
	public static final int MIN_NUM_NEURONS = 6;
	public static int numLossBins;
	
	public GaierAutoencoderNeuronLossScaleRotationDeltaXDeltaYBinLabels() {
		if(!Parameters.parameters.booleanParameter("trainingAutoEncoder")) {
			throw new IllegalStateException("You can't use the GaierAutoencoderPictureBinLabels binning scheme without training an autoencoder");
		}
		numLossBins = Parameters.parameters.integerParameter("numReconstructionLossBins");
	}

	/**
	 * Creates the bin labels using the number of neurons, number
	 * of loss bins, number of scale bins, number of rotation bins,
	 * and number of translation bins for both deltaX and deltaY.
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
					for(int k = 0; k < Parameters.parameters.doubleParameter("maxScale") / Parameters.parameters.integerParameter("scaleDivider"); k++) {
						for(int m = 0; m < 360; m++) {
							for(int n = 0; n < Parameters.parameters.doubleParameter("imageCenterTranslationRange"); n++) {
								for(int o = 0; o < Parameters.parameters.doubleParameter("imageCenterTranslationRange"); o++){
									labels.add("Neurons" + i + "loss" + j + "-scale" + k + "-rotation" + m + "-deltaX" + n + "-deltaY" + o);
									count++;
								}
							}
						}
					}
					labels.add("Neurons" + i + "loss" + j);
					count++;
				}
			}
			assert count == size : "Incorrect number of bins created in archive: " + count;
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
		return new String[] {"Neurons", "Reconstruction Loss", "Scale", "Rotation", "DeltaX", "DeltaY"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("maxNumNeurons") - CPPNComplexityBinLabels.MIN_NUM_NEURONS + 1, numLossBins};
	}
}
