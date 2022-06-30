package edu.southwestern.networks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.nd4j.linalg.activations.Activation;

import edu.southwestern.networks.activationfunctions.ActivationFunction;
import edu.southwestern.networks.activationfunctions.*;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Contains activation functions used by neural networks.
 * 
 * @author Jacob Schrum Edits by Gabby Gonzalez and Lauren Gillespie
 */
public class ActivationFunctions {

	public static final int MAX_POSSIBLE_ACTIVATION_FUNCTIONS = 29;

	/**
	 * Initialize the array list for all ftypes
	 */
	public static ArrayList<Integer> availableActivationFunctions = new ArrayList<>(MAX_POSSIBLE_ACTIVATION_FUNCTIONS);

	/**
	 * Initialize the ftypes to be available for the CPPN/TWEANN
	 */
	public static final int FTYPE_SIGMOID = 0;
	public static final int FTYPE_TANH = 1;
	public static final int FTYPE_ID = 2;
	public static final int FTYPE_FULLAPPROX = 3;
	public static final int FTYPE_APPROX = 4;
	public static final int FTYPE_GAUSS = 5;
	public static final int FTYPE_SINE = 12;
	public static final int FTYPE_ABSVAL = 13;
	public static final int FTYPE_PIECEWISE = 14;
	public static final int FTYPE_HLPIECEWISE = 15;
	public static final int FTYPE_SAWTOOTH = 16;
	public static final int FTYPE_STRETCHED_TANH = 17;
	public static final int FTYPE_RE_LU = 18;
	public static final int FTYPE_SOFTPLUS  = 19;
	public static final int FTYPE_LEAKY_RE_LU = 20;
	public static final int FTYPE_FULLSAWTOOTH = 21;
	public static final int FTYPE_TRIANGLEWAVE = 22;
	public static final int FTYPE_SQUAREWAVE = 23;
	public static final int FTYPE_FULLSIGMOID = 24;
	public static final int FTYPE_FULLGAUSS = 25;
	public static final int FTYPE_COS = 26;
	public static final int FTYPE_SIL = 27;
	public static final int FTYPE_DSIL = 28;

	private static HashMap<Integer,ActivationFunction> functionMap;
	
	// When loaded, fill HashMap with instances of each function
	static {
		functionMap = new HashMap<>();
		functionMap.put(FTYPE_SIGMOID, new SigmoidFunction());
		functionMap.put(FTYPE_TANH, new TanHFunction());
		functionMap.put(FTYPE_ID, new IDFunction());
		functionMap.put(FTYPE_FULLAPPROX, new FullQuickSigmoidFunction());
		functionMap.put(FTYPE_APPROX, new QuickSigmoidFunction());
		functionMap.put(FTYPE_GAUSS, new GaussianFunction());
		functionMap.put(FTYPE_SINE, new SineFunction());
		functionMap.put(FTYPE_ABSVAL, new ClippedAbsValFunction());
		functionMap.put(FTYPE_PIECEWISE, new FullLinearPiecewiseFunction());
		functionMap.put(FTYPE_HLPIECEWISE, new HalfLinearPiecewiseFunction());
		functionMap.put(FTYPE_SAWTOOTH, new SawtoothFunction());
		functionMap.put(FTYPE_STRETCHED_TANH, new StretchedTanHFunction());
		functionMap.put(FTYPE_RE_LU, new ReLUFunction());
		functionMap.put(FTYPE_SOFTPLUS, new SoftplusFunction());
		functionMap.put(FTYPE_LEAKY_RE_LU, new LeakyReLUFunction());
		functionMap.put(FTYPE_FULLSAWTOOTH, new FullSawtoothFunction());
		functionMap.put(FTYPE_TRIANGLEWAVE, new TriangleWaveFunction());
		functionMap.put(FTYPE_SQUAREWAVE, new SquareWaveFunction());
		functionMap.put(FTYPE_FULLSIGMOID, new FullSigmoidFunction());
		functionMap.put(FTYPE_FULLGAUSS, new FullGaussianFunction());
		functionMap.put(FTYPE_COS, new CosineFunction());
		functionMap.put(FTYPE_SIL, new SiLFunction());
		functionMap.put(FTYPE_DSIL, new DSiLFunction());
	}
	
	/**
	 * List of all possible activation functions, including those that are
	 * currently disabled.
	 * @return List of integers corresponding to the ftypes of the functions
	 */
	public static List<Integer> allPossibleActivationFunctions() {
		List<Integer> list = new LinkedList<>();
		list.addAll(functionMap.keySet());
		return list;
	}
	
