package boardGame.rps;

import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public abstract class RPSPlayer implements BoardGamePlayer{

	/**
	 * Allows a Player to select a Move based on its internal Logic
	 * 
	 * @param current RPSState being used to Play
	 * @return int representing the Move a Player took
	 */
	public abstract int selectMove(RPSState current);

	/**
	 * Updates a copy of the given BoardGameState based on the actions a Player took
	 * 
	 * @param current BoardGameState being used to Play
	 * @return BoardGameState affected by the Move a Player took
	 */
	@Override
	public BoardGameState takeAction(BoardGameState current) {
		RPSState state = (RPSState) current.copy(); // Copies the current BoardGameState

		state.playerMoves[state.nextPlayer++] = selectMove(state);
		return state;
	}
}
