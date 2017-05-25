package boardGame.othello;

import boardGame.TwoDimensionalBoardGame;

public class Othello extends TwoDimensionalBoardGame<OthelloState>{
	
	/**
	 * Default Constructor
	 */
	public Othello(){
		super(new OthelloState());
	}
	
	/**
	 * Returns the number of Players for Othello
	 * 
	 * @return 2, the number of Players for Othello
	 */
	@Override
	public int getNumPlayers() {
		return 2;
	}

	/**
	 * Returns a String containing the name of the Game
	 * 
	 * @return String containing "Othello/Reversi"
	 */
	@Override
	public String getName() {
		return "Othello/Reversi";
	}

}
