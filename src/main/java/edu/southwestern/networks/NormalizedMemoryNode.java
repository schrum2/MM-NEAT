package edu.southwestern.networks;

import edu.southwestern.networks.TWEANN.Link;

public class NormalizedMemoryNode extends TWEANN.Node{
	
	//the last nodeNormMemoryLength values that this node has seen
	private int numActivationsSeenSoFar;
	private double memoryMean; //change name
	private double memoryVariance;
	private double gamma;
	private double beta;
	private double epsilon;

	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation) {
		this(tweann, ftype, ntype, innovation, 0.0);
	}
	
	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation, double bias) {
		this(tweann, ftype, ntype, innovation, false, bias);
	}

	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation, boolean frozen, double bias) {
		tweann.super(ftype, ntype, innovation, frozen, bias);
		this.numActivationsSeenSoFar = 0;
		this.memoryMean = 0;
		this.memoryVariance = 0;
		//expensive to compute for every node. Should this be a common constant?
	}
	
	@Override
	protected void activateAndTransmit() {
		activation = ActivationFunctions.activation(ftype, sum);
		numActivationsSeenSoFar++;
		double oldMean = memoryMean;
		memoryMean = oldMean + ((activation - memoryMean) / numActivationsSeenSoFar);
		double oldVariance = memoryVariance;
		memoryVariance = oldVariance + (activation - oldMean) * (activation - memoryMean);
		
		//normalize activation
		activation = (activation - memoryMean) / Math.sqrt(memoryVariance + epsilon);
		
		//scale and shift
		activation = gamma * activation + beta;
		
		//should this be here instead
		//normalizedMemory.add(activation);
		
        // reset sum to original bias after activation 
		sum = bias;
		for (Link l : outputs) {
			l.transmit(activation);
		}
	}
}
