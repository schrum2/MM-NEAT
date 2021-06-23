package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.*;

import org.junit.After;
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
	
	@After
	public void tearDown() throws Exception {
		Parameters.parameters = null; // Important that trainingAutoEncoder is not true for other tests
	}

	@Test
	public void testBinLabels() {
		// Might add later
	}

	@Test
	public void testOneDimensionalIndex() {
				
		int[] allNeuronValues = new int[] {5,5,6,19,79,10,70,28,76,20,35,56,14,68,84,99,38,59};
		double[] allLossValues = new double[] {0.01,0.11,0.53,0.24,0.48,0.18,0.10,0.01,0.29,0.04,0.99,0.35,0.85,0.82,0.57,0.95,0.73,0.64};
		String[] labelEnds = new String[] {"0","1","5","2","4","1","1","0","2","0","9","3","8","8","5","9","7","6"};
		//String[] labelEnds = new String[] {"0_0-0_1","0_1-0_2","0_5-0_6","0_2-0_3","0_4-0_5","0_1-0_2","0_1-0_2","0_0-0_1","0_2-0_3","0_0-0_1","0_9-1_0","0_3-0_4","0_8-0_9","0_8-0_9","0_5-0_6","0_9-1_0","0_7-0_8","0_6-0_7"};
		
		assert allNeuronValues.length == allLossValues.length: "array lengths need to match";
		
		for(int i = 0; i < allNeuronValues.length; i++) {
			assertEquals("Neurons" + allNeuronValues[i] + "loss" + labelEnds[i], labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i],(int) (allLossValues[i]*NUM_LOSS_BINS)})));
		}
		Parameters.parameters.setBoolean("trainingAutoEncoder", false);
	}

}
