package edu.utexas.cs.nn.tasks.gvgai;

import java.util.List;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import gvgai.core.game.StateObservation;
import gvgai.core.player.AbstractPlayer;
import gvgai.ontology.Types.ACTIONS;
import gvgai.tools.ElapsedCpuTimer;

public class GVGAIOneStepNNPlayer<T extends Network> extends AbstractPlayer {
	
	public static Network network;
	public static final double BIAS = 1.0;
	
	public GVGAIOneStepNNPlayer(){
	}
	
	public GVGAIOneStepNNPlayer(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
	}
	
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