package edu.utexas.cs.nn.networks;

/**
 *
 * @author Jacob Schrum
 * Edits by Gabby Gonzalez
 */
public class ActivationFunctions {

	// For use in sigmoid, it is convenient to bound the inputs to the exp function
	public static final double SAFE_EXP_BOUND = 7;
	
	/**
	 * Will behave the same as Math.exp within specified bound
	 * @param x Function parameter
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
     * Quick approximation to exp. Inaccurate, but has needed properties.
     * Could slightly speed up execution given how often exp is used in a sigmoid.
     * @param val Function parameter
     * @return approximate result of exp(val)
     */
    public static double quickExp(double val) {
        final long tmp = (long) (1512775 * val + 1072632447);
        return Double.longBitsToDouble(tmp << 32);
    }

    /**
     * Quick approximation to sigmoid. Inaccurate, but has needed properties.
     * Could slightly speed up execution given how often sigmoid is used. 
     * @param x Function parameter
     * @return approximate value of sigmoid(x)
     */
    public static double quickSigmoid(double x) { 
        return 1.0 / (1.0 + quickExp(-x)); // Purpose of a negative x? I think it's to allow the sigmoid to start lower rather than higher? Or is that backwards - Gabby
    }

    /**
     * Safe function for sigmoid. Will behave the same as Math.exp within specified bound.
     * @param x Function parameter
     * @return value of sigmoid(x)
     */
    public static double sigmoid(double x) { 
        return 1.0 / (1.0 + safeExp(-x));
    }

    /**
     * Derivative function for sigmoid. Used to evaluate weights to change.
     * @param x Function parameter
     * @return value of derivative(x)
     */
    public static double sigmoidprime(double x) {
        return sigmoid(x) * (1.0 - sigmoid(x));
    }

    /**
     * Function for tanh, hyperbolic tangent. Uses Math.tanh, no approximation.
     * @param x Function parameter
     * @return value of tanh(x)
     */
    public static double tanh(double x) {
        return Math.tanh(x);
    }

    /**
     * Quick approximation to sigmoid within the bounds of -1 < x < 1. Inaccurate, but has needed properties.
     * Could slightly speed up execution given how often sigmoid is used.
     * @param x Function parameter
     * @return value of sigmoid(x) within -1 and 1
     */
    public static double fullQuickSigmoid(double x) {
        return (2 * quickSigmoid(x)) - 1;
    }
    
    /**
     * Derivative function for tanh, hyperbolic tangent. Used to evaluate weights to change.
     * @param x Function parameter
     * @return value of derivative(x)
     */
    public static double tanhprime(double x) {
        return 1.0 - tanh(x) * tanh(x);
    }
    
    /**
     * Linear function that returns x within the bounds of -1 < x < 1
     * @param x Function parameter
     * @return linear x within -1 and 1
     */
    public static double fullLinear(double x) {
        return Math.max(-1, Math.min(1, x));
    }
    
    /**
     * Gaussian function for x, sigma, and mu. Does not utilize safe exp at the moment, can be changed.
     * @param x, sigma, mu Function parameters
     * @return value of gaussian(x)
     */
    public static double gaussian(double x, double sig, double mu){
    	double second = Math.exp(-0.5 * ((x - mu)/sig) * ((x - mu)/sig));
    	double first = (1 / (sig * Math.sqrt(2 * Math.PI)));
		return first * second;
    }

    /**
     * Overloaded gaussian function for x. Sigma is set to 1 and mu is set to 0. Does not utilize safe exp at the moment, can be changed.
     * @param x Function parameter
     * @return value of gaussian(x)
     */
    public static double gaussianOverload(double x){
    	double second = Math.exp(-0.5 * ((x - 0)/1) * ((x - 0)/1));
    	double first = (1 / (1 * Math.sqrt(2 * Math.PI)));
		return first * second;
    }
    
    /**
     * Sine function for x. Uses Math.sin();
     * @param x Function parameter
     * @return value of sin(x)
     */
    public static double sine(double x){
    	return Math.sin(x);
    }
    
    /**
     * Absolute value function for x. Uses Math.abs();
     * @param x Function parameter
     * @return value of abs(x)
     */
    public static double absVal(double x){
    	return Math.abs(x);
    }    
}
