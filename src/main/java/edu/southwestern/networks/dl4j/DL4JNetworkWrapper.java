package edu.southwestern.networks.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import edu.southwestern.networks.Network;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * Most of my domains depend on a phenotype represented by a class
 * that implements my Network interface. The point of this class is
 * to take a network that works in DL4J (which I've been packaging
 * up in the TensorNetwork interface) and use the methods from my
 * Network class as a facade to access those method.
 * @author Jacob Schrum
 */
public class DL4JNetworkWrapper implements Network {

	public static final int INDEX_INPUT_BATCH = 0;
	public static final int INDEX_INPUT_CHANNELS = 1;
	public static final int INDEX_INPUT_HEIGHT = 2;
	public static final int INDEX_INPUT_WIDTH = 3;
	
	private TensorNetwork net;
	private int[] inputShape; // batch size, channels, width, height
	private int outputCount;

	/**
	 * Initialize a given network wrapped as a TensorNetwork (my class for
	 * wrapping DL4J networks) with the specified input shape and output count.
	 * The input shape and output counts must match the TensorNetwork. 
	 * 
	 * @param net A DL4J network.
	 * @param inputShape Batch size, Channel number, Width, and Height of inputs.
	 * @param outputCount Number of output neurons.
	 */
	public DL4JNetworkWrapper(TensorNetwork net, int[] inputShape, int outputCount) {
		this.net = net;
		this.inputShape = inputShape;
		this.outputCount = outputCount;
	}
	
	/**
	 * Call the Tensor network's fit method to train using backprop,
	 * after converting types from 2D arrays to INDArrays.
	 *  
	 * @param inputs
	 * @param outputTargets
	 */
	public void fit(double[][] inputs, double[][] outputTargets) {
		// DL4J requires INDArrays. Create input for hstack.
		INDArray[] hstackInput = new INDArray[inputs.length];
		for(int i = 0; i < hstackInput.length; i++) {
			hstackInput[i] = reshapeInput(inputs[i]);
		}
		INDArray inputBatchVectors = Nd4j.concat(0,hstackInput);
		//System.out.println("Inputs: " + inputBatchVectors.shapeInfoToString());
		// Similar procedure for outputs
		INDArray[] hstackOutput = new INDArray[outputTargets.length];
		for(int i = 0; i < hstackOutput.length; i++) {
			hstackOutput[i] = Nd4j.create(outputTargets[i]);
		}
		INDArray outputBatchVectors = Nd4j.concat(0,hstackOutput);
		//System.out.println("Outputs: " + outputBatchVectors.shapeInfoToString());
		System.out.println("outputBatchVectors:" + outputBatchVectors);
		
		DataSet minibatch = new DataSet(inputBatchVectors,outputBatchVectors);
		
		net.fit(minibatch);
	}
	
	/**
	 * Total number of inputs
	 */
	@Override
	public int numInputs() {
		return org.nd4j.common.util.ArrayUtil.prod(inputShape);
	}

	/**
	 * Total number of outputs
	 */
	@Override
	public int numOutputs() {
		return outputCount;
	}

	/**
	 * No multitask networks supported yet,
	 * so the effective number always matches the actual number.
	 */
	@Override
	public int effectiveNumOutputs() {
		return numOutputs();
	}

	/**
	 * Take a flat array of input values and put them into a shape that
	 * the DL4J network can handle within an INDArray. Pass that to the
	 * network to get an output result inside of an INDArray, and then
	 * unwrap that into a flat array of output values that is returned.
	 */
	@Override
	public double[] process(double[] inputs) {
		// Put linear inputs into tensor INDArray
		INDArray tensorInput = reshapeInput(inputs);
		// Process using DL4J network
		INDArray tensorOutput = net.output(tensorInput);
		// Flatten to 1D array
		INDArray flat = Nd4j.toFlattened('c', tensorOutput);
		// Convert to primitive Java array
		return ArrayUtil.doubleArrayFromINDArray(flat);
	}
	
	/**
	 * Take linear array of network inputs and reshape into
	 * an INDArray according to the inputShape.
	 * @param inputs Linear double array
	 * @return INDArray with appropriate shape for the network
	 */
	public INDArray reshapeInput(double[] inputs) {
		return Nd4j.create(inputs, inputShape, 'c');
	}

	/**
	 * Eliminate recurrent state
	 */
	@Override
	public void flush() {
		net.flush();
	}

	/**
	 * Not supported yet
	 */
	@Override
	public boolean isMultitask() {
		return false;
	}

	/**
	 * Not supported yet
	 */
	@Override
	public void chooseMode(int mode) {
		throw new UnsupportedOperationException("Cannot choose mode with wrapped DL4J networks yet");
	}

	/**
	 * Currently, there can only ever be one, so it will always be the first.
	 */
	@Override
	public int lastModule() {
		return 0;
	}

	/**
	 * Multimodal operations not supported yet
	 */
	@Override
	public double[] moduleOutput(int mode) {
		throw new UnsupportedOperationException("Cannot choose mode with wrapped DL4J networks yet");
	}

	/**
	 * Currently, there can only ever be one
	 */
	@Override
	public int numModules() {
		return 1;
	}

	/**
	 * There is only one that is used every time
	 */
	@Override
	public int[] getModuleUsage() {
		throw new UnsupportedOperationException("Should not ask for module usage with wrapped DL4J networks yet");
	}

}
