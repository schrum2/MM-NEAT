package edu.southwestern.networks;

import edu.southwestern.evolution.genotypes.MLPGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;
import java.util.Arrays;

/**
 * Standard Multi-Layer Perceptron 
 * MLP is a feedforward ANN that is fully connected
 * Uses backpropagation
 * Has one hidden layer between inputs and outputs.
 * 
 * @author Jacob Schrum
 */
public class MLP implements Network {

	@Override
	/**
	 * returns number of inputs
	 */
	public int numInputs() {
		return firstConnectionLayer.length;
	}

	@Override
	/**
	 * returns number of outputs
	 */
	public int numOutputs() {
		return secondConnectionLayer[0].length;
	}

	@Override
	/**
	 * processes inputs via propagation
	 */
	public double[] process(double[] inputs) {
		return propagate(inputs);
	}

	@Override
	/**
	 * Clears MLP so SRN can be reused
	 */
	public void flush() {
		// Matters for SRN
		clear(inputs);
		clear(hiddenNeurons);
		clear(outputs);
	}

	@Override
	/**
	 * Unwritten method, will become useful
	 * once mulitask learning is developed for MLPs
	 */
	public boolean isMultitask() {
		// Leave like this until I bother developing multitask MLPs
		return false;
	}

	@Override
	/**
	 * Chooses the node corresponding to the input
	 */
	public void chooseMode(int mode) {
		// Does nothing until I bother developing multitask MLPs
		throw new UnsupportedOperationException("Multimodal MLPs not supported yet");
	}

	/*
	 * Everything below here comes from Togelius' implementation and is only
	 * slightly modified by me
	 */
	//double array containing connections between layers
	public double[][] firstConnectionLayer;//connection from first layer to hidden
	public double[][] secondConnectionLayer;//connection from hidden layer to output
	//Layers of nodes 
	protected double[] hiddenNeurons;
	protected double[] inputs;
	protected double[] outputs;
	public double learningRate = Parameters.parameters.doubleParameter("backpropLearningRate");
	//this parameter is the "Rate backprop learning for MLPs"

	/**
	 * Constructor for an MLP from an
	 * MLPGenotype
	 * @param genotype MLPGenotype
	 */
	public MLP(MLPGenotype genotype) {
		this(genotype.firstConnectionLayer, genotype.secondConnectionLayer);
	}

	/**
	 * Constructor for an MLP
	 * @param numberOfInputs num input nodes
	 * @param numberOfHidden num hidden nodes
	 * @param numberOfOutputs num output nodes
	 */
	public MLP(int numberOfInputs, int numberOfHidden, int numberOfOutputs) {
		this.inputs = new double[numberOfInputs];
		this.firstConnectionLayer = new double[numberOfInputs][numberOfHidden];
		this.secondConnectionLayer = new double[numberOfHidden][numberOfOutputs];
		this.hiddenNeurons = new double[numberOfHidden];
		this.outputs = new double[numberOfOutputs];
		//initially all MLPs insantiated randomly. May change?//TODO
		initializeAllLayersRandom();
	}

	/**
	 * Constructor for an MLP that specifies all connections
	 * @param firstConnectionLayer connections between input and hidden layer
	 * @param secondConnectionLayer connections between hidden and output layer
	 */
	public MLP(double[][] firstConnectionLayer, double[][] secondConnectionLayer) {
		this(firstConnectionLayer, secondConnectionLayer, secondConnectionLayer.length,
				secondConnectionLayer[0].length);
	}

	/**
	 * Constructor for an MLP that specifies all connections and num hidden, output nodes
	 * @param firstConnectionLayer connections between input and hidden layer
	 * @param secondConnectionLayer connections between hidden and output layer
	 * @param numberOfHidden num hidden nodes
	 * @param numberOfOutputs num output nodes
	 */
	public MLP(double[][] firstConnectionLayer, double[][] secondConnectionLayer, int numberOfHidden,
			int numberOfOutputs) {
		this.inputs = new double[firstConnectionLayer.length];
		this.firstConnectionLayer = firstConnectionLayer;
		this.secondConnectionLayer = secondConnectionLayer;
		this.hiddenNeurons = new double[numberOfHidden];
		this.outputs = new double[numberOfOutputs];
	}

