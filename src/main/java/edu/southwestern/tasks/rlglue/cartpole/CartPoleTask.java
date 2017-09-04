package edu.southwestern.tasks.rlglue.cartpole;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.rlglue.RLGlueTask;

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
