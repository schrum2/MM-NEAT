package boardGame.rps;

import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public abstract class RPSPlayer implements BoardGamePlayer{

	public abstract int takeAction(RPSState current);

	@Override
	public BoardGameState takeAction(BoardGameState current) {
		// TODO Auto-generated method stub
		return null;
	}
}
