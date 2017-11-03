package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

import edu.southwestern.networks.ActivationFunctions;

public interface ActivationFunction {
	/**
	 * Function from real number to real number
	 * @param x Function input
	 * @return Result
	 */
	public double f(double x);
	
	/**
	 * If there is an equivalent DL4J activation function,
	 * return the enum entry identifying it.
	 * @return Instance of Activation enum from DL4J
	 */
	public Activation equivalentDL4JFunction();
	
	/**
	 * Display name for this function
	 * @return
	 */
	public String name();


	public static double sigmoid(double x) {
		return (1.0 / (1.0 + ActivationFunctions.safeExp(-x)));
	}
	
}