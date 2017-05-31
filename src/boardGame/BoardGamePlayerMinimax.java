package boardGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import boardGame.heuristics.BoardGameHeuristic;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class BoardGamePlayerMinimax<T extends BoardGameState> implements BoardGamePlayer<T> {
	
	BoardGameHeuristic<T> boardHeuristic; // Should generalize to take any heuristic function, not just a network eval
	private static final int DEPTH = 4; // Used to keep track of how far down the Tree to check
	
	@SuppressWarnings("unchecked")
	public BoardGamePlayerMinimax(){
		try {
			boardHeuristic = (BoardGameHeuristic<T>) ClassCreation.createObject("boardGameOpponentHeuristic");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
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
			utilities[index++] = minimax(bgs, DEPTH, true); // Action is being taken as the Maximizing Player; maxPlayer == true
		}

		return poss.get(StatisticsUtilities.argmax(utilities)); // Returns the BoardGameState which produced the highest network output
	}
	
	/**
	 * Minimax Evaluation Function; evaluates all possible
	 * BoardGameStates from a given State and returns the
	 * Max Value to be Scored from that Board Move
	 * 
	 * @param bgState Possible BoardGame Move being evaluated
	 * @param depth Depth of the current Move in the Move Tree
	 * @param maxPlayer Is the current Move being taken by the Player?
	 * @return Max Double Value to be Scored from the given Move
	 */
	private double minimax(T bgState, int depth, boolean maxPlayer){
		
		if(depth == 0 || bgState.endState()){
			return boardHeuristic.heuristicEvalution(bgState); // Return the Heuristic value of the Node
		}
		
		if(maxPlayer){
			double bestValue = Double.NEGATIVE_INFINITY;
			Set<T> poss = bgState.possibleBoardGameStates(bgState);
			
			for(T childState: poss){
				double v = minimax(childState, depth-1, !maxPlayer);
				bestValue = Math.max(bestValue, v);
			}
			return bestValue;
		}else{
			double bestValue = Double.POSITIVE_INFINITY;
			Set<T> poss = bgState.possibleBoardGameStates(bgState);
			
			for(T childState: poss){
				double v = minimax(childState, depth -1, !maxPlayer);
				bestValue = Math.min(bestValue, v);
			}
			return bestValue;
		}
	}
	
}
