package boardGame.rps;

import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public abstract class RPSPlayer implements BoardGamePlayer{

	public abstract int takeAction(RPSState current);

	@Override
	public BoardGameState takeAction(BoardGameState current) {
		RPSState state = (RPSState) current;
		// TODO: Copy the state before modifying it
		state.playerMoves[state.nextPlayer++] = takeAction(state);
		return state;
	}
}
