package edu.southwestern.networks;

import java.util.ArrayList;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Contains activation functions used by neural networks.
 * 
 * @author Jacob Schrum Edits by Gabby Gonzalez and Lauren Gillespie
 */
public class ActivationFunctions {

	public static final int MAX_POSSIBLE_ACTIVATION_FUNCTIONS = 27;

	/**
	 * Initialize the array list for all ftypes
	 */
	public static ArrayList<Integer> availableActivationFunctions = new ArrayList<>(MAX_POSSIBLE_ACTIVATION_FUNCTIONS);

	// For use in sigmoid, it is convenient to bound the inputs to the exp function
	public static final double SAFE_EXP_BOUND = 7;

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
	public static final int FTYPE_HLPIECEWISE = 15;
	public static final int FTYPE_SAWTOOTH = 16;
	public static final int FTYPE_STRETCHED_TANH = 17;
	public static final int FTYPE_RE_LU  = 18;
	public static final int FTYPE_SOFTPLUS  = 19;
	public static final int FTYPE_LEAKY_RE_LU = 20;
	public static final int FTYPE_FULLSAWTOOTH = 21;
	public static final int FTYPE_TRIANGLEWAVE = 22;
	public static final int FTYPE_SQUAREWAVE = 23;
	public static final int FTYPE_FULLSIGMOID = 24;
	public static final int FTYPE_FULLGAUSS = 25;
	public static final int FTYPE_COS = 26;

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
	}

	/**
	 * Provides activation from node
	 * @param ftype type of node
	 * @param sum input sent node
	 * @return activation of node
	 */
	public static double activation(int ftype, double sum) {
		double activation = 0.0;
		switch (ftype) {
		case ActivationFunctions.FTYPE_SAWTOOTH:
			activation = ActivationFunctions.sawtooth(sum);
			assert!Double.isNaN(activation) : "sawtooth returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "sawtooth is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_HLPIECEWISE:
			activation = ActivationFunctions.halfLinear(sum);
			assert!Double.isNaN(activation) : "halfLinear returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "halfLinear is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_SIGMOID:
			activation = ActivationFunctions.sigmoid(sum);
			assert!Double.isNaN(activation) : "sigmoid returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "sigmoid is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_TANH:
			activation = ActivationFunctions.tanh(sum);
			assert!Double.isNaN(activation) : "tanh returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "tanh is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_ID:
			activation = sum;
			assert!Double.isNaN(activation) : "ID returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "ID is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_APPROX:
			activation = ActivationFunctions.quickSigmoid(sum);
			assert!Double.isNaN(activation) : "quickSigmoid returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "quickSigmoid is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_FULLAPPROX:
			activation = ActivationFunctions.fullQuickSigmoid(sum);
			assert!Double.isNaN(activation) : "fullQuickSigmoid returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "fullQuickSigmoid is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_GAUSS:
			activation = ActivationFunctions.gaussian(sum);
			assert!Double.isNaN(activation) : "gaussian returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "gaussian is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_SINE:
			activation = ActivationFunctions.sine(sum);
			assert!Double.isNaN(activation) : "sine returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "sine is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_ABSVAL:
			activation = ActivationFunctions.absVal(sum);
			assert!Double.isNaN(activation) : "absVal returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "absVal is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_STRETCHED_TANH:
			activation = ActivationFunctions.stretchedTanh(sum);
			assert!Double.isNaN(activation) : "stretchedTanh returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "stretchedTanh is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_RE_LU:
			activation = ActivationFunctions.ReLU(sum);
			assert!Double.isNaN(activation) : "rectified linear units returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "rectified linear units is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_SOFTPLUS:
			activation = ActivationFunctions.Softplus(sum);
			assert!Double.isNaN(activation) : "softplus returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "softplus is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_LEAKY_RE_LU:
			activation = ActivationFunctions.LeakyReLU(sum);
			assert!Double.isNaN(activation) : "leaky ReLU returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "leaky ReLU is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_FULLSAWTOOTH:
			activation = ActivationFunctions.fullSawtooth(sum);
			assert!Double.isNaN(activation) : "fullSawtooth returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "fullSawtooth is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_TRIANGLEWAVE:
			activation = ActivationFunctions.triangleWave(sum);
			assert!Double.isNaN(activation) : "triangleWave returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "triangleWave is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_SQUAREWAVE:
			activation = ActivationFunctions.squareWave(sum);
			assert!Double.isNaN(activation) : "squareWave returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "squareWave is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_FULLSIGMOID:
			activation = ActivationFunctions.fullSigmoid(sum);
			assert!Double.isNaN(activation) : "fullSigmoid returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "fullSigmoid is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_FULLGAUSS:
			activation = ActivationFunctions.fullGaussian(sum);
			assert!Double.isNaN(activation) : "fullGaussian returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "fullGaussian is infinite on " + sum + " from " + activation;
			break;
		case ActivationFunctions.FTYPE_COS:
			activation = ActivationFunctions.cosine(sum);
			assert!Double.isNaN(activation) : "cosine returns NaN on " + sum;
			assert!Double.isInfinite(activation) : "cosine is infinite on " + sum + " from " + activation;
			break;
		}
		return activation;
	}

	/**
	 * String name of the activation function
	 * @param ftype Identifier for activation function
	 * @return
	 */
	public static String activationName(int ftype) { 
		assert ftype > -1 && ftype <= MAX_POSSIBLE_ACTIVATION_FUNCTIONS:"given activation function not valid! " + ftype;
		if(ftype == FTYPE_SIGMOID) {
			return "Sigmoid";
		}else if(ftype == FTYPE_TANH) {
			return "Tanh";
		} else if(ftype == FTYPE_SAWTOOTH) {
			return "Sawtooth";
		} else if(ftype == FTYPE_HLPIECEWISE) {
			return "Half Piecewise";
		}else if(ftype == FTYPE_ID) {
			return "ID";
		} else if(ftype == FTYPE_APPROX) {
			return "Approximate Sigmoid";
		} else if(ftype == FTYPE_FULLAPPROX) {
			return "Full Approximate Sigmoid";
		} else if(ftype == FTYPE_GAUSS) {
			return "Gaussian";
		} else if(ftype == FTYPE_SINE) {
			return "Sine";
		} else if(ftype == FTYPE_ABSVAL){
			return "Absolute Value";
		} else if(ftype == FTYPE_STRETCHED_TANH) {
			return "Stretched Tanh";
		} else if(ftype == FTYPE_RE_LU) {
			return "Rectified Linear Units";
		}else if(ftype == FTYPE_SOFTPLUS) {
			return "Softplus";
		}else if(ftype == FTYPE_LEAKY_RE_LU) {
			return "Leaky Rectified Linear Units";
		}else if(ftype == FTYPE_FULLSAWTOOTH) {
			return "Full Sawtooth";
		}else if(ftype == FTYPE_TRIANGLEWAVE) {
			return "Triangle Wave";
		}else if(ftype == FTYPE_SQUAREWAVE) {
			return "Square Wave";
		}else if(ftype == FTYPE_FULLSIGMOID) {
			return "Full Sigmoid";
		}else if(ftype == FTYPE_FULLGAUSS) {
			return "Full Gaussian";
		}else if(ftype == FTYPE_COS) {
			return "Cosine";
		}else {
			System.out.println("given ftype is not a valid activation function! " + ftype);
			System.exit(1);
			return null;
		}
	}
	/**
	 * Takes in the list of all ftypes and randomly selects a function. (For
	 * CPPN)
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
	 * Will behave the same as Math.exp within specified bound
	 *
	 * @param x
	 *            Function parameter
	 * @return
	 */
	public static double safeExp(double x) {
		if (Math.abs(x) < SAFE_EXP_BOUND) { // Don't change values in bounds
			return Math.exp(x);
		} else if (x > 0.0) { // Maximum allowed
			return Math.exp(SAFE_EXP_BOUND);
		} else { // Minimum allowed
			return Math.exp(-SAFE_EXP_BOUND);
		}
	}

	/**
	 * Quick approximation to exp. Inaccurate, but has needed properties. Could
	 * slightly speed up execution given how often exp is used in a sigmoid.
	 *
	 * @param val
	 *            Function parameter
	 * @return approximate result of exp(val)
	 */
	public static double quickExp(double val) {
		final long tmp = (long) (1512775 * val + 1072632447);
		return Double.longBitsToDouble(tmp << 32);
	}

	/**
	 * Quick approximation to sigmoid. Inaccurate, but has needed properties.
	 * Could slightly speed up execution given how often sigmoid is used.
	 *
	 * @param x
	 *            Function parameter
	 * @return approximate value of sigmoid(x)
	 */
	public static double quickSigmoid(double x) {
		return 1.0 / (1.0 + quickExp(-x)); 
	}

	/**
	 * Safe function for sigmoid. Will behave the same as Math.exp within
	 * specified bound.
	 *
	 * @param x
	 *            Function parameter
	 * @return value of sigmoid(x)
	 */
	public static double sigmoid(double x) {
		return (1.0 / (1.0 + safeExp(-x)))*2 - 1;
	}
	
	/**
	 * Derivative function for sigmoid. 
	 * Used by backpropagation.
	 *
	 * @param x
	 *            Function parameter
	 * @return value of derivative(x)
	 */
	public static double sigmoidprime(double x) {
		return sigmoid(x) * (1.0 - sigmoid(x));
	}

	/**
	 * Standard sigmoid, but stretched to the range
	 * -1 to 1. This is an alternative to tanh, and is
	 * also used in the original Picbreeder.
	 * @param x parameter
	 * @return result
	 */
	public static double fullSigmoid(double x) {
		return (1.0 / (1+safeExp(-x))) * 2.0 - 1.0;
	}
	
	/**
	 * Function for tanh, hyperbolic tangent. Uses Math.tanh, no approximation.
	 *
	 * @param x
	 *            Function parameter
	 * @return value of tanh(x)
	 */
	public static double tanh(double x) {
		return Math.tanh(x);
	}

	/**
	 * Quick approximation to sigmoid within the bounds of -1 < x < 1.
	 * Inaccurate, but has needed properties. Could slightly speed up execution
	 * given how often sigmoid is used. 
	 * @param x Function parameter 
	 * @return sigmoid approximation within -1 and 1
	 */
	public static double fullQuickSigmoid(double x) {
		return (2 * quickSigmoid(x)) - 1;
	}

	/**
	 * Derivative function for tanh, hyperbolic tangent. 
         * Used for backpropogation.
	 *
	 * @param x
	 *            Function parameter
	 * @return value of derivative(x)
	 */
	public static double tanhprime(double x) {
		return 1.0 - tanh(x) * tanh(x);
	}

	/**
	 * Linear function that returns x within the bounds of -1 < x < 1
	 *
	 * @param x
	 *            Function parameter
	 * @return linear x within -1 and 1
	 */
	public static double fullLinear(double x) {
		return Math.max(-1, Math.min(1, x));
	}

	/**
	 * Linear function that returns x within the bounds of 0 < x < 1
	 *
	 * @param x
	 *            Function parameter
	 * @return linear x within 0 and 1
	 */
	public static double halfLinear(double x) {
		return Math.max(0, Math.min(1, x));
	}

	/**
	 * Gaussian function for x, sigma, and mu. Does not utilize safe exp at the
	 * moment, can be changed.
	 *
	 * @param x,
	 *            function input
         * @param sig
         *            standard deviation
         * @param mu
         *            mean/center
	 * @return value of gaussian(x)
	 */
	public static double gaussian(double x, double sig, double mu) {
		double second = Math.exp(-0.5 * ((x - mu) / sig) * ((x - mu) / sig));
		double first = (1 / (sig * Math.sqrt(2 * Math.PI)));
		return first * second;
	}

	/**
	 * Overloaded gaussian function for x. Sigma is set to 1 and mu is set to 0.
	 * Does not utilize safe exp at the moment, can be changed.
	 *
	 * @param x
	 *            Function parameter
	 * @return value of gaussian(x)
	 */
	public static double gaussian(double x) {
		return gaussian(x, 1, 0);
		// EXPERIMENTING
		//return Math.exp(-x*x); // Unsigned (stretched to range [0,1])
	}

	/**
	 * Gaussian stretched to the range [-1,1].
	 * This is the version used in the original Picbreeder.
	 * @param x parameter
	 * @return result
	 */
	public static double fullGaussian(double x) {
		return Math.exp(-x*x)*2 - 1;
	}
	
	/**
	 * Sine function for x. Uses Math.sin();
	 *
	 * @param x
	 *            Function parameter
	 * @return value of sin(x)
	 */
	public static double sine(double x) {
		return Math.sin(x);
	}
	
	/**
	 * Cosine: didn't have this originally, since sine
	 * seemed good enough, but apparently the original
	 * Picbreeder has cosine in it.
	 * @param x parameter
	 * @return result
	 */
	public static double cosine(double x) {
		return Math.cos(x);
	}

	/**
	 * Sawtooth function for x. mimics sine but in piecewise way. Uses
	 * Math.floor().
	 *
	 * @param x
	 *            Function parameter
	 * @return value of sawtooth(x)
	 */
	public static double sawtooth(double x) {
		return x - Math.floor(x);
	}

	/**
	 * Absolute value function for x. Uses Math.abs();
	 * 
	 * Also clamps result to range [0,1] after use of absolute value because of
	 * problems with values rising to infinity.
	 *
	 * @param x
	 *            Function parameter
	 * @return value of abs(x) clamped to [0,1]
	 */
	public static double absVal(double x) {
		return halfLinear(Math.abs(x));
	}
	/**
	 * returns the leaky rectified function, which allows for a small, non-zero gradient when the unit is not active
	 * @param sum input
	 * @return result
	 */
        public static double LeakyReLU(double sum) {
		return (sum > 0) ? sum : 0.01 * sum;
	}

        /**
         * The smooth approximation of the reLU function
         * @param sum
         * @return
         */
		public static double Softplus(double sum) {
		return Math.log(1 + Math.pow(Math.E, sum));
	}

		/**
		 * ramp function, analogous to half-wave rectification in electrical engineering
		 * @param sum input
		 * @return result
		 */
		public static double ReLU(double sum) {
		return Math.max(0, sum);
	}

		/**
         * Function proposed in the following paper as being better than standard 
         * tanh for neural networks.
         * Y. LeCun, L. Bottou, G. Orr and K. Muller: Efficient BackProp, in 
         * Orr, G. and Muller K. (Eds), Neural Networks: Tricks of the trade, Springer, 1998
         * 
         * The recommendation is for BackProp, but could be useful for us too.
         * @param sum input
         * @return function result
         */
	public static double stretchedTanh(double sum) {
		return 1.7159 * tanh( (2.0/3) * sum);  
	}
	
	/**
	 * Generalization of fullSawtooth with only one parameter.
	 * @param x Input parameter
	 * @return value of fullSawtooth(x, 1)
	 */
	public static double fullSawtooth(double x) {
		return fullSawtooth(x,1);
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
	 * Triangle wave can be represented as the absolute value of the sawtooth function.
	 * Piecewise linear, continuous real function - useful for sound generation in Java
	 * 
	 * @param x function parameter
	 * @param a period 
	 * @return value of triangleWave(x, a)
	 */
	public static double triangleWave(double x) {
		return Math.abs(fullSawtooth(x));
	}
	
	/**
	 * Generalization of square wave function with only one parameter.
	 * 
	 * @param x function parameter
	 * @return value of squareWave(x, 1, 1)
	 */
	public static double squareWave(double x) {
		return squareWave(x, 1, 1);
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
}
