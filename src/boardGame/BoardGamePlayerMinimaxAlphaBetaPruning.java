package boardGame;

import java.util.Set;

public class BoardGamePlayerMinimaxAlphaBetaPruning<T extends BoardGameState> extends BoardGamePlayerMinimax<BoardGameState> {
	
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