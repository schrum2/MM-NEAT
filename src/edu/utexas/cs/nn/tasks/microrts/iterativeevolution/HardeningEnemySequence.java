package edu.utexas.cs.nn.tasks.microrts.iterativeevolution;

import java.util.ArrayList;

import micro.ai.RandomAI;
import micro.ai.RandomBiasedAI;
import micro.ai.abstraction.partialobservability.POLightRush;
import micro.ai.abstraction.partialobservability.POWorkerRush;
import micro.ai.core.AI;
import micro.ai.mcts.mlps.MLPSMCTS;
import micro.ai.mcts.naivemcts.NaiveMCTS;
import micro.ai.mcts.uct.UCT;
import micro.ai.portfolio.PortfolioAI;

/**
 * @author quintana
 * preliminary sequence of enemies for iterative evolution
 */
public class HardeningEnemySequence implements EnemySequence{

	private static final int gensPerEnemy = 7;
	
	private final AI[] enemies = new AI[]{
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
	
	public ArrayList<AI> getAppropriateEnemy(int generation){
		ArrayList<AI> appropriateEnemies = new ArrayList<AI>();
		appropriateEnemies.add(enemies[Math.min(gensPerEnemy, enemies.length-1)]);
		return appropriateEnemies;
	}
}
