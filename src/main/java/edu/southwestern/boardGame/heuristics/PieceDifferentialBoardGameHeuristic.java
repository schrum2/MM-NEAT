package edu.southwestern.boardGame.heuristics;

import edu.southwestern.boardGame.TwoDimensionalBoardGameState;

public class PieceDifferentialBoardGameHeuristic<T extends TwoDimensionalBoardGameState> implements BoardGameHeuristic<T>{

	@Override
	public double heuristicEvalution(T bgState) {
		assert bgState.getNumPlayers() == 2 : "PieceDifferentialBoardGameHeuristic only applies to two player games!";
		// Number of player 1's pieces minus number of player 2's pieces
		return bgState.numberOfPieces(0) - bgState.numberOfPieces(1);
	}
	
}
