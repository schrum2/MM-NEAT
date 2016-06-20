package edu.utexas.cs.nn.networks;

import static org.junit.Assert.*;

import org.junit.Test;

public class NetworkUtilTest {

	@Test
	public void testPropagateOneStepSubstrateMLP() {
		double[][] fromLayer = {{1, 2}, {3, 4}};
		double[][] toLayer = {{10}, {9}};
		double[][][][] connection = { { {{-1}, {-2}}, 
									    {{-3}, {-4}} }, 
									  { {{-5}, {-6}}, 
									    {{-7}, {-8}} } };
		double [][] networkOutput = NetworkUtil.propagateOneStep(fromLayer, toLayer, connection);
//		assertEquals(networkOutput[0][0], 10+1*-1, 0.00000001);
//		assertEquals(networkOutput[0][0], 10+2*-2, 0.00000001);
//		assertEquals(networkOutput[0][0], 10+3*-3, 0.00000001);
//		assertEquals(networkOutput[0][0], 10+4*-4, 0.00000001);
//		assertEquals(networkOutput[0][0], 9+5*-5, 0.00000001);
//		assertEquals(networkOutput[0][0], 9+0.6*-6, 0.00000001);
//		assertEquals(networkOutput[0][0], 9+7*-7, 0.00000001);
//		assertEquals(networkOutput[0][0], 9+8*-8, 0.00000001);
	}

	@Test
	public void simpleTestPropagateOneStepSubstrateMLP() {
		double[][] fromLayer = {{0.5}};
		double[][][][] connection1 = { { {{-1}} } };
		double[][][][] connection2 = { { {{100}} } };
		double[][][][] connection3 = { { {{-100}} } };
		
		double[][] toLayer = {{1}};
		double [][] networkOutput1 = NetworkUtil.propagateOneStep(fromLayer, toLayer, connection1);
		toLayer = new double[][]{{1}};
		double [][] networkOutput2 = NetworkUtil.propagateOneStep(fromLayer, toLayer, connection2);
		toLayer = new double[][]{{1}};
		double [][] networkOutput3 = NetworkUtil.propagateOneStep(fromLayer, toLayer, connection3);
		
		assertEquals(networkOutput1[0][0], 1+0.5*-1, 0.00000001);
		assertEquals(networkOutput2[0][0], 1+0.5*100, 0.00000001);
		assertEquals(networkOutput3[0][0], 1+0.5*-100, 0.00000001);
		
	}
}
