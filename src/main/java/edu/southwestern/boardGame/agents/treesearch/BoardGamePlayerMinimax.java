package edu.southwestern.boardGame.agents.treesearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.agents.HeuristicBoardGamePlayer;
import edu.southwestern.boardGame.heuristics.BoardGameHeuristic;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;

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
	protected boolean prune;
		
	/**
	 * This constructor assumes an opponent agent is being created.
	 * But if an evolved agent needs to be created, its heuristic
	 * can be re-loaded with the setHeuristic method.
	 */
	@SuppressWarnings("unchecked")
	public BoardGamePlayerMinimax(){
		try {
			boardHeuristic = (BoardGameHeuristic<T>) ClassCreation.createObject("boardGameOpponentHeuristic");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		sharedInit();
	}
	
	/**
	 * New instance with a given heuristic.
	 * @param bgh BoardGameHeuristic
	 */
	public BoardGamePlayerMinimax(BoardGameHeuristic<T> bgh){
		boardHeuristic = bgh;
		sharedInit();
	}	
	
	/**
	 * Initialization code common to both constructors
	 */
	private void sharedInit() {
		prune = false; // standard minimax does not prune
		depth = Parameters.parameters.integerParameter("minimaxSearchDepth");
		if(MMNEAT.boardGame.getNumPlayers() != 2) {
			System.out.println("The BoardGamePlayerMinimax can only be applied to two-player games");
			System.out.println("This one has " + MMNEAT.boardGame.getNumPlayers());
			System.exit(1);
		}
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
		assert current.getNumPlayers() == 2 : "Only works for two player games."; // TODO: Generalize this later
		
		int currentPlayer = current.getCurrentPlayer();
		// For a two player game, player 0 tries to maximize, and player 1 tries to minimize
		boolean maximize = currentPlayer == 0;
		
		List<T> poss = new ArrayList<T>();
		poss.addAll(current.possibleBoardGameStates(current));

		//System.out.println(poss);
		
		// If occasional random moves are allowed, then minimax calculation can be skipped
		double rand = RandomNumbers.randomGenerator.nextDouble();
		if(rand < Parameters.parameters.doubleParameter("minimaxRandomRate")){
			return RandomNumbers.randomElement(poss);
		} 
		
		double[] utilities = new double[poss.size()]; // Stores the network's outputs

		double alpha = ALPHA;
		double beta = BETA;

		int index = 0;
		double v = maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		for(T bgs : poss){ // Gets the network's outputs for all possible BoardGameStates
			// Use !maximize because the next level down are the opponent's moves
			utilities[index] = minimax(bgs, depth, alpha, beta, !maximize);
			
			// Pruning is only for the alpha-beta agent
			if(prune) {
				// Update alpha/beta (copied code)
				if(maximize) {
					v = Math.max(v, utilities[index]);
					alpha = Math.max(alpha, v);
					if(beta <= alpha){
						break; // Beta cut-off
					}
				} else { // minimize 
					v = Math.min(v, utilities[index]);
					beta = Math.min(beta, v);
					if(beta <= alpha){
						break; // Alpha cut-off
					}
				}
			}
			index++;
		}
		
		//System.out.println(Arrays.toString(utilities));
		// Best move (either min or max) for current player
		int selectedIndex = bestIndex(utilities, maximize);		
		// If there is a second option, and random number is less than the second-best chance, then switch
		if(utilities.length > 1 && rand < Parameters.parameters.doubleParameter("minimaxSecondBestRate")) {
			// Make the current best option seem the worst possible
			utilities[selectedIndex] = maximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
			// Best among the remaining utilities
			selectedIndex = bestIndex(utilities, maximize);		
		}
		
		return poss.get(selectedIndex); 
	}
	
	/**
	 * Based on utility scores, get index of either the max or min value,
	 * depending on maximize parameter.
	 * @param utilities utilities of possible game states
	 * @param maximize pick the max if true, and the min otherwise
	 * @return index of best utility, depending on value of maximize
	 */
	private static int bestIndex(double[] utilities, boolean maximize) {
		if(maximize) {
			// Best move for player 1: BoardGameState which produced the highest network output
			return StatisticsUtilities.argmax(utilities);
		} else {
			// Best move for player 2: BoardGameState which produced the lowest network output
			return StatisticsUtilities.argmin(utilities);
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
				double v = minimax(childState, depth-1, alpha, beta, !maxPlayer);
				bestValue = Math.max(bestValue, v);
			}
			return bestValue;
		}else{
			double bestValue = Double.POSITIVE_INFINITY;
			Set<T> poss = bgState.possibleBoardGameStates(bgState);
			
			for(T childState: poss){
				double v = minimax(childState, depth-1, alpha, beta, !maxPlayer);
				bestValue = Math.min(bestValue, v);
			}
			return bestValue;
		}
	}	
}
