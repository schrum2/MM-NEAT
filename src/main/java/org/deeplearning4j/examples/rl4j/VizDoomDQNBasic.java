package org.deeplearning4j.examples.rl4j;

import org.deeplearning4j.rl4j.mdp.vizdoom.Basic;
import org.deeplearning4j.rl4j.mdp.vizdoom.DeadlyCorridor;
import org.deeplearning4j.rl4j.mdp.vizdoom.VizDoom;

import edu.southwestern.util.random.RandomNumbers;
import vizdoom.SpecifyDLL;

public class VizDoomDQNBasic {

	public static void main(String[] args) {
		SpecifyDLL.specifyDLLPath(); // Added to allow the vizdoom.dll to be in a different location
		
//		VizDoom vd = new Basic(true);
		VizDoom vd = new DeadlyCorridor(true);
		int numActions = vd.getActionSpace().getSize();
		while(!vd.isDone()) {
			// Do random actions
			vd.step(RandomNumbers.randomGenerator.nextInt(numActions));
		}
		vd.close();
	}
}
