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

		int[] allNeuronValues = new int[] {7 ,34,36 ,55,99,43 ,46 ,64,58,100,26 ,8 ,15,76,44,55,59,31,83,22,94,49,76,20,36,74,90,80,69};
		int[] allLinkValues = new int[]   {10,35,123,68,64,194,199,65,38,190,150,35,74,91,53,46,141,23,93,58,184,127,8,4,6,12,9,45,5};
		
		assert allNeuronValues.length == allLinkValues.length: "array lengths need to match";
		
		for(int i = 0; i < allNeuronValues.length; i++) {
			assertEquals("Neurons" + allNeuronValues[i] + "links" + allLinkValues[i], labels.binLabels().get(labels.oneDimensionalIndex(new int[] {allNeuronValues[i],allLinkValues[i]})));
		}
	
	}

}
