package edu.southwestern.networks;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;

public class ActivationFunctionsTests {
	
	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	// Checks extreme edges of range, and the center
	public static double[] keyActivationPoints = { -ActivationFunctions.SAFE_EXP_BOUND, 0,
			ActivationFunctions.SAFE_EXP_BOUND };

	@Test
	public void test_keypoints_sigmoid() {
		// outputs of sigmoid for inputs from keyActivationPoints
		double[] keyActivationAnswers = { 0, 0.5, 1 };

		for (int i = 0; i < keyActivationPoints.length; i++) {
			assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_SIGMOID, keyActivationPoints[i]), keyActivationAnswers[i], 0.001);
		}
	}

	@Test
	public void test_keypoints_sawtooth() {
		double[] keyActivationInputs = { -1, -1.5, 0, 0.7, 1, 1.666 };
		double[] keyActivationAnswers = { 0, .5, 0, .7, 0, .666 };
		assertEquals(keyActivationInputs.length, keyActivationAnswers.length);
		for (int i = 0; i < keyActivationAnswers.length; i++) {
			assertEquals(keyActivationAnswers[i], ActivationFunctions.activation(ActivationFunctions.FTYPE_SAWTOOTH, keyActivationInputs[i]), .00001);
		}
	}

	@Test
	public void test_keypoints_tanh() {
		// outputs of tanh for inputs from keyActivationPoints
		double[] keyActivationAnswers = { -1, 0, 1 };

		for (int i = 0; i < keyActivationPoints.length; i++) {
			assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_TANH, keyActivationPoints[i]), keyActivationAnswers[i], 0.001);
		}
	}

	@Test
	public void test_keypoints_fullLinear() {
		// outputs of fullLinear for inputs from keyActivationPoints
		double[] keyActivationAnswers = { -1, 0, 1 };

		for (int i = 0; i < keyActivationPoints.length; i++) {
			assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_PIECEWISE, keyActivationPoints[i]), keyActivationAnswers[i], 0.001);
		}
	}

	@Test
	public void test_keypoints_halfLinear() {
		double[] keyActivationAnswers = { 0, 0, .5, 1, 1 };
		double[] keyActivationInputs = { -100, 0, .5, 1, 100 };
		assertEquals(keyActivationAnswers.length, keyActivationInputs.length);
		for (int i = 0; i < keyActivationAnswers.length; i++) {
			assertEquals(keyActivationAnswers[i], ActivationFunctions.activation(ActivationFunctions.FTYPE_HLPIECEWISE, keyActivationInputs[i]), 0.001);
		}
	}

	@Test
	public void test_keypoints_quickSigmoid() {
		// outputs of quickSigmoid for inputs from keyActivationPoints
		double[] keyActivationAnswers = { 0, 0.5, 1 };

		for (int i = 0; i < keyActivationPoints.length; i++) {
			// epsilon bound larger because this activation function uses
			// approximations
			assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_APPROX, keyActivationPoints[i]), keyActivationAnswers[i], 0.015);
		}
	}

	@Test
	public void test_keypoints_fullQuickSigmoid() {
		// outputs of fullQuickSigmoid for inputs from keyActivationPoints
		double[] keyActivationAnswers = { -1, 0, 1 };

		for (int i = 0; i < keyActivationPoints.length; i++) {
			// epsilon bound larger because this activation function uses
			// approximations
			assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_FULLAPPROX, keyActivationPoints[i]), keyActivationAnswers[i], 0.015);
		}
	}

	/**
	 * sigmoid should be roughly equal to quickSigmoid
	 */
	@Test
	public void test_quickSigmoid_vs_sigmoid() {
		for (double i = -ActivationFunctions.SAFE_EXP_BOUND; i <= ActivationFunctions.SAFE_EXP_BOUND; i++) {
			assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_APPROX, i), ActivationFunctions.activation(ActivationFunctions.FTYPE_SIGMOID, i), 0.01);
		}
	}

	/**
	 * Tests rectified linear units function, commonly used in DNNs
	 */
	@Test
	public void test_ReLU(){
		double test1 = 1.00001;
		double test2 = -1.0001;
		double test3  = 0.0001;
		double test4 = -0.0001;
		double test5 = -56.009;
		double test6 = 56.009;
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_RE_LU, test1), Math.max(0, test1), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_RE_LU, test2), Math.max(0, test2), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_RE_LU, test3), Math.max(0, test3), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_RE_LU, test4), Math.max(0, test4), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_RE_LU, test5), Math.max(0, test5), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_RE_LU, test5), 0, .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_RE_LU, test6), Math.max(0, test6), .000001);
		
	}
	
	/**
	 * Tests leaky ReLU, allows for negative outputs unlike ReLU but scaled heavily
	 */
	@Test
	public void test_LeakyReLU() {
		double test1 = 1.00001;
		double test2 = -1.0001;
		double test3  = 0.0001;
		double test4 = -0.0001;
		double test5 = -56.009;
		double test6 = 56.009;
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_LEAKY_RE_LU, test1), test1, .00001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_LEAKY_RE_LU, test2), test2*.01, .00001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_LEAKY_RE_LU, test3), test3, .00001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_LEAKY_RE_LU, test4), test4 * .01, .00001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_LEAKY_RE_LU, test5), test5*.01, .00001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_LEAKY_RE_LU, test6), test6, .00001);
	}
	
	/**
	 *derivation of ReLU, ln(1 + e^x)
	 */
	@Test
	public void test_Softplus(){
		double test1 = 1.00001;
		double test2 = -1.0001;
		double test3  = 0.0001;
		double test4 = -0.0001;
		double test5 = -56.009;
		double test6 = 56.009;
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_SOFTPLUS, test1), Math.log(1 + Math.pow(Math.E, test1)), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_SOFTPLUS, test2), Math.log(1 + Math.pow(Math.E, test2)), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_SOFTPLUS, test3), Math.log(1 + Math.pow(Math.E, test3)), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_SOFTPLUS, test4), Math.log(1 + Math.pow(Math.E, test4)), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_SOFTPLUS, test5), Math.log(1 + Math.pow(Math.E, test5)), .000001);
		assertEquals(ActivationFunctions.activation(ActivationFunctions.FTYPE_SOFTPLUS, test6), Math.log(1 + Math.pow(Math.E, test6)), .000001);
	}
	
	/**
	 * tests full sawtooth function, output very similar to sawtooth but values are doubled
	 */
	@Test
	public void test_FullSawtooth() {
		double[] keyActivationInputs = { -1, -1.5, 0, 0.7, 1, 1.666 };
		double[] keyActivationAnswers = { 0, 1.0, 0, 1.4, 0, 1.332 };
		assertEquals(keyActivationInputs.length, keyActivationAnswers.length);
		for (int i = 0; i < keyActivationAnswers.length; i++) {
			assertEquals(keyActivationAnswers[i], ActivationFunctions.activation(ActivationFunctions.FTYPE_FULLSAWTOOTH, keyActivationInputs[i]), .00001);
		}
	}
	
	/**
	 * tests triangle wave function, tests are similar to full sawtooth because function is simply the absolute
	 * value of full sawtooth
	 */
	@Test
	public void test_TriangleWave() {
		double[] keyActivationInputs = { -1, -1.5, 0, 0.7, 1, 1.666 };
		double[] keyActivationAnswers = { 0, 1.0, 0, 1.4, 0, 1.332 };
		assertEquals(keyActivationInputs.length, keyActivationAnswers.length);
		for (int i = 0; i < keyActivationAnswers.length; i++) {
			assertEquals(keyActivationAnswers[i], ActivationFunctions.activation(ActivationFunctions.FTYPE_TRIANGLEWAVE, keyActivationInputs[i]), .00001);
		}
	}
	
	/**
	 * tests triangle wave function - avoiding points where frequency alternates because slope is infinite
	 * here so there is not a defined answer
	 */
	@Test
	public void test_SquareWave() {
		double[] keyActivationInputs = { -2.1, -1.4, 0.1, 0.7, 3.8, 1.666 };
		double[] keyActivationAnswers = { -1.0, -1.0, 1.0, -1.0, -1.0,  -1.0};
		assertEquals(keyActivationInputs.length, keyActivationAnswers.length);
		for (int i = 0; i < keyActivationAnswers.length; i++) {
			assertEquals(keyActivationAnswers[i], ActivationFunctions.activation(ActivationFunctions.FTYPE_SQUAREWAVE, keyActivationInputs[i]), .00001);
		}
	}
	
}
