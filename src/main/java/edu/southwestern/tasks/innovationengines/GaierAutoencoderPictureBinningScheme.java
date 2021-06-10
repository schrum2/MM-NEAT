package edu.southwestern.tasks.innovationengines;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import autoencoder.python.AutoEncoderProcess;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

public class GaierAutoencoderPictureBinningScheme<T extends Network> implements BinLabels  {
	
	List<String> labels = null;
	
	public static int MAX_NUM_NEURONS;
	public static double lossValue;
	public static final int MIN_NUM_NEURONS = 5;
	
	public GaierAutoencoderPictureBinningScheme(BufferedImage image) {
		MAX_NUM_NEURONS = Parameters.parameters.integerParameter("maxNumNeurons");
		// initialize the loss here?
		lossValue = AutoEncoderProcess.getReconstructionLoss(image);
	}

	@Override
	public List<String> binLabels() {
		if(labels ==  null) {
			int size = (MAX_NUM_NEURONS - MIN_NUM_NEURONS + 1);
			System.out.println("Archive Size: "+size);
			labels = new ArrayList<String>(size);
			int count = 0;
			for(int i = MIN_NUM_NEURONS; i <= MAX_NUM_NEURONS; i++) {
				labels.add("Neurons[" + i + "]loss[" + lossValue + "]");
				count++;
			}

			assert count == size : "Incorrect number of bins created in archive: " + count;
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
