package edu.utexas.cs.nn.tasks.microrts;

import micro.ai.PassiveAI;
import micro.ai.RandomAI;
import micro.ai.RandomBiasedAI;
import micro.ai.abstraction.partialobservability.POLightRush;
import micro.ai.abstraction.partialobservability.POWorkerRush;
import micro.ai.core.AI;
import micro.ai.mcts.mlps.MLPSMCTS;
import micro.ai.mcts.naivemcts.NaiveMCTS;
import micro.ai.mcts.uct.UCT;
import micro.ai.portfolio.PortfolioAI;

// TODO: Add comments

// TODO: Also, need to make this into an interface
public class EnemySequence {
	
	private static final int gensPerEnemy = 5;
	private static final AI[] enemies = new AI[]{
//		new PassiveAI(),
		new RandomAI(),
		new RandomBiasedAI(), //used in competition
		new UCT(),
		new POWorkerRush(), //used in competition
		new POLightRush(),  //used in competition
		new NaiveMCTS(), //used in competition
		new MLPSMCTS(),
		new PortfolioAI()
	};
	
	// TODO: This will no longer be a static method once this is an interface
	public static AI getAppropriateEnemy(int generation){
		return enemies[Math.min(gensPerEnemy, enemies.length-1)];
	}
}