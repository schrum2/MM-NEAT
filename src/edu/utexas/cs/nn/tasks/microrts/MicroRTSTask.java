package edu.utexas.cs.nn.tasks.microrts;

import java.util.List;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class MicroRTSTask extends NoisyLonerTask implements NetworkTask, HyperNEATTask{

	@Override
	public int numObjectives() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numCPPNInputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Substrate> getSubstrateInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] sensorLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] outputLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair oneEval(Genotype individual, int num) {
		// main from GameVisualSimulationTest
		return null;
	}
	
	//end methods from interface//

}
