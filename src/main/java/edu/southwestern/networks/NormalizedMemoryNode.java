package edu.southwestern.networks;

import edu.southwestern.networks.TWEANN.Link;

public class NormalizedMemoryNode extends TWEANN.Node{
	private int numActivationsSeen;
	private double memoryMean;
	private double memorySumOfSquares;
	private double gamma;
	private double beta;
	private static final double EPSILON = 0.0001; // Small value to assure no division by 0 occurs

	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation, boolean frozen, double bias, double gamma, double beta) {
		tweann.super(ftype, ntype, innovation, frozen, bias);
		this.numActivationsSeen = 0;
		this.memoryMean = 0;
		this.memorySumOfSquares = 0;
		this.gamma = gamma;
		this.beta = beta;
	}

	@Override
	protected void activateAndTransmit() {
		double immediateActivation = ActivationFunctions.activation(ftype, sum);
		numActivationsSeen++;
		//calculate mean
		double oldMean = memoryMean; // Save for Sum of Squares calculation below
		memoryMean += (immediateActivation - memoryMean) / numActivationsSeen;
		memoryMean = oldMean + ((immediateActivation - oldMean) / numActivationsSeen);
		System.out.println("mean: " + memoryMean + " of innovation: " + innovation);
		//calculate variance
		//double oldSumOfSquares = memorySumOfSquares;
		memorySumOfSquares += (immediateActivation - oldMean) * (immediateActivation - memoryMean);
		//memorySumOfSquares = oldSumOfSquares + (immediateActivation - oldMean) * (immediateActivation - memoryMean);
		double variance = memorySumOfSquares / numActivationsSeen;
		System.out.println("variance: " + variance + " of innovation: " + innovation);

		//normalize activation
		activation = (immediateActivation - memoryMean) / Math.sqrt(variance + EPSILON);
		
		//scale and shift
		activation = gamma * activation + beta;
		System.out.println("activation: " + activation + " of innovation: " + innovation);
		//System.out.println("activation: " + activation);
		// Standard code from original activateAndTransmit method
		// reset sum to original bias after activation 
		if(activation > 1) {
			activation = 1;
		}
		sum = bias;
		for (Link l : outputs) {
			l.transmit(activation);
		}
	}
}
