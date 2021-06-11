package edu.southwestern.tasks.innovationengines;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import autoencoder.python.AutoEncoderProcess;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

/**
 * This class provides a different bining scheme for
 * PictureTargetTask.  This class bins base off of neurons
 * and the loss value, and was inspired by the link below
 * (from the 2019 GECCO conference).
 * 
 * https://dl.acm.org/doi/10.1145/3319619.3321897
 * 
 * @author Anna Wicker
 *
 * @param <T>
 */
public class GaierAutoencoderPictureBinningScheme<T extends Network> implements BinLabels  {
	
	List<String> labels = null;
	
	public static int MAX_NUM_NEURONS;
	public static double lossValue;
	public static int numLossBins;
	public static final int MIN_NUM_NEURONS = 5;
	
	public GaierAutoencoderPictureBinningScheme(BufferedImage image) {
		MAX_NUM_NEURONS = Parameters.parameters.integerParameter("maxNumNeurons");
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
			int size = (MAX_NUM_NEURONS - MIN_NUM_NEURONS + 1) * numLossBins;
			System.out.println("Archive Size: " + size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int i = MIN_NUM_NEURONS; i <= MAX_NUM_NEURONS; i++) {
				for(int j = 0; j < numLossBins; j++) {
					BigDecimal secondSegment = new BigDecimal(j);
					BigDecimal thirdSegment = new BigDecimal(j + 1);
					secondSegment = secondSegment.divide(new BigDecimal(numLossBins));
					thirdSegment = thirdSegment.divide(new BigDecimal(numLossBins));
					labels.add("Neurons[" + i + "]loss[" + secondSegment.setScale(4, RoundingMode.HALF_UP).doubleValue() + ","+ thirdSegment.setScale(4, RoundingMode.HALF_UP).doubleValue()+"]");
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
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
