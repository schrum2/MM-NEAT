package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;

public class CPPNNeuronScaleRotationDeltaXDeltaYBinLabelsTest {
	
	CPPNNeuronScaleRotationDeltaXDeltaYBinLabels labels;
	public static final int NUM_LOSS_BINS = 10;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"maxNumNeurons:100", "numScaleIntervals:10", "numRotationIntervals:10", "numTranslationIntervals:10", "maxScale:50"});
		labels = new CPPNNeuronScaleRotationDeltaXDeltaYBinLabels();
	}

	@Test
	public void testCPPNNeuronScaleRotationDeltaXDeltaYBinLabels() {
		//fail("Not yet implemented");
	}

	@Test
	public void testBinLabels() {
		//fail("Not yet implemented");
	}

	@Test
	public void testOneDimensionalIndex() {
		int[] allNeuronValues = new int[] {5,5,6,19,79,10,70,28,76,20,35,56,14,68,84,99,38,59};
		int[] allScaleValues = new int[] {1,3,8,7,9,4,1,3,9,5,4,6,2,3,4,9,2,4};
		int[] allRotationValues = new int [] {9,8,2,3,2,1,5,2,4,7,1,5,8,7,6,5,8,7};
		int[] allDeltaXValues = new int[] {0,1,2,3,4,5,6,7,8,9,2,8,7,5,4,1,3,7};
		int[] allDeltaYValues = new int[] {0,1,2,3,4,5,6,7,8,9,6,7,2,3,1,5,9,4};
		
//		int[] allNeuronValues = new int[] {6,7,10,20,18,9,8,11,32,7,8,42,33,24,31,5,16,21};
//		int[] allScaleValues = new int[]  {0,8,0,0,0,0,6,0,0,0,0,5,0,0,0,0,3,0};
//		int[] allRotationValues = new int [] {0,0,6,0,0,0,0,1,0,0,0,0,5,0,0,0,0,1};
//		int[] allDeltaXValues = new int[] {0,0,0,4,0,0,0,0,2,0,0,0,0,4,0,0,0,0};
//		int[] allDeltaYValues = new int[] {0,0,0,0,7,0,0,0,0,9,0,0,0,0,8,0,0,0};
		
		assert allNeuronValues.length == allScaleValues.length: "array lengths need to match";
		assert allScaleValues.length == allRotationValues.length: "array lengths need to match";
		assert allRotationValues.length == allDeltaXValues.length: "array lengths need to match";
		assert allDeltaXValues.length == allDeltaYValues.length: "array lengths need to match";

		for(int i = 0; i < allNeuronValues.length; i++) {
			assertEquals("Neurons" + allNeuronValues[i]+ "-scale" + allScaleValues[i] + "-rotation" + allRotationValues[i] + "-deltaX" + allDeltaXValues[i] + 
					"-deltaY" + allDeltaYValues[i], labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i],allScaleValues[i],allRotationValues[i],allDeltaXValues[i],allDeltaYValues[i]})));
		}
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
