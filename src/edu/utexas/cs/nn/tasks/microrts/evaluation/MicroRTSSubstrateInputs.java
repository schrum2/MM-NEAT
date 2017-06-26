package edu.utexas.cs.nn.tasks.microrts.evaluation;

import micro.rts.GameState;

/**
 * abstract class extended by all substrate input configurations
 * used by microRTS
 * 
 * @author alicequint
 *
 */
abstract class MicroRTSSubstrateInputs {
	
	protected int numSubstrates;
	
	abstract double[][] getInputs(GameState gs);
	
}
