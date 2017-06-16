package edu.utexas.cs.nn.tasks.microrts.iterativeevolution;

import java.util.ArrayList;

import edu.utexas.cs.nn.parameters.CommonConstants;
import micro.ai.RandomBiasedAI;
import micro.ai.abstraction.partialobservability.POLightRush;
import micro.ai.abstraction.partialobservability.POWorkerRush;
import micro.ai.core.AI;
import micro.ai.mcts.mlps.MLPSMCTS;
import micro.ai.mcts.naivemcts.NaiveMCTS;
import micro.ai.mcts.uct.UCT;
import micro.ai.portfolio.PortfolioAI;
import micro.ai.puppet.PuppetSearchMCTS;

/**
 * sequence of the static opponents that are used in the
 * MicroRTS competition, but it switches very fast (every generation)
 * for testing
 * 
 * @author alicequint
 *
 */
public class FastEnemySequence extends EnemySequence{

	public FastEnemySequence(){
		gensPerEnemy = 1;
		enemies = new AI[]{
				new RandomBiasedAI(),
				new POWorkerRush(),
				new POLightRush(),
				new NaiveMCTS(),
				new UCT(),
				new MLPSMCTS(),
				new PuppetSearchMCTS(),
				new PortfolioAI(),
		};
	}
}