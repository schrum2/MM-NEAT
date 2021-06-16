package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;

public class GaierAutoencoderPictureBinLabelsTest {
	
	GaierAutoencoderPictureBinLabels labels;
	public static final int NUM_LOSS_BINS = 10;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"maxNumNeurons:100", "numReconstructionLossBins:"+NUM_LOSS_BINS, "trainingAutoEncoder:true"});
		labels = new GaierAutoencoderPictureBinLabels();
	}

	@Test
	public void testBinLabels() {
		// Might add later
	}

	@Test
	public void testOneDimensionalIndex() {
				
		int[] allNeuronValues = new int[] {5,5,6,19,79,10,70,28,76,20,35,56,14,68,84,99,38,59};
		double[] allLossValues = new double[] {0.01,0.11,0.53,0.24,0.48,0.18,0.10,0.01,0.29,0.04,0.99,0.35,0.85,0.82,0.57,0.95,0.73,0.64};
		String[] labelEnds = new String[] {"0.0,0.1","0.1,0.2","0.5,0.6","0.2,0.3","0.4,0.5","0.1,0.2","0.1,0.2","0.0,0.1","0.2,0.3","0.0,0.1","0.9,1.0","0.3,0.4","0.8,0.9","0.8,0.9","0.5,0.6","0.9,1.0","0.7,0.8","0.6,0.7"};
		
		assert allNeuronValues.length == allLossValues.length: "array lengths need to match";
		
		for(int i = 0; i < allNeuronValues.length; i++) {
			assertEquals("Neurons[" + allNeuronValues[i] + "]loss[" + labelEnds[i] + "]", labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i],(int) (allLossValues[i]*NUM_LOSS_BINS)})));
		}
	}

}