	/**
	 * Initializes the set of ftypes by checking boolean parameters for included
	 * functions
	 */
	public static void resetFunctionSet() {
		availableActivationFunctions = new ArrayList<>(MAX_POSSIBLE_ACTIVATION_FUNCTIONS);
		if (Parameters.parameters.booleanParameter("includeSigmoidFunction")) {
			availableActivationFunctions.add(FTYPE_SIGMOID);
		}
		if (Parameters.parameters.booleanParameter("includeTanhFunction")) {
			availableActivationFunctions.add(FTYPE_TANH);
		}
		if (Parameters.parameters.booleanParameter("includeIdFunction")) {
			availableActivationFunctions.add(FTYPE_ID);
		}
		if (Parameters.parameters.booleanParameter("includeFullApproxFunction")) {
			availableActivationFunctions.add(FTYPE_FULLAPPROX);
		}
		if (Parameters.parameters.booleanParameter("includeApproxFunction")) {
			availableActivationFunctions.add(FTYPE_APPROX);
		}
		if (Parameters.parameters.booleanParameter("includeGaussFunction")) {
			availableActivationFunctions.add(FTYPE_GAUSS);
		}
		if (Parameters.parameters.booleanParameter("includeSineFunction")) {
			availableActivationFunctions.add(FTYPE_SINE);
		}
		if (Parameters.parameters.booleanParameter("includeAbsValFunction")) {
			availableActivationFunctions.add(FTYPE_ABSVAL);
		}
		if (Parameters.parameters.booleanParameter("includeFullLinearPiecewiseFunction")) {
			availableActivationFunctions.add(FTYPE_PIECEWISE);
		}
		if (Parameters.parameters.booleanParameter("includeHalfLinearPiecewiseFunction")) {
			availableActivationFunctions.add(FTYPE_HLPIECEWISE);
		}
		if (Parameters.parameters.booleanParameter("includeSawtoothFunction")) {
			availableActivationFunctions.add(FTYPE_SAWTOOTH);
		}
		if(Parameters.parameters.booleanParameter("includeStretchedTanhFunction")) {
			availableActivationFunctions.add(FTYPE_STRETCHED_TANH);
		}
		if(Parameters.parameters.booleanParameter("includeReLUFunction")) {
			availableActivationFunctions.add(FTYPE_RE_LU);
		}
		if(Parameters.parameters.booleanParameter("includeSoftplusFunction")) {
			availableActivationFunctions.add(FTYPE_SOFTPLUS);
		}
		if(Parameters.parameters.booleanParameter("includeLeakyReLUFunction")) {
			availableActivationFunctions.add(FTYPE_LEAKY_RE_LU);
		}
		if(Parameters.parameters.booleanParameter("includeFullSawtoothFunction")) {
			availableActivationFunctions.add(FTYPE_FULLSAWTOOTH);
		}
		if(Parameters.parameters.booleanParameter("includeTriangleWaveFunction")) {
			availableActivationFunctions.add(FTYPE_TRIANGLEWAVE);
		}
		if(Parameters.parameters.booleanParameter("includeSquareWaveFunction")) {
			availableActivationFunctions.add(FTYPE_SQUAREWAVE);
		}
		if (Parameters.parameters.booleanParameter("includeFullSigmoidFunction")) {
			availableActivationFunctions.add(FTYPE_FULLSIGMOID);
		}
		if (Parameters.parameters.booleanParameter("includeFullGaussFunction")) {
			availableActivationFunctions.add(FTYPE_FULLGAUSS);
		}
		if (Parameters.parameters.booleanParameter("includeCosineFunction")) {
			availableActivationFunctions.add(FTYPE_COS);
		}
		if (Parameters.parameters.booleanParameter("includeSiLFunction")) {
			availableActivationFunctions.add(FTYPE_SIL);
		}
		if (Parameters.parameters.booleanParameter("includeDSiLFunction")) {
			availableActivationFunctions.add(FTYPE_DSIL);
		}
		
	}

	/**
	 * Provides activation from node
	 * @param ftype type of node
	 * @param sum input sent node
	 * @return activation of node
	 */
	public static double activation(int ftype, double sum) {
		assert!Double.isInfinite(sum) : "Function input is infinite " + sum;
		assert!Double.isNaN(sum) : "Function input is NaN " + sum;
		double activation = functionMap.get(ftype).f(sum);
		assert!Double.isNaN(activation) : activationName(ftype) + " returns NaN on " + sum + " from " + activation;
		assert!Double.isInfinite(activation) : activationName(ftype) + " is infinite on " + sum + " from " + activation;
		return activation;
	}

