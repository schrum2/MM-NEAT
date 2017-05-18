package boardGame.rps;

import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public class RockPaperScissors implements BoardGame{

	private int currentPlayer; // Used to keep track of whose turn it is
	private RPSState board;
	
	public RockPaperScissors(){
		currentPlayer = 0;
		board = new RPSState();
	}
	
	@Override
	public int getNumPlayers() {
		return 2;
	}

	@Override
	public boolean isGameOver() {
		return board.endState();
	}

	@Override
	public List<Integer> getWinners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BoardGameState> possibleBoardGameStates(int player, BoardGameState currentState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void move(BoardGamePlayer bgp) {
		bgp.takeAction(board);
	}

	@Override
	public int getCurrentPlayer() {
		return 	currentPlayer;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
