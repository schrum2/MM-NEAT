package boardGame.checkers;

import boardGame.BoardGame;
import boardGame.TwoDimensionalBoardGame;

public class Checkers extends TwoDimensionalBoardGame<CheckersState> implements BoardGame<CheckersState>{
	
	public Checkers(){
		super(new CheckersState());
	}
	
	/**
	 * Returns the number of Players for Checkers
	 * 
	 * @return 2, the number of Players for Checkers
	 */
	@Override
	public int getNumPlayers() {
		return 2;
	}

	/**
	 * Returns a String containing the name of the Game, "Checkers"
	 * 
	 * @return String containing "Checkers"
	 */
	@Override
	public String getName() {
		return "Checkers";
	}

}
