package edu.utexas.cs.nn.networks;

import static org.junit.Assert.*;

import org.junit.Test;

public class ActivationFunctionsTests {
	// Checks extreme edges of range, and the center
	public static double[] keyActivationPoints = {-ActivationFunctions.SAFE_EXP_BOUND, 0, ActivationFunctions.SAFE_EXP_BOUND};

	@Test
	public void test_keypoints_sigmoid() {
		// outputs of sigmoid for inputs from keyActivationPoints
		double[] keyActivationAnswers = {0, 0.5, 1};
		
		for(int i = 0; i < keyActivationPoints.length; i++) {
			assertEquals(ActivationFunctions.sigmoid(keyActivationPoints[i]), keyActivationAnswers[i], 0.001);
		}
	}
	
	@Test
	public void test_keypoints_tanh() {
		// outputs of tanh for inputs from keyActivationPoints
		double[] keyActivationAnswers = {-1, 0, 1};
		
		for(int i = 0; i < keyActivationPoints.length; i++) {
			assertEquals(ActivationFunctions.tanh(keyActivationPoints[i]), keyActivationAnswers[i], 0.001);
		}
	}

	@Test
	public void test_keypoints_fullLinear() {
		// outputs of fullLinear for inputs from keyActivationPoints
		double[] keyActivationAnswers = {-1, 0, 1};
		
		for(int i = 0; i < keyActivationPoints.length; i++) {
			assertEquals(ActivationFunctions.fullLinear(keyActivationPoints[i]), keyActivationAnswers[i], 0.001);
		}
	}

	@Test
	public void test_keypoints_quickSigmoid() {
		// outputs of quickSigmoid for inputs from keyActivationPoints
		double[] keyActivationAnswers = {0, 0.5, 1};
		
		for(int i = 0; i < keyActivationPoints.length; i++) {
			// epsilon bound larger because this activation function uses approximations
			assertEquals(ActivationFunctions.quickSigmoid(keyActivationPoints[i]), keyActivationAnswers[i], 0.015);
		}
	}
	
	@Test
	public void test_keypoints_fullQuickSigmoid() {
		// outputs of fullQuickSigmoid for inputs from keyActivationPoints
		double[] keyActivationAnswers = {-1, 0, 1};
		
		for(int i = 0; i < keyActivationPoints.length; i++) {
			// epsilon bound larger because this activation function uses approximations
			assertEquals(ActivationFunctions.fullQuickSigmoid(keyActivationPoints[i]), keyActivationAnswers[i], 0.015);
		}
	}

	/**
	 * sigmoid should be roughly equal to quickSigmoid
	 */
	@Test
	public void test_quickSigmoid_vs_sigmoid() {
		for(double i = -ActivationFunctions.SAFE_EXP_BOUND; i <= ActivationFunctions.SAFE_EXP_BOUND; i++) {
			assertEquals(ActivationFunctions.quickSigmoid(i), ActivationFunctions.sigmoid(i), 0.01);
		}
	}

}
