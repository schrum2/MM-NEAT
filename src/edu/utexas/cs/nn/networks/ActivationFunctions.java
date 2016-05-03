package edu.utexas.cs.nn.networks;

/**
 *
 * @author Jacob Schrum
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
        return 1.0 / (1.0 + quickExp(-x));
    }

    public static double sigmoid(double x) {
        return 1.0 / (1.0 + safeExp(-x));
    }

    public static double sigmoidprime(double x) {
        return sigmoid(x) * (1.0 - sigmoid(x));
    }

    public static double tanh(double x) {
        return Math.tanh(x);
    }

    public static double fullQuickSigmoid(double x) {
        return (2 * quickSigmoid(x)) - 1;
    }

    public static double tanhprime(double x) {
        return 1.0 - tanh(x) * tanh(x);
    }
    
    public static double fullLinear(double x) {
        return Math.max(-1, Math.min(1, x));
    }
}