	/**
	 * String name of the activation function
	 * @param ftype Identifier for activation function
	 * @return
	 */
	public static String activationName(int ftype) { 
		return functionMap.get(ftype).name();
	}
	
	/**
	 * Return an instance of Activation, which is an activation function used
	 * by DL4J/ND4J. Activation functions that do not have DL4J equivalents will
	 * through exceptions.
	 * 
	 * @param ftype Unique identifier for activation function in MM-NEAT
	 * @return Enum instance from DL4J for corresponding activation function.
	 */
	public static Activation getDL4JEquivalent(int ftype) {
		return functionMap.get(ftype).equivalentDL4JFunction();
	}
	
	/**
	 * Takes in the list of all ftypes and randomly selects a function. (For CPPN)
	 *
	 * @return random listed integer for ftype
	 */
	public static int randomFunction() {
		return RandomNumbers.randomElement(availableActivationFunctions);
	}

	/**
	 * Determines whether or not to use TWEANN (the fixed parameter ftype) or
	 * CPPN (random function out of function list)
	 *
	 * @return function for either TWEANN or CPPN
	 */
	public static int newNodeFunction() {
		if (Parameters.parameters.booleanParameter("allowMultipleFunctions")) { 
			// for CPPN
			return randomFunction();
		} else {
			// for TWEANN
			return CommonConstants.ftype; 
		}
	}

	/**
	 * Standard sigmoid function used in various places.
	 * Uses safeExp to save time, since the sigmoid function saturates
	 * in a way that makes calculating the tails with exp expensive.
	 * @param x
	 * @return
	 */
	public static double sigmoid(double x) {
		return (1.0 / (1.0 + Math.exp(-x)));
	}	

	/**
	 * Quick approximation to exp. Inaccurate, but has needed properties. Could
	 * slightly speed up execution given how often exp is used in a sigmoid.
	 *
	 * @param val Function parameter
	 * @return approximate result of exp(val)
	 */
	public static double quickExp(double val) {
		final long tmp = (long) (1512775 * val + 1072632447);
		return Double.longBitsToDouble(tmp << 32);
	}

	/**
	 * Gaussian function for x, sigma, and mu. Does not utilize safe exp at the
	 * moment, can be changed.
	 *
	 * @param x function input
	 * @param sig standard deviation
	 * @param mu mean/center
	 * @return value of gaussian(x)
	 */
	public static double gaussian(double x, double sig, double mu) {
		double second = Math.exp(-0.5 * ((x - mu) / sig) * ((x - mu) / sig));
		double first = (1 / (sig * Math.sqrt(2 * Math.PI)));
		return first * second;
	}
	
	/**
	 * Similar to sawtooth function, but with a range of -1 to 1. 
	 * 
	 * @param x function parameter
	 * @param a period
	 * @return value of fullSawtooth(x, a)
	 */
	public static double fullSawtooth(double x, double a) {
		return 2 * ((x/a) - Math.floor(1/2 + x/a));
	}
	
	/**
	 * Sawtooth function for x. mimics sine but in piecewise way. Uses
	 * Math.floor().
	 *
	 * @param x Function parameter
	 * @return value of sawtooth(x)
	 */
	public static double halfSawtooth(double x) {
		return x - Math.floor(x);
	}

	/**
	 * Square wave is a sinusoidal periodic waveform. Alternating between 
	 * minimum and maximum amplitudes at a steady rate - useful for sound generation
	 * in Java
	 * 
	 * @param x function parameter
	 * @param p period 
	 * @param a amplitude
	 * @return value of squareWave(x, p, a)
	 */
	public static double squareWave(double x, double p, double a) {
		double sineCalculation = Math.sin(2 * Math.PI/p * x);
		if (sineCalculation == 0) //checks for frequency switch where a discontinuity would occur
			return 0; 
		else
			return a * 1/sineCalculation * Math.abs(sineCalculation);
	}
	
	public static void main(String[] args) {
		for(double i = 0; i < 100; i++) {
			double e = sigmoid(i);
			double q = 1 / (1 + Math.exp(-i));
			System.out.printf("%f\t%f\t%f\n", e,q,(e-q));
		}
	}
}
