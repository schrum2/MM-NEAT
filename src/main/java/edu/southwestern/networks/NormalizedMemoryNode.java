package edu.southwestern.networks;

import edu.southwestern.networks.TWEANN.Link;

public class NormalizedMemoryNode extends TWEANN.Node{
	private int numActivationsSeen;
	private double memoryMean;
	private double memorySumOfSquares;
	private double gamma;
	private double beta;
	private static final double EPSILON = 0.0001; // Small value to assure no division by 0 occurs

	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation) {
		this(tweann, ftype, ntype, innovation, 0.0);
	}

	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation, double bias) {
		this(tweann, ftype, ntype, innovation, false, bias);
	}

	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation, boolean frozen, double bias) {
		tweann.super(ftype, ntype, innovation, frozen, bias);
		this.numActivationsSeen = 0;
		this.memoryMean = 0;
		this.memorySumOfSquares = 0;
	}

	@Override
	protected void activateAndTransmit() {
		double immediateActivation = ActivationFunctions.activation(ftype, sum);
		numActivationsSeen++;
		//calculate mean
		double oldMean = memoryMean; // Save for Sum of Squares calculation below
		memoryMean = oldMean + ((immediateActivation - oldMean) / numActivationsSeen);

		//calculate variance
		double oldSumOfSquares = memorySumOfSquares;
		//memorySumOfSquares += (immediateActivation - oldMean) * (immediateActivation - memoryMean);
		memorySumOfSquares = oldSumOfSquares + (immediateActivation - oldMean) * (immediateActivation - memoryMean);
		double variance = memorySumOfSquares / numActivationsSeen;

		//normalize activation
		activation = (immediateActivation - memoryMean) / Math.sqrt(variance + EPSILON);
		System.out.println("here: " + (activation));

		//scale and shift
		activation = gamma * activation + beta;

		// Standard code from original activateAndTransmit method
		// reset sum to original bias after activation 
		sum = bias;
		for (Link l : outputs) {
			l.transmit(activation);
		}
	}
}
