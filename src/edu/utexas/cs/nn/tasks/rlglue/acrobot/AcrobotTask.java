package edu.utexas.cs.nn.tasks.rlglue.acrobot;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;

public class AcrobotTask<T extends Network> extends RLGlueTask<T> {
	
	public AcrobotTask() {
		super();
		MMNEAT.registerFitnessFunction("RL Return");
	}
	
	@Override
	public String[] outputLabels() {
		return new String[]{"Torque"};
	}
}
