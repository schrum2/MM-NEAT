package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;

public class CPPNNeuronCountBinLabelsTest {

	CPPNNeuronCountBinLabels labels;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"maxNumNeurons:100", "trainingAutoEncoder:false"});
		labels = new CPPNNeuronCountBinLabels();
	}
	
	@Test
	public void testBinLabels() {
		//fail("Not yet implemented");
	}

	@Test
	public void testOneDimensionalIndex() {
		//fail("Not yet implemented");
		int[] allNeuronValues = new int[] {5,32,34,53,97,41,44,62,56,99,24,6,15,74,42,53,57,29,81,20,92,47,74,18,34,72,88,78,67};
		
		for(int i = 0; i < allNeuronValues.length; i++) {
			assertEquals("Neurons" + allNeuronValues[i], labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i]})));
		}
	}

}
