package edu.utexas.cs.nn.tasks.microrts;

import java.util.List;

import micro.ai.core.AI;
import micro.ai.core.AIWithComputationBudget;
import micro.ai.core.ParameterSpecification;
import micro.rts.GameState;
import micro.rts.PlayerAction;

public class NNMicroRTSAgent extends AIWithComputationBudget{

	public NNMicroRTSAgent(int mt, int mi) {
		super(mt, mi);
		// TODO Auto-generated constructor stub
	}

	//methods from AIWithComputationBudget
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AI clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParameterSpecification> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}
	//END methods below here from AIWithComputationBudget
}
