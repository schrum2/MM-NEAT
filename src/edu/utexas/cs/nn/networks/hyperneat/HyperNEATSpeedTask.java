package edu.utexas.cs.nn.networks.hyperneat;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.tasks.Task;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

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
	public List<Pair<String, String>> getSubstrateConnectivity() {
		Pair<String, String> connect1 = new Pair<String, String>(input.name, process.name);
		Pair<String, String> connect2 = new Pair<String, String>(process.name, output.name);
		ArrayList<Pair<String, String>>	pairs = new ArrayList<Pair<String, String>>();
		pairs.add(connect1);
		pairs.add(connect2);
		return pairs;
	}

	@Override
	public double[] getSubstrateInputs(List<Substrate> inputSubstrates) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ArrayList evaluateAll(ArrayList population) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
