package boardGame.ttt;

import boardGame.BoardGame;
import boardGame.TwoDimensionalBoardGame;

public class TicTacToe extends TwoDimensionalBoardGame<TicTacToeState> implements BoardGame<TicTacToeState>{

	
	public TicTacToe() {
		super(new TicTacToeState());
	}
	
	/**
	 * Returns the number of Players for Tic-Tac-Toe
	 * 
	 * @return 2, the number of Players
	 */
	@Override
	public int getNumPlayers() {
		return 2; // TicTacToe can only ever have two Players
	}

	/**
	 * Returns a String containing the name of the game, "Tic-Tac-Toe"
	 * 
	 * @return String containing "Tic-Tac-Toe"
	 */
	@Override
	public String getName() {
		return "Tic-Tac-Toe";
	}
	
}