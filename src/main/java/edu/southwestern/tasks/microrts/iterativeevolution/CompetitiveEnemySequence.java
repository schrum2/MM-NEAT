package edu.southwestern.tasks.microrts.iterativeevolution;

import micro.ai.abstraction.LightRush;
import micro.ai.abstraction.WorkerRush;
import micro.ai.core.AI;
import micro.ai.mcts.mlps.MLPSMCTS;
import micro.ai.mcts.naivemcts.NaiveMCTS;
import micro.ai.mcts.uct.UCT;
import micro.ai.portfolio.PortfolioAI;
import micro.ai.puppet.PuppetSearchMCTS;

/**
 * sequence of the static opponents that are used in the
 * MicroRTS competition
 * 
 * @author alicequint
 *
 */
public class CompetitiveEnemySequence extends EnemySequence{

	public CompetitiveEnemySequence(){
		gensPerEnemy = 20;
		
		enemies = new AI[]{
				new WorkerRush(),
				new LightRush(),
				new NaiveMCTS(),
				new UCT(),
				new MLPSMCTS(),
				new PuppetSearchMCTS(),
				new PortfolioAI(),
		};
	}



}