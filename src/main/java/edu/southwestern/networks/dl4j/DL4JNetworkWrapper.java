package edu.southwestern.networks.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;
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

	public static final int INDEX_INPUT_WIDTH = 0;
	public static final int INDEX_INPUT_HEIGHT = 1;
	public static final int INDEX_INPUT_CHANNELS = 2;
	
	private TensorNetwork net;
	private int[] inputShape; // width, height, channels
	private int[] outputShape; // width, height, channels

	public DL4JNetworkWrapper(TensorNetwork net, int[] inputShape, int[] outputShape) {
		this.net = net;
		this.inputShape = inputShape;
		this.outputShape = outputShape;
	}
	
	@Override
	public int numInputs() {
		return org.nd4j.linalg.util.ArrayUtil.prod(inputShape);
	}

	@Override
	public int numOutputs() {
		return org.nd4j.linalg.util.ArrayUtil.prod(outputShape);
	}

	/**
	 * No multitask networks supported yet,
	 * so the effective number always matches the actual number.
	 */
	@Override
	public int effectiveNumOutputs() {
		return numOutputs();
	}

	@Override
	public double[] process(double[] inputs) {
		// Put linear inputs into tensor INDArray
		INDArray tensorInput = Nd4j.create(inputs, inputShape, 'c');
		// Process using DL4J network
		INDArray tensorOutput = net.output(tensorInput);
		// Flatten to 1D array
		INDArray flat = Nd4j.toFlattened('c', tensorOutput);
		// Convert to primitive Java array
		return ArrayUtil.doubleArrayFromINDArray(flat);
	}

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
