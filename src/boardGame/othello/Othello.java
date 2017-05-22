package boardGame.othello;

import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;

public class Othello implements BoardGame<OthelloState>{
	
	private int currentPlayer; // Used to keep track of whose turn it is
	private OthelloState board;
	
	public Othello(){
		currentPlayer = 0;
		board = new OthelloState();
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
		return board.getWinner();
	}

	@Override
	public void move(BoardGamePlayer<OthelloState> bgp) {
		bgp.takeAction(board);
		currentPlayer = (currentPlayer + 1) % 2; // Switches the currentPlayer
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public String getName() {
		return "Othello/Reversi";
	}

	@Override
	public String[] getFeatureLabels() {
		// TODO Auto-generated method stub
		String[] feature = new String[0];
		return feature;
	}

	@Override
	public void reset() {
		currentPlayer = 0;
		board = new OthelloState();
	}

}
