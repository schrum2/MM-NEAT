package boardGame.agents.treesearch;

import java.util.Set;

import boardGame.BoardGameState;
import boardGame.heuristics.BoardGameHeuristic;
import boardGame.heuristics.PieceDifferentialBoardGameHeuristic;

public class BoardGamePlayerMinimaxAlphaBetaPruning<T extends BoardGameState> extends BoardGamePlayerMinimax<BoardGameState> {
	
	public BoardGamePlayerMinimaxAlphaBetaPruning(BoardGameHeuristic bgh) {
		super(bgh);
	}

	@Override
	protected double minimax(BoardGameState bgState, int depth, double alpha, double beta, boolean maxPlayer) {
		
		if(depth == 0 || bgState.endState()){
			boardHeuristic.heuristicEvalution(bgState);
		}
		
		if(maxPlayer){
			double v = Double.NEGATIVE_INFINITY;
			
			Set<BoardGameState> poss = bgState.possibleBoardGameStates(bgState);
			
			for(BoardGameState childState : poss){
				v = Math.max(v, minimax(childState, depth-1, alpha, beta, false));
				alpha = Math.max(alpha, v);
				if(beta <= alpha){
					break; // Beta cut-off
				}
			}
			return v;
			
		}else{
			double v = Double.POSITIVE_INFINITY;
			
			Set<BoardGameState> poss = bgState.possibleBoardGameStates(bgState);
			
			for(BoardGameState childState : poss){
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