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
public class FastEnemySequence implements EnemySequence{

	private static final int gensPerEnemy = 1;
	private ArrayList<AI> appropriateEnemies = new ArrayList<>();
	private int generationOfLastUpdate = -1;

	private final AI[] enemies = new AI[]{
			new RandomBiasedAI(), //used in competition
			new POWorkerRush(), //used in competition
			new POLightRush(), //used in competition
			new NaiveMCTS(), //used in competition
			new UCT(),
			new MLPSMCTS(),
			new PuppetSearchMCTS(),
			new PortfolioAI(),
	};

	@Override
	public ArrayList<AI> getAppropriateEnemy(int generation) {
		if(growingSet){
				if(generationOfLastUpdate != generation && generation % gensPerEnemy == 0 && appropriateEnemies.size() < enemies.length){
					generationOfLastUpdate = generation;
					if(CommonConstants.watch)
						System.out.println("gen: " + generation + ", adding to set: " + enemies[generation/gensPerEnemy]); //index out of bounds! gen x 2
					appropriateEnemies.add(enemies[generation/gensPerEnemy]);
				}
			return appropriateEnemies;
		}else {
			appropriateEnemies = new ArrayList<>(1); // only one enemy: set does not grow
			appropriateEnemies.add(enemies[Math.min(generation/gensPerEnemy, enemies.length-1)]);
		}
		return appropriateEnemies;
	}

}