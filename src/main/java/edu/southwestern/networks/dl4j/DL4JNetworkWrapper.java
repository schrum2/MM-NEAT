package edu.southwestern.networks.dl4j;

import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.networks.Network;

/**
 * Most of my domains depend on a phenotype represented by a class
 * that implements my Network interface. The point of this class is
 * to take a network that works in DL4J (which I've been packaging
 * up in the TensorNetwork interface) and use the methods from my
 * Network class as a facade to access those method.
 * @author Jacob Schrum
 */
public class DL4JNetworkWrapper implements Network {

	private TensorNetwork net;

	public DL4JNetworkWrapper(TensorNetwork net) {
		this.net = net;
	}
	
	@Override
	public int numInputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numOutputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int effectiveNumOutputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] process(double[] inputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isMultitask() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void chooseMode(int mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int lastModule() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] moduleOutput(int mode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numModules() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getModuleUsage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INDArray output(INDArray input) {
		// TODO Auto-generated method stub
		return null;
	}

}
