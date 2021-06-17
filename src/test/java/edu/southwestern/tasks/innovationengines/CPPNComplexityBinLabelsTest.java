package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;

public class CPPNComplexityBinLabelsTest {

	CPPNComplexityBinLabels labels;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"maxNumNeurons:100", "maxNumLinks:200"});
		labels = new CPPNComplexityBinLabels();
	}

	@Test
	public void testBinLabels() {
		//fail("Not yet implemented");
	}

	@Test
	public void testOneDimensionalIndex() {

		int[] allNeuronValues = new int[] {5,32,34,53,97,41,44,62,56,99,24,6,15,74,42,53,57,29,81,20,92,47,74,18,34,72,88,78,67};
		int[] allLinkValues = new int[] {10,35,123,68,64,194,199,65,38,190,150,35,74,91,53,46,141,23,93,58,184,127,8,4,6,12,9,45,5};
		
		assert allNeuronValues.length == allLinkValues.length: "array lengths need to match";
		
		for(int i = 0; i < allNeuronValues.length; i++) {
			for(int j = 0; j < allLinkValues.length; j++) {
				assertEquals("Neurons[" + allNeuronValues[i] + "]links[" + allLinkValues[j] + "]", labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i],allLinkValues[j]})));
			}
		}
	
	}

}
