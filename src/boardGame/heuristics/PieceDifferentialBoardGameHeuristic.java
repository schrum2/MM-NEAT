package boardGame.heuristics;

import boardGame.TwoDimensionalBoardGameState;
import edu.utexas.cs.nn.networks.Network;

public class PieceDifferentialBoardGameHeuristic<T extends Network, S extends TwoDimensionalBoardGameState> implements BoardGameHeuristic<S>{

	@Override
	public double heuristicEvalution(S bgState) {
		return bgState.numberOfPieces(bgState.getNextPlayer()) - bgState.numberOfPieces((bgState.getNextPlayer() + 1) % bgState.getNumPlayers());
	}
	
}
