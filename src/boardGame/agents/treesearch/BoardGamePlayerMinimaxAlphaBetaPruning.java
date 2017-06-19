package boardGame.agents.treesearch;

import java.util.Set;

import boardGame.BoardGameState;
import boardGame.heuristics.BoardGameHeuristic;
import edu.utexas.cs.nn.util.MiscUtil;

public class BoardGamePlayerMinimaxAlphaBetaPruning<T extends BoardGameState> extends BoardGamePlayerMinimax<T> {
	
	public BoardGamePlayerMinimaxAlphaBetaPruning() {
		super();
		prune = true;
	}
	
	public BoardGamePlayerMinimaxAlphaBetaPruning(BoardGameHeuristic<T> bgh) {
		super(bgh);
		prune = true;
	}

	@Override
	protected double minimax(T bgState, int depth, double alpha, double beta, boolean maxPlayer) {
		//System.out.println(maxPlayer + ":" + depth + ": alpha: " + alpha.getValue() + ", beta: " + beta.getValue());
		if(depth == 0 || bgState.endState()){
			double result = boardHeuristic.heuristicEvalution(bgState); // Return the Heuristic value of the Node
			//System.out.println("result = " + result);
			return result;
		}
		
		if(maxPlayer){
			double v = Double.NEGATIVE_INFINITY;
			Set<T> poss = bgState.possibleBoardGameStates(bgState);
			
			for(T childState: poss){
				v = Math.max(v, minimax(childState, depth-1, alpha, beta, false));
				alpha = Math.max(alpha, v);
//				System.out.println("MAX: alpha: " + alpha.getValue() + ", beta: " + beta.getValue() + ", v = " + v);
//				System.out.println(childState);
//				MiscUtil.waitForReadStringAndEnterKeyPress();
				if(beta <= alpha){
					//System.out.println("BETA CUT");
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
//				System.out.println("MIN: alpha: " + alpha.getValue() + ", beta: " + beta.getValue() + ", v = " + v);
//				System.out.println(childState);
//				MiscUtil.waitForReadStringAndEnterKeyPress();
				if(beta <= alpha){
					//System.out.println("ALPHA CUT");
					break; // Alpha cut-off
				}
			}
			return v;
		}
		
	}	
	
		
	
}