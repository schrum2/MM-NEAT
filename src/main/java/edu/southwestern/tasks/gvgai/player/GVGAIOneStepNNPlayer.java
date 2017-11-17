package edu.southwestern.tasks.gvgai.player;

import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.util.stats.StatisticsUtilities;
import gvgai.core.game.StateObservation;
import gvgai.ontology.Types.ACTIONS;
import gvgai.tools.ElapsedCpuTimer;

/**
 * 
 * @author Darwin Johnson
 *
 * @param <T>
 */
public class GVGAIOneStepNNPlayer<T extends Network> extends GVGAINNPlayer<T> {
	
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		
		List<ACTIONS> acts = stateObs.getAvailableActions(); // Stores all currently possible ACTIONS
		double[] evals = new double[acts.size()];
		int index = 0;
		
		for(ACTIONS move : acts){ // Cycles through all currently possible ACTIONS
			StateObservation temp = stateObs.copy();
			temp.advance(move); // Updates the copied State with one possible action
			
			double gameScore = temp.getGameScore(); // The current Score in the game
			double gameHealth = temp.getAvatarHealthPoints(); // The Avatar's current HP
			double gameSpeed = temp.getAvatarSpeed(); // The Avatar's current speed
			double gameTick = temp.getGameTick(); // The game's current Tick
			
			double[] simpleFeatExtract = new double[]{gameScore, gameHealth, gameSpeed, gameTick, BIAS}; // Simple Feature Extractor; TODO: Probably replace later
			evals[index++] = network.process(simpleFeatExtract)[0]; // Stores the Network's evaluation
		}
		
		return acts.get(StatisticsUtilities.argmax(evals));
	}

}