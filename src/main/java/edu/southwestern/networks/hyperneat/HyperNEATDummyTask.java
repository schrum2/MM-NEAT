package edu.southwestern.networks.hyperneat;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.Task;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;
/**
 * Dummy hyperNEAT task used for testing purposes
 * @author Lauren Gillespie
 *
 */
public class HyperNEATDummyTask<T> implements HyperNEATTask, Task, SinglePopulationTask<T> {

	//Substrates
	private Substrate input;
	private Substrate process;
	private Substrate output;

	public HyperNEATDummyTask() {
		input = new Substrate(new Pair<Integer, Integer>(3, 3), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 0, 0), "I_0");
		process = new Substrate(new Pair<Integer, Integer>(3, 3), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 4, 0), "P_0");
		output = new Substrate(new Pair<Integer, Integer>(2, 4), Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 8, 0), "O_0");
	}


	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs;
	}
	
	/**
	 * returns substrates from dummy task
	 */
	@Override
	public List<Substrate> getSubstrateInformation() {
		ArrayList<Substrate> subs = new ArrayList<Substrate>();
		subs.add(input);
		subs.add(process);
		subs.add(output);
		return subs;
	}

	/**
	 * returns connections from dummy task
	 */
	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity() {
		SubstrateConnectivity connect1 = new SubstrateConnectivity(input.getName(), process.getName(), SubstrateConnectivity.CTYPE_FULL);
		SubstrateConnectivity connect2 = new SubstrateConnectivity(process.getName(), output.getName(), SubstrateConnectivity.CTYPE_FULL);
		ArrayList<SubstrateConnectivity>	pairs = new ArrayList<SubstrateConnectivity>();
		pairs.add(connect1);
		pairs.add(connect2);
		return pairs;
	}

	/**
	 * Not necessary method
	 */
	@Override
	public int numObjectives() {
		return 1;
	}

	/**
	 * Not necessary
	 */
	@Override
	public double[] minScores() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Not necessary
	 */
	@Override
	public double getTimeStamp() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Not necessary
	 */
	@Override
	public void finalCleanup() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList evaluateAll(ArrayList population) {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public void flushSubstrateMemory() {
		// Does nothing: This task does not cache substrate information
	}


	@Override
	public void postConstructionInitialization() {
		System.out.println("set up dummy hyperNEAT task. Used for testing purposes only");
	}

}
