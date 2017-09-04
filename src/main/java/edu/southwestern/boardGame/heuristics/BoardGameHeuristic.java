package edu.southwestern.boardGame.heuristics;

import edu.southwestern.boardGame.BoardGameState;

public interface BoardGameHeuristic<T extends BoardGameState> {
		
	/**
	 * Returns a Score based on the State
	 * 
	 * @param bgState The BoardGameState to be evaluated
	 * @return Double representing the Score of the specified State
	 */
	public double heuristicEvalution(T bgState);
	
}
