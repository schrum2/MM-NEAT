package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;

public class GaierAutoencoderPictureBinningSchemeTest {
	
	GaierAutoencoderPictureBinningScheme labels;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"maxNumNeurons:100", "numReconstructionLossBins:200"});
		labels = new GaierAutoencoderPictureBinningScheme();
	}

	@Test
	public void testGaierAutoencoderPictureBinningScheme() {
		//fail("Not yet implemented");
	}

	@Test
	public void testBinLabels() {
		//fail("Not yet implemented");
	}

	@Test
	public void testOneDimensionalIndex() {
		//fail("Not yet implemented");
//		int[] allNeuronValues = new int[] {5,32,4,53,0,1,44,62,56,99,24,6,15,74,42,53,57,29,81,20,92,47,74,18,34,72,88,78,67};
//		int[] allLinkValues = new int[] {10,35,123,68,64,194,199,1,38,190,150,35,74,91,53,46,141,23,93,58,184,127,8,4,6,3,9,2,5};
//		
//		assert allNeuronValues.length == allLinkValues.length: "array lengths need to match";
//		
//		for(int i = 0; i < allNeuronValues.length; i++) {
//			for(int j = 0; j < allLinkValues.length; j++) {
//				assertEquals("Neurons[" + allNeuronValues[i] + "]links[" + allLinkValues[j] + "]", labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i],allLinkValues[j]})));
//			}
//		}
		
		int[] allNeuronValues = new int[] {6,19,79,10,70,28,76,20,35,56,14,68,84,99,38,59};
		double[] allLossValues = new double[] {0.53,0.24,0.48,0.18,0.10,0.01,0.29,0.04,0.99,0.35,0.85,0.82,0.57,0.95,0.73,0.64};
		String[] labels = new String[] {"0.5,0.6","0.2,0.3","0.4,0.5","0.1,0.2","0.1,0.2","0.0,0.1","0.2,0.3","0.0,0.1","0.9,1.0","0.3,0.4","0.8,0.9","0.8,0.9","0.5,0.6","0.9,1.0","0.7,0.8","0.6,0.7"};
		
		assert allNeuronValues.length == allLossValues.length: "array lengths need to match";
		
		for(int i = 0; i < allNeuronValues.length; i++) {
			for(int j = 0; j < allLossValues.length; j++) {
				assertEquals("Neurons[" + allNeuronValues[i] + "]loss[" + labels[i] + "]", labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i],(int) allLossValues[j]})));
			}
		}
	}

}
