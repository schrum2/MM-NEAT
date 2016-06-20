package edu.utexas.cs.nn.networks;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;

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
		mlp = new MLP(3, 5, 1);
		double[] inputs = {.7, -.5, .1};
		double[] outputs = mlp.process(inputs);
		double[] hidden = new double[mlp.firstConnectionLayer.length * mlp.firstConnectionLayer[0].length];
		double actualOutput = 0.0;
		int x = 0;
		for(int i = 0; i < mlp.firstConnectionLayer.length; i++) {
			for(int j = 0; j < mlp.firstConnectionLayer[0].length; j++) {
				actualOutput += Math.tanh(inputs[i]*mlp.firstConnectionLayer[i][j]);
			hidden[x++] = Math.tanh(inputs[i]*mlp.firstConnectionLayer[i][j]);
			}
		}
		double actualOutputs = 0;
		for(int i = 0; i < mlp.secondConnectionLayer.length; i++) { 
			for(int j = 0; j < mlp.secondConnectionLayer[0].length; j++) {
//				System.out.println("hidden size" + hidden.length);
//				MiscUtil.waitForReadStringAndEnterKeyPress();
				actualOutputs += actualOutput*mlp.secondConnectionLayer[i][j];
			}
		}
		actualOutputs = Math.tanh(actualOutputs);
		assertEquals(outputs[0], actualOutputs, .0000001);
	}
}
