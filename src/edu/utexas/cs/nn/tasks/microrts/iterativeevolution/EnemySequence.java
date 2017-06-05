package edu.utexas.cs.nn.tasks.microrts.iterativeevolution;

import micro.ai.core.AI;

/**
 * @author alicequint
 *
 * extended by classes that exists to define
 * the maps and speed of sequence for iterative evolution
 */
public interface EnemySequence {
	
	/**
	 * 
	 * @param generation
	 * 				current generation
	 * @return
	 * 			appropriate AI for current generation
	 */
	public AI getAppropriateEnemy(int generation);
}