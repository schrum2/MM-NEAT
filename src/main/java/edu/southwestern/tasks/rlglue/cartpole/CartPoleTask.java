package edu.utexas.cs.nn.tasks.rlglue.cartpole;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;

public class CartPoleTask<T extends Network> extends RLGlueTask<T> {

	public CartPoleTask() {
		super();
		MMNEAT.registerFitnessFunction("RL Return");
	}
	
	@Override
	public String[] outputLabels() {
		return new String[]{"Left", "Right"};
	}
}
