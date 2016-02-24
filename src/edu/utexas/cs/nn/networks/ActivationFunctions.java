package edu.utexas.cs.nn.networks;

/**
 *
 * @author Jacob Schrum
 */
public class ActivationFunctions {

    public static double safeExp(double x) {
        if (Math.abs(x) < 7.0) {
            return Math.exp(x);
        }
        if (x > 0.0) {
            return Math.exp(7.0);
        } else {
            return Math.exp(-7.0);
        }
    }

    /*
     * Quick approximation to exp. Inaccurrate, but has needed properties
     */
    public static double quickExp(double val) {
        final long tmp = (long) (1512775 * val + 1072632447);
        return Double.longBitsToDouble(tmp << 32);
    }

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
