package boardGame.agents.treesearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import boardGame.BoardGameState;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.heuristics.BoardGameHeuristic;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

/**
 * Random selection process based on:
 * 
 * Temporal Difference Learning Versus Co-Evolution
 * for Acquiring Othello Position Evaluation
 * 
 * (Simon M. Lucas and Thomas P. Runarsson)
 * 
 * http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=10AB4B0966FEE51BE133255498065C42?doi=10.1.1.580.8400&rep=rep1&type=pdf
 * 
 * @author johnso17
 * 
 */
public class BoardGamePlayerMinimax<T extends BoardGameState> extends HeuristicBoardGamePlayer<T> {
	
	private static int depth; // Used to keep track of how far down the Tree to check
	protected static final double ALPHA = Double.NEGATIVE_INFINITY; // Holds the Starting Value for Alpha
	protected static final double BETA = Double.POSITIVE_INFINITY; // Holds the Starting Value for Beta
	
	Random random = RandomNumbers.randomGenerator;
	
	/**
	 * This constructor assumes an opponent agent is being created.
	 * But if an evolved agent needs to be created, its heuristic
	 * can be re-loaded with the setHeuristic method.
	 */
	@SuppressWarnings("unchecked")
	public BoardGamePlayerMinimax(){
		try {
			boardHeuristic = (BoardGameHeuristic<T>) ClassCreation.createObject("boardGameOpponentHeuristic");
			depth = Parameters.parameters.integerParameter("minimaxSearchDepth");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * New instance with a given heuristic.
	 * @param bgh BoardGameHeuristic
	 */
	public BoardGamePlayerMinimax(BoardGameHeuristic<T> bgh){
		boardHeuristic = bgh;
	}	
	
	/**
	 * Evaluates the current possible BoardGameStates to a certain Depth
	 * using the Minimax Evaluation function and returns the State
	 * with the highest Score
	 * 
	 * @param current BoardGameState to be played from
	 * @return Possible BoardGameState with the best Heuristic Score
	 */
	@Override
	public T takeAction(T current) {
		List<T> poss = new ArrayList<T>();
		poss.addAll(current.possibleBoardGameStates(current));
		double[] utilities = new double[poss.size()]; // Stores the network's ouputs

		int index = 0;
		for(T bgs : poss){ // Gets the network's outputs for all possible BoardGameStates
			utilities[index++] = minimax(bgs, depth, ALPHA, BETA, true); // Action is being taken as the Maximizing Player; maxPlayer == true
		}

		double rand = random.nextDouble();
		
		if(rand < Parameters.parameters.doubleParameter("minimaxRandomRate")){
			return RandomNumbers.randomElement(poss);
		}else{ // Will always return best Move
			return poss.get(StatisticsUtilities.argmax(utilities)); // Returns the BoardGameState which produced the highest network output
		}
	}
	
	/**
	 * Minimax Evaluation Function; evaluates all possible
	 * BoardGameStates from a given State and returns the
	 * Max Value to be Scored from that Board Move
	 * 
	 * @param bgState Possible BoardGame Move being evaluated
	 * @param depth Depth of the current Move in the Move Tree
	 * @param alpha Ignored by default Minimax, but used by Alpha-Beta Pruning Minimax
	 * @param beta Ignored by default Minimax, but used by Alpha-Beta Pruning Minimax
	 * @param maxPlayer Is the current Move being taken by the Player?
	 * @return Max Double Value to be Scored from the given Move
	 */
	protected double minimax(T bgState, int depth, double alpha, double beta, boolean maxPlayer){
		
		if(depth == 0 || bgState.endState()){
			return boardHeuristic.heuristicEvalution(bgState); // Return the Heuristic value of the Node
		}
		
		if(maxPlayer){
			double bestValue = Double.NEGATIVE_INFINITY;
			Set<T> poss = bgState.possibleBoardGameStates(bgState);
			
			for(T childState: poss){
				double v = minimax(childState, depth-1, ALPHA, BETA, !maxPlayer);
				bestValue = Math.max(bestValue, v);
			}
			return bestValue;
		}else{
			double bestValue = Double.POSITIVE_INFINITY;
			Set<T> poss = bgState.possibleBoardGameStates(bgState);
			
			for(T childState: poss){
				double v = minimax(childState, depth-1, ALPHA, BETA, !maxPlayer);
				bestValue = Math.min(bestValue, v);
			}
			return bestValue;
		}
	}
	
	public void setRandomSeed(long seed){
		random.setSeed(seed);
	}

}
