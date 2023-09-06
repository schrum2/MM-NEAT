package edu.southwestern.networks.activationfunctions;

public class StretchedTanHFunction implements ActivationFunction {
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
	@Override
	public double f(double sum) {
		return 1.7159 * Math.tanh( (2.0/3) * sum);  
	}
	
	@Override
	public String name() {
		return "tanh(stretched)"; //"Stretched Tanh";
	}
}
