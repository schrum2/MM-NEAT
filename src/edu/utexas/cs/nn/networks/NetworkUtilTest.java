package edu.utexas.cs.nn.networks;

import java.util.Arrays;
import static org.junit.Assert.*;

import org.junit.Test;

public class NetworkUtilTest {

	@Test
	public void testPropagateOneStepSubstrateMLP() {
		double[][] fromLayer = {{1, 3}, {2, 4}};
		double[][] toLayer = {{10}, {9}};
		double[][][][] connection = new double[2][2][2][1];
                connection[0][0][0][0] = 0.1;
                connection[0][0][1][0] = 0.2;
                connection[0][1][0][0] = 0.3;
                connection[0][1][1][0] = 0.4;

                connection[1][0][0][0] = 0.5;
                connection[1][0][1][0] = 0.6;
                connection[1][1][0][0] = 0.7;
                connection[1][1][1][0] = 0.8;
                
		NetworkUtil.propagateOneStep(fromLayer, toLayer, connection);
//                for(int i = 0; i < toLayer.length; i++) {
//                    System.out.println(Arrays.toString(toLayer[i]));
//                }
		assertEquals(toLayer[0][0], 10 + 1*0.1 + 2*0.5 + 3*0.3 + 4*0.7, 0.00000001);
		assertEquals(toLayer[1][0],  9 + 1*0.2 + 2*0.6 + 3*0.4 + 4*0.8, 0.00000001);
	}

	@Test
	public void simpleTestPropagateOneStepSubstrateMLP() {
		double[][] fromLayer = {{0.5}};
		double[][][][] connection1 = { { {{-1}} } };
		double[][][][] connection2 = { { {{100}} } };
		double[][][][] connection3 = { { {{-100}} } };
		
		double[][] toLayer = {{1}};
		NetworkUtil.propagateOneStep(fromLayer, toLayer, connection1);
		double [][] networkOutput1 = toLayer;
                toLayer = new double[][]{{1}};
		NetworkUtil.propagateOneStep(fromLayer, toLayer, connection2);
		double [][] networkOutput2 = toLayer;
                toLayer = new double[][]{{1}};
		NetworkUtil.propagateOneStep(fromLayer, toLayer, connection3);
		double [][] networkOutput3 = toLayer;
                
		assertEquals(networkOutput1[0][0], 1+0.5*-1, 0.00000001);
		assertEquals(networkOutput2[0][0], 1+0.5*100, 0.00000001);
		assertEquals(networkOutput3[0][0], 1+0.5*-100, 0.00000001);
		
	}
}
