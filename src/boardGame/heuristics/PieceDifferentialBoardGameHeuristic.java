package boardGame.heuristics;

import boardGame.TwoDimensionalBoardGameState;

public class PieceDifferentialBoardGameHeuristic<T extends TwoDimensionalBoardGameState> implements BoardGameHeuristic<T>{

	@Override
	public double heuristicEvalution(T bgState) {
		return bgState.numberOfPieces(bgState.getNextPlayer()) - bgState.numberOfPieces((bgState.getNextPlayer() + 1) % bgState.getNumPlayers());
	}
	
}
