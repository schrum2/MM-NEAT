package edu.southwestern.networks.hyperneat;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.Task;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

/**
 * This class is used in certain JUnit tests, but it not a "real" task
 * @author schrum2
 *
 */
@SuppressWarnings("rawtypes")
public class HyperNEATSpeedTask implements HyperNEATTask, Task, SinglePopulationTask {

	//Substrates
	private Substrate input;
	private Substrate process;
	private Substrate output;
	
	public HyperNEATSpeedTask() {
		input = new Substrate(new Pair<Integer, Integer>(100, 10), Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 0, 0), "I_0");
		process = new Substrate(new Pair<Integer, Integer>(100, 10), Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 4, 0), "P_0");
		output = new Substrate(new Pair<Integer, Integer>(2, 2), Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, 8, 0), "O_0");

	}


	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs;
	}
	
	@Override
	public int numObjectives() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double[] minScores() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double getTimeStamp() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void finalCleanup() {
		throw new UnsupportedOperationException("Not supported yet.");

	}

	@Override
	public List<Substrate> getSubstrateInformation() {
		ArrayList<Substrate> subs = new ArrayList<Substrate>();
		subs.add(input);
		subs.add(process);
		subs.add(output);
		return subs;
	}

	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity() {
		SubstrateConnectivity connect1 = new SubstrateConnectivity(input.getName(), process.getName(), SubstrateConnectivity.CTYPE_FULL);
		SubstrateConnectivity connect2 = new SubstrateConnectivity(process.getName(), output.getName(), SubstrateConnectivity.CTYPE_FULL);
		ArrayList<SubstrateConnectivity>	pairs = new ArrayList<SubstrateConnectivity>();
		pairs.add(connect1);
		pairs.add(connect2);
		return pairs;
	}

	@Override
	public ArrayList evaluateAll(ArrayList population) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