	/**
	 * MLP version of process. Propagates inputs through network
	 * @param inputIn inputs to network
	 * @return outputs 
	 */
	public double[] propagate(double[] inputIn) {
		if (inputs == null) {//if inputs never instantiated(useful if # inputs not readily known)
			inputs = new double[inputIn.length];
		}
		if (inputs != inputIn) {//Alerts user that num inputs doesn't match input layer to MLP
			if (inputIn.length > inputs.length) {//TODO is necessary considering assert below?
				System.out.println("MLP given " + inputIn.length + " inputs, but only intialized for " + inputs.length);
			}
			System.arraycopy(inputIn, 0, this.inputs, 0, inputIn.length);
		}
		assert (inputIn.length == inputs.length) : //throws an error if inputs and input layer don't match
			("NOTE: only " + inputIn.length + " inputs out of " + inputs.length + " are used in the network") + "\n" +
			("inputIn:" + inputIn.length + ",inputs" + inputs.length) + "\n" +
			("inputIn:" + Arrays.toString(inputIn)) + "\n" +
			("inputs:" + Arrays.toString(inputs));
		//clears network so no remnants from previous prop interfere
		clear(hiddenNeurons);
		clear(outputs);
		//manually propagates inputs through network, possible since
		//network defined as having exactly one input, hidden and output layer
		NetworkUtil.propagateOneStep(inputs, hiddenNeurons, firstConnectionLayer);
		//transforms weights
		tanh(hiddenNeurons);
		//manually propagates forward
		NetworkUtil.propagateOneStep(hiddenNeurons, outputs, secondConnectionLayer);
		//transforms weights
		tanh(outputs);
		return outputs;
	}

	/**
	 * Initializes random connections between layers
	 * (??will not be fully connected??)//TODO
	 */
	public final void initializeAllLayersRandom() {
		initializeRandom(this.firstConnectionLayer);
		initializeRandom(this.secondConnectionLayer);
	}

	/**
	 * Initializes a single layer of MLP randomly
	 * (??will not be fully connected??)
	 * @param layer layer to be randomized
	 */
	private void initializeRandom(double[][] layer) {
		for (int i = 0; i < layer.length; i++) {
			for (int j = 0; j < layer[i].length; j++) {
				layer[i][j] = RandomNumbers.fullSmallRand();
			}
		}
	}

	/**
	 * hard copies MLP
	 * @return copy of given MLP
	 */
	public MLP copy() {
		return new MLP(copy(firstConnectionLayer), copy(secondConnectionLayer), hiddenNeurons.length, outputs.length);
	}

	/**
	 * Hard-copies layers
	 * @param original original layer
	 * @return duplicate of original 
	 */
	protected double[][] copy(double[][] original) {
		double[][] copy = new double[original.length][original[0].length];
		for (int i = 0; i < original.length; i++) {
			System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
		}
		return copy;
	}



	/**
	 * Clears given array
	 * @param array array to clear
	 */
	protected void clear(double[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = 0;
		}
	}

	/**
	 * Returns tanh-transformed values of whole array
	 * @param array array to be transformed
	 */
	protected void tanh(double[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = Math.tanh(array[i]);
		}
	}

	/**
	 * sums all weights of connections
	 * @return sum of all connection weights
	 */
	public double sum() {
		double sum = 0;
		for (int i = 0; i < firstConnectionLayer.length; i++) {
			for (int j = 0; j < firstConnectionLayer[i].length; j++) {
				sum += firstConnectionLayer[i][j];
			}
		}
		for (int i = 0; i < secondConnectionLayer.length; i++) {
			for (int j = 0; j < secondConnectionLayer[i].length; j++) {
				sum += secondConnectionLayer[i][j];
			}
		}
		return sum;
	}

	/**
	 * Detailed to string method for an MLP 
	 * Prints connections between layers
	 */
	public void println() {

		System.out.print("\n\n----------------------------------------------------" + "-----------------------------------\n");
		for (int i = 0; i < firstConnectionLayer.length; i++) {
			System.out.print("|");

			for (int j = 0; j < firstConnectionLayer[i].length; j++) {
				System.out.print(" " + firstConnectionLayer[i][j]);
			}
			System.out.print(" |\n");
		}

		System.out.print("----------------------------------------------------" + "-----------------------------------\n");

		for (int i = 0; i < secondConnectionLayer.length; i++) {

			System.out.print("|");

			for (int j = 0; j < secondConnectionLayer[i].length; j++) {
				System.out.print(" " + secondConnectionLayer[i][j]);
			}

			System.out.print(" |\n");

		}

		System.out.print("----------------------------------------------------" + "-----------------------------------\n");
	}

	/**
	 * ?D? hyperbolic tangent of number
	 * Transforms given double
	 * @param num number to transform
	 * @return transformed number
	 */
	private double dtanh(double num) {
		// return 1;
		return (1 - (num * num));
	}

	@SuppressWarnings("unused")
	private double dSigmoid(double num) {
		final double val = sig(num);
		return (val*(1 - val));
	}

	private double sig(double num) {
		return 1 / (1 + Math.exp(-num));
	}

	/**
	 * Form of network training where desired outputs are 
	 * given and then passed backwards through network
	 * in order to hone in weights to return a more desireable
	 * output when propagated
	 * Backpropagates target outputs through network
	 * @param targetOutputs
	 * @return
	 */
	public double backPropagate(double[] targetOutputs) {
		// Calculate output layer error
		double[] outputError = calculateWeightError(outputs, hiddenNeurons, true);
		// Calculate hidden layer error
		double[] hiddenError = calculateWeightError(inputs, outputError, false);
		// Update first weight layer
		updateLayerWeight(inputs, hiddenNeurons, firstConnectionLayer, hiddenError);
		// Update second weight layer
		updateLayerWeight(hiddenNeurons, outputs, secondConnectionLayer, hiddenError);
		//calculate sum of error
		return summedOutputError(outputs, targetOutputs);
	}

