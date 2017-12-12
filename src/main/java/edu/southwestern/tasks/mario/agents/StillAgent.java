package edu.southwestern.tasks.mario.agents;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;

public class StillAgent  implements Agent {

	@Override
	public void reset() {
		// No state to reset
	}

	@Override
	public boolean[] getAction(Environment observation) {
		// Does not press any buttons
		return new boolean[Environment.numberOfButtons];
	}

	@Override
	public AGENT_TYPE getType() {
		return Agent.AGENT_TYPE.AI;
	}

	@Override
	public String getName() {
		return "Still";
	}

	@Override
	public void setName(String name) {
		// Not needed
	}

}
