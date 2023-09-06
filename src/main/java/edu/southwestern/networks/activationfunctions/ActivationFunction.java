package edu.southwestern.networks.activationfunctions;

public interface ActivationFunction {
	/**
	 * Function from real number to real number
	 * @param x Function input
	 * @return Result
	 */
	public double f(double x);
	
	/**
	 * Display name for this function
	 * @return
	 */
	public String name();
}