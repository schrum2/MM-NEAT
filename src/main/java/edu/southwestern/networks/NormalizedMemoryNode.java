package edu.southwestern.networks;

import java.util.ArrayDeque;
import edu.southwestern.networks.TWEANN.Link;
import edu.southwestern.parameters.Parameters;

public class NormalizedMemoryNode extends TWEANN.Node{
	
	//the last nodeNormMemoryLength values that this node has seen
	public ArrayDeque<Double> normalizedMemory;
	public double normalizedMemorySum;
	public double normalizedMemoryMean;
	public double normalizedMemorySumVarianceTerms;
	public double normalizedMemoryVariance;
	public static int normalizedMemoryMaxSize;
	double gamma;
	double beta;
	double epsilon;

	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation) {
		this(tweann, ftype, ntype, innovation, 0.0);
	}
	
	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation, double bias) {
		this(tweann, ftype, ntype, innovation, false, bias);
	}

	public NormalizedMemoryNode(TWEANN tweann, int ftype, int ntype, long innovation, boolean frozen, double bias) {
		tweann.super(ftype, ntype, innovation, frozen, bias);
		this.normalizedMemorySum = 0;
		this.normalizedMemoryMean = 0;
		this.normalizedMemorySumVarianceTerms = 0;
		this.normalizedMemoryVariance = 0;
		//expensive to compute for every node. Should this be a common constant?
		normalizedMemoryMaxSize = Parameters.parameters.integerParameter("nodeNormMemoryLength");
		this.normalizedMemory  = new ArrayDeque<Double>(normalizedMemoryMaxSize);
	}
	
	@Override
	protected void activateAndTransmit() {
		activation = ActivationFunctions.activation(ftype, sum);
		if(normalizedMemory.size() == normalizedMemoryMaxSize) {
			double removedValue = normalizedMemory.poll();
			normalizedMemorySum -= removedValue;
			normalizedMemorySumVarianceTerms -= (removedValue - normalizedMemoryMean) * (removedValue - normalizedMemoryMean);
		}
		normalizedMemory.add(activation);
		//calculate mean
		normalizedMemorySum += activation;
		normalizedMemoryMean = normalizedMemorySum / normalizedMemory.size();
		
		//calculate variance
		normalizedMemorySumVarianceTerms += (activation - normalizedMemoryMean) * (activation - normalizedMemoryMean);
		normalizedMemoryVariance = normalizedMemorySumVarianceTerms / normalizedMemory.size();
		
		//normalize activation
		activation = (activation - normalizedMemoryMean) / Math.sqrt(normalizedMemoryVariance + epsilon);
		
		//scale and shift
		activation = gamma * activation + beta;
		
        // reset sum to original bias after activation 
		sum = bias;
		for (Link l : outputs) {
			l.transmit(activation);
		}
	}
}
