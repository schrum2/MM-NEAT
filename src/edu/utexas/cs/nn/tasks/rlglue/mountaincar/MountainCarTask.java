package edu.utexas.cs.nn.tasks.rlglue.mountaincar;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;

public class MountainCarTask<T extends Network> extends RLGlueTask<T> {

	public MountainCarTask() {
		super();
		MMNEAT.registerFitnessFunction("RL Return");
	}
	
	@Override
	public String[] outputLabels() {
		return new String[]{"Reverse", "Neutral", "Forward"};
	}
}
