package edu.utexas.cs.nn.tasks.gvgai;

import java.util.List;
import java.util.Random;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import gvgai.core.game.StateObservation;
import gvgai.core.player.AbstractPlayer;
import gvgai.ontology.Types.ACTIONS;
import gvgai.tools.ElapsedCpuTimer;

public class GVGAITreeSearchNNPlayer<T extends Network> extends AbstractPlayer {
	
	public static Network network;
	public static final double BIAS = 1.0;
	
	private static int depth; // Used to keep track of how far down the Tree to check
	protected static final double ALPHA = Double.NEGATIVE_INFINITY; // Holds the Starting Value for Alpha
	protected static final double BETA = Double.POSITIVE_INFINITY; // Holds the Starting Value for Beta
	protected boolean prune;
	
	Random random = RandomNumbers.randomGenerator;
	
	public GVGAITreeSearchNNPlayer(){
		sharedInit();
	}
	
	public GVGAITreeSearchNNPlayer(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		sharedInit();
	}
	
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		// TODO: Add kick-off code here
		
		
		
		return null;
	}
	
	private void sharedInit() {
		prune = false; // standard minimax does not prune
		depth = Parameters.parameters.integerParameter("minimaxSearchDepth");
	}
	
	/**
	 * Tree-Search Algorithm; based on half of the Minimax Algorithm
	 * 
	 * @param stateObs Possible StateObservation being evaluated
	 * @param depth Depth of the current Move in the Move Tree
	 * @param alpha Ignored by default Minimax, but used by Alpha-Beta Pruning Minimax
	 * @param beta Ignored by default Minimax, but used by Alpha-Beta Pruning Minimax
	 * @return Max Double Value to be Scored from the given Move
	 */
	protected double minimax(StateObservation stateObs, int depth, double alpha, double beta){

		if(depth == 0 || stateObs.isGameOver()){

			double gameScore = stateObs.getGameScore(); // The current Score in the game
			double gameHealth = stateObs.getAvatarHealthPoints(); // The Avatar's current HP
			double gameSpeed = stateObs.getAvatarSpeed(); // The Avatar's current speed
			double gameTick = stateObs.getGameTick(); // The game's current Tick

			double[] simpleFeatExtract = new double[]{gameScore, gameHealth, gameSpeed, gameTick, BIAS}; // Simple Feature Extractor; TODO: Probably replace later

			return network.process(simpleFeatExtract)[0]; // Return the Heuristic value of the Node
		}

		double bestValue = Double.NEGATIVE_INFINITY;
		List<ACTIONS> poss = stateObs.getAvailableActions(); // Stores all currently possible ACTIONS

		for(ACTIONS act: poss){
			StateObservation childState = stateObs.copy();
			childState.advance(act);

			double v = minimax(childState, depth-1, alpha, beta);
			bestValue = Math.max(bestValue, v);
		}
		return bestValue;
	}
	
	public void setRandomSeed(long seed){
		random.setSeed(seed);
	}
	
	
}