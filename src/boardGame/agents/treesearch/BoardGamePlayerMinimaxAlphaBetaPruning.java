package boardGame.agents.treesearch;

import java.util.Set;

import boardGame.BoardGameState;
import boardGame.heuristics.BoardGameHeuristic;

public class BoardGamePlayerMinimaxAlphaBetaPruning<T extends BoardGameState> extends BoardGamePlayerMinimax<T> {
	
	public BoardGamePlayerMinimaxAlphaBetaPruning() {
		super();
	}
	
	public BoardGamePlayerMinimaxAlphaBetaPruning(BoardGameHeuristic<T> bgh) {
		super(bgh);
	}

	@Override
	protected double minimax(T bgState, int depth, double alpha, double beta, boolean maxPlayer) {
		
		if(depth == 0 || bgState.endState()){
			return boardHeuristic.heuristicEvalution(bgState); // Return the Heuristic value of the Node
		}
		
		if(maxPlayer){
			double v = Double.NEGATIVE_INFINITY;
			Set<T> poss = bgState.possibleBoardGameStates(bgState);
			
			for(T childState: poss){
				v = Math.max(v, minimax(childState, depth-1, alpha, beta, false));
				alpha = Math.max(alpha, v);
				if(beta <= alpha){
					break; // Beta cut-off
				}
			}
			return v;
		}else{
			double v = Double.POSITIVE_INFINITY;
			Set<T> poss = bgState.possibleBoardGameStates(bgState);
			
			for(T childState: poss){
				v = Math.min(v, minimax(childState, depth-1, alpha, beta, true));
				beta = Math.min(beta, v);
				if(beta <= alpha){
					break; // Alpha cut-off
				}
			}
			return v;
		}
		
	}	
	
		
	
}