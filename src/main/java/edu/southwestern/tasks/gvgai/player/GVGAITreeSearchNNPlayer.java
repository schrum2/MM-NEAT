package edu.southwestern.tasks.gvgai.player;

import java.util.List;
import java.util.Random;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;
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
public class GVGAITreeSearchNNPlayer<T extends Network> extends GVGAINNPlayer<T> {
	
	private static int depth; // Used to keep track of how far down the Tree to check
	protected static final double ALPHA = Double.NEGATIVE_INFINITY; // Holds the Starting Value for Alpha
	protected static final double BETA = Double.POSITIVE_INFINITY; // Holds the Starting Value for Beta
	protected boolean prune;
	
	Random random = RandomNumbers.randomGenerator;
	
	public GVGAITreeSearchNNPlayer(){
		depth = Parameters.parameters.integerParameter("minimaxSearchDepth");
		prune = true;
	}
		
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		
		List<ACTIONS> poss = stateObs.getAvailableActions(); // Stores all currently possible ACTIONS
		
		// If occasional random moves are allowed, then minimax calculation can be skipped
		double rand = random.nextDouble();
		if(rand < Parameters.parameters.doubleParameter("minimaxRandomRate")){
			return RandomNumbers.randomElement(poss);
		}

		double[] utilities = new double[poss.size()]; // Stores the network's outputs

		double alpha = ALPHA;
		double beta = BETA;

		int index = 0;
		double v = Double.NEGATIVE_INFINITY;

		for(ACTIONS act : poss){ // Gets the network's outputs for all possible BoardGameStates

			StateObservation temp = stateObs.copy();
			temp.advance(act);
			
			utilities[index] = minimax(temp, depth, alpha, beta);

			// Pruning is only for the alpha-beta agent
			if(prune) {
				// Update alpha/beta (copied code)
				v = Math.max(v, utilities[index]);
				alpha = Math.max(alpha, v);
				if(beta <= alpha){
					break; // Beta cut-off
				}
			} // No minimizing pruning because this is meant to be a Single Player
			index++;
		}
		
		return poss.get(StatisticsUtilities.argmax(utilities));
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