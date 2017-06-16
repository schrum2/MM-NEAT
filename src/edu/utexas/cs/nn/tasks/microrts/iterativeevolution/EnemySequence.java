package edu.utexas.cs.nn.tasks.microrts.iterativeevolution;

import java.util.ArrayList;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import micro.ai.core.AI;

/**
 * @author alicequint
 *
 * extended by classes that exists to define
 * the maps and speed of sequence for iterative evolution
 */
public abstract class EnemySequence {
	
	protected boolean growingSet = Parameters.parameters.booleanParameter("microRTSGrowingEnemySet");
	protected int gensPerEnemy;
	private ArrayList<AI> appropriateEnemies = new ArrayList<>();
	private int generationOfLastUpdate = -1;
	protected AI[] enemies;
	
	/**
	 * 
	 * @param generation
	 * 				current generation
	 * @return
	 * 			appropriate AI for current generation
	 */
	public ArrayList<AI> getAppropriateEnemy(int generation) {
		if(generation != 0 && appropriateEnemies.isEmpty() && growingSet){ //post best watch, or resuming from before 
			for(int i = 0; i < generation; i++){
				if(i % gensPerEnemy == 0 && appropriateEnemies.size() < enemies.length){
					appropriateEnemies.add(enemies[generation/gensPerEnemy]);
				}
			}
			generationOfLastUpdate = generation;
		}
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