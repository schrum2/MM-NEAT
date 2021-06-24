package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;

public class GaierAutoencoderNeuronLossScaleRotationDeltaXDeltaYBinLabelsTest {
	
	GaierAutoencoderNeuronLossScaleRotationDeltaXDeltaYBinLabels labels;
	public static final int NUM_LOSS_BINS = 10;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"numReconstructionLossBins:10", "maxNumNeurons:100", "numScaleIntervals:10", "numRotationIntervals:10", "numTranslationIntervals:10", "maxScale:50", "trainingAutoEncoder:true"});
		labels = new GaierAutoencoderNeuronLossScaleRotationDeltaXDeltaYBinLabels();
	}

	@Test
	public void testBinLabels() {
		//fail("Not yet implemented");
	}

	@Test
	public void testOneDimensionalIndex() {
		//fail("Not yet implemented");
		int[] allNeuronValues = new int[] {5,5,6,19,79,10,70,28,76,20,35,56,14,68,84,99,38,59};
		double[] allLossValues = new double[] {0.01,0.11,0.53,0.24,0.48,0.18,0.10,0.01,0.29,0.04,0.99,0.35,0.85,0.82,0.57,0.95,0.73,0.64};
		int[] labelEnds = new int[] {0,1,5,2,4,1,1,0,2,0,9,3,8,8,5,9,7,6};
		int[] allScaleValues = new int[] {1,3,8,7,9,4,1,3,9,5,4,6,2,3,4,9,2,4};
		int[] allRotationValues = new int [] {9,8,2,3,2,1,5,2,4,7,1,5,8,7,6,5,8,7};
		int[] allDeltaXValues = new int[] {0,1,2,3,4,5,6,7,8,9,2,8,7,5,4,1,3,7};
		int[] allDeltaYValues = new int[] {0,1,2,3,4,5,6,7,8,9,6,7,2,3,1,5,9,4};
		
		assert allNeuronValues.length == allLossValues.length: "array lengths need to match";
		assert allLossValues.length == allScaleValues.length: "array lengths need to match";
		assert allScaleValues.length == allRotationValues.length: "array lengths need to match";
		assert allRotationValues.length == allDeltaXValues.length: "array lengths need to match";
		assert allDeltaXValues.length == allDeltaYValues.length: "array lengths need to match";

		for(int i = 0; i < allNeuronValues.length; i++) {
//			System.out.println("Loss: "+allLossValues[i]);
//			System.out.println("Neurons" + allNeuronValues[i]+ "-loss" + labelEnds[i] +"-scale" + allScaleValues[i] + "-rotation" + allRotationValues[i] + "-deltaX" + allDeltaXValues[i] + 
//					"-deltaY" + allDeltaYValues[i]);
			assertEquals("N" + allNeuronValues[i]+ "L" + labelEnds[i] +"S" + allScaleValues[i] + "R" + allRotationValues[i] + "X" + allDeltaXValues[i] + "Y" + allDeltaYValues[i], 
					labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i],(int) (allLossValues[i]*NUM_LOSS_BINS),allScaleValues[i],allRotationValues[i],allDeltaXValues[i],allDeltaYValues[i]})));
		}
		Parameters.parameters.setBoolean("trainingAutoEncoder", false);
	}

	@Test
	public void testDimensions() {
		//fail("Not yet implemented");
	}

	@Test
	public void testDimensionSizes() {
		//fail("Not yet implemented");
	}

}