	/**
	 * calculates summed output error from backpropogation
	 * @param actualOutputs actual outputs
	 * @param targetOutputs target outputs
	 * @return sum of error between outputs
	 */
	private double summedOutputError(double[] actualOutputs, double[] targetOutputs) { 
		double summedOutputError = 0.0;
		for (int k = 0; k < actualOutputs.length; k++) {
			summedOutputError += Math.abs(targetOutputs[k] - actualOutputs[k]);
		}
		summedOutputError /= actualOutputs.length;
		return summedOutputError;
	}

	/**
	 * Calculates error between weights
	 * @param actualOutputs actual weights
	 * @param targetOutputs target weights
	 * @param calculateOutput whether or not to calculate error for output layer
	 * @return error
	 */
	private double[] calculateWeightError(double[] actualOutputs, double[] targetOutputs, boolean calculateOutput) {
		double[] error = new double[actualOutputs.length];
		for (int i = 0; i < actualOutputs.length; i++) {
			if(calculateOutput) {//calculates for output layer
				calculateOutputWeightError(error, actualOutputs, targetOutputs, i);
			} else {//
				calculateHiddenWeightError(i, error, actualOutputs, targetOutputs);
			}
		}
		return error;
	}

	/**
	 * 
	 * @param error
	 * @param actualOutputs
	 * @param targetOutputs
	 * @param i
	 */
	private void calculateOutputWeightError(double[] error, double[] actualOutputs, double[] targetOutputs, int i) { 
		error[i] = dtanh(actualOutputs[i]) * (targetOutputs[i] - actualOutputs[i]);

		/*Printlns for troubleshooting
		// System.out.println("Err: " + (targetOutputs[i] - outputs[i]) +
		// "=" + targetOutputs[i] + "-" + outputs[i]);
		// System.out.println("dnet: " + outputError[i] + "=" +
		// (dtanh(outputs[i])) + "*" + (targetOutputs[i] - outputs[i]));
		 */

		if (Double.isNaN(error[i])) {//only should occur if problem ocurred in backpropagation
			System.out.println("Problem at output " + i);
			System.out.println(actualOutputs[i] + " " + targetOutputs[i]);
			System.exit(0);//TODO is 1 or 0 appropriate for this?
		}
	}

	private void calculateHiddenWeightError(int i, double[] error, double[] actualOutputs, double[] targetOutputs) { 
		double contributionToOutputError = 0;
		for (int toOutput = 0; toOutput < actualOutputs.length; toOutput++) {
			contributionToOutputError += secondConnectionLayer[i][toOutput] * targetOutputs[toOutput];

			/*Printlns for troubleshooting
			// System.out.println("Hidden " + hidden + ", toOutput" +
			// toOutput);
			// System.out.println("Err tempSum: " +
			// contributionToOutputError + "="
			// +secondConnectionLayer[hidden][toOutput] + "*"
			// +outputError[toOutput] );
			 */
		}
		error[i] = dtanh(actualOutputs[i]) * contributionToOutputError;
		// System.out.println("dnet: " + hiddenError[hidden] + "=" +
		// dtanh(hiddenNeurons[hidden])+ "*" + contributionToOutputError);
	}

	private void updateLayerWeight(double[] source, double[] target, double[][] connectionLayer, double[] hiddenError) { 
		for (int input = 0; input < source.length; input++) {
			for (int hidden = 0; hidden < target.length; hidden++) {
				double saveAway = firstConnectionLayer[input][hidden];//adds to weight based on error 
				connectionLayer[input][hidden] += learningRate * hiddenError[hidden] * source[input];
				if (Double.isNaN(connectionLayer[input][hidden])) {//only should occur if problem ocurred in backpropagation
					System.out.println("Late weight error! target " + hiddenError[hidden] + " source "
							+ source[input] + " was " + saveAway);
				}
			}
		}
	}
	
	/**
	 * returns info about MLP
	 * @return info
	 */
	public String info() {
		int numberOfConnections = (firstConnectionLayer.length * firstConnectionLayer[0].length)
				+ (secondConnectionLayer.length * secondConnectionLayer[0].length);
		return "Straight mlp, mean connection weight " + (sum() / numberOfConnections);
	}

	@Override
	/**
	 * default toString method
	 */
	public String toString() {
		return "MLP:" + firstConnectionLayer.length + "/" + secondConnectionLayer.length + "/" + outputs.length;
	}

	@Override
	/**
	 * Not supported yet
	 */
	public int effectiveNumOutputs() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	/**
	 * not supported yet
	 */
	public double[] moduleOutput(int mode) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	/**
	 * not supported yet
	 */
	public int lastModule() {
		// Does nothing until I develope multitask for MLPs
		return -1;
	}

	@Override
	/**
	 *not supported yet
	 */
	public int numModules() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	/**
	 * not supported yet
	 */
	public int[] getModuleUsage() {
		return null;
	}
}
