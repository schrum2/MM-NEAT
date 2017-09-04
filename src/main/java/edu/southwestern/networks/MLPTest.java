package edu.southwestern.networks;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

public class MLPTest {

	MLP mlp;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "mlpMutationRate:0.3"});
		MMNEAT.loadClasses();
		mlp = new MLP(1, 1, 1);
	}

	@After
	public void tearDown() throws Exception {
		mlp = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void testNumInputs() {
		assertTrue(mlp.numInputs() == 1);
	}

	@Test
	public void testNumOutputs() {
		assertTrue(mlp.numOutputs() == 1);
	}

	@Test
	public void testProcess() {
		double[] calculatedOutputs = mlp.process(new double[] {0.5});
		double[] realOutputs = {Math.tanh(Math.tanh(mlp.inputs[0]*mlp.firstConnectionLayer[0][0])*mlp.secondConnectionLayer[0][0])};
		assertEquals(calculatedOutputs[0], realOutputs[0], .000001);
	}
	
	@Test
	public void testProcessComplex() { 
		mlp = null;
		mlp = new MLP(2, 2, 1);
		double[] inputs = {.7, -.5};
		double[] outputs = mlp.process(inputs);
		double p1 = Math.tanh(inputs[0]*mlp.firstConnectionLayer[0][0] + inputs[1]*mlp.firstConnectionLayer[1][0]);
		double p2 = Math.tanh(inputs[0]*mlp.firstConnectionLayer[0][1] + inputs[1]*mlp.firstConnectionLayer[1][1]);;
		double O = Math.tanh(p1*mlp.secondConnectionLayer[0][0] + p2*mlp.secondConnectionLayer[1][0]);
		assertEquals(O, outputs[0], .000001);
	}
}
