package boardGame.heuristics;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;

public abstract class HeuristicBoardGamePlayer<T extends BoardGameState> implements BoardGamePlayer<T> {

	protected BoardGameHeuristic<T> boardHeuristic;
	
	/**
	 * Sets the Heuristic for the BoardGamePlayer
	 * 
	 * @param bgh BoardGameHeuristic to be used by the Player
	 */
	public void setHeuristic(BoardGameHeuristic<T> bgh) {
		boardHeuristic = bgh;
	}


}
