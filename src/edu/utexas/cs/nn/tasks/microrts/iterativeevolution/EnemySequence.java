package edu.utexas.cs.nn.tasks.microrts.iterativeevolution;

import java.util.ArrayList;

import edu.utexas.cs.nn.parameters.Parameters;
import micro.ai.core.AI;

/**
 * @author alicequint
 *
 * extended by classes that exists to define
 * the maps and speed of sequence for iterative evolution
 */
public interface EnemySequence {
	
	boolean growingSet = Parameters.parameters.booleanParameter("microRTSGrowingEnemySet");
	
	/**
	 * 
	 * @param generation
	 * 				current generation
	 * @return
	 * 			appropriate AI for current generation
	 */
	public ArrayList<AI> getAppropriateEnemy(int generation);
}