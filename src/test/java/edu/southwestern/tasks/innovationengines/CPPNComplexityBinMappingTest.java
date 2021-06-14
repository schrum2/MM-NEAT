package edu.southwestern.tasks.innovationengines;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;

public class CPPNComplexityBinMappingTest {

	CPPNComplexityBinMapping labels;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"maxNumNeurons:100", "maxNumLinks:200"});
		labels = new CPPNComplexityBinMapping();
	}

	@Test
	public void testBinLabels() {
		//fail("Not yet implemented");
	}

	@Test
	public void testOneDimensionalIndex() {

		int[] allNeuronValues = new int[] {32,4,53,0,1,44,62,56,99,24,6,15,74,42,53,57,29,81,20,92,47,74,18,34,72,88,78,67};
		int[] allLinkValues = new int[] {35,123,68,64,194,199,1,38,190,150,35,74,91,53,46,141,23,93,58,184,127,8,4,6,3,9,2,5};
		
		
		int numNeurons = 5;
		int numLinks = 10;
		assertEquals("Neurons[" + numNeurons + "]links[" + numLinks + "]", labels.binLabels().get(labels.oneDimensionalIndex(new int[] {numNeurons,numLinks})));
		
		for(int i = 0; i < allNeuronValues.length; i++) {
			for(int j = 0; j < allLinkValues.length; j++) {
				assert allNeuronValues.length == allLinkValues.length: "array lengths need to match";
				assertEquals("Neurons[" + numNeurons + "]links[" + numLinks + "]", labels.binLabels().get(labels.oneDimensionalIndex(new int[] {numNeurons,numLinks})));
			}
		}
	
	}

}
