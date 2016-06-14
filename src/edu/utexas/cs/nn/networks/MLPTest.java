package edu.utexas.cs.nn.networks;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;

public class MLPTest {

	MLP mlp;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "mlpMutationRate:0.3"  });
		MMNEAT.loadClasses();
		mlp = new MLP(2, 3, 2);
	}

	@After
	public void tearDown() throws Exception {
		mlp = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void testNumInputs() {
		assertTrue(mlp.numInputs() == 2);
	}

	@Test
	public void testNumOutputs() {
		assertTrue(mlp.numOutputs() == 2);
	}

	@Test
	public void testProcess() {
		double[] calculatedOutputs = mlp.process(new double[] {2.0, 1.0});
		//System.out.println(mlp.);
		double[] realOutputs = {};
	}

	@Test
	public void testInitializeAllLayersRandom() {
		fail("Not yet implemented");
	}

	@Test
	public void testCopy() {
		fail("Not yet implemented");
	}

}
