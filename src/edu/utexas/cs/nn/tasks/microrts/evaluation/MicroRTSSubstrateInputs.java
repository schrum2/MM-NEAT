package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.util.MiscUtil;
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
	
	/**
	 * creates a 2D array to be input to a neural network.
	 * 
	 * @param gs
	 * 			gameState containing the information to make the substrate
	 * @param evaluatedPlayer
	 * 			index of player currently being evaluated
	 * @return
	 * 			substrate reflecting the game state
	 */
	abstract double[][] getInputs(GameState gs, int evaluatedPlayer);
	
	/**
	 * for debugging
	 * 
	 * @param substrate
	 */
	protected void printSubstrateConfiguration(double[][] substrate, boolean waitAfterPrint){
		for(int i = 0; i < substrate.length; i++){
			for(int j = 0; j < substrate[0].length; j++){
				System.out.print(substrate[i][j] + " ");
			}
			System.out.println();
		}
		
		if(waitAfterPrint){
			MiscUtil.waitForReadStringAndEnterKeyPress();
		}
	}
}
