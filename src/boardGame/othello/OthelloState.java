package boardGame.othello;

import java.util.List;

import boardGame.BoardGameState;

public class OthelloState implements BoardGameState{

	@Override
	public double[] getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean endState() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends BoardGameState> List<T> possibleBoardGameStates(T currentState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoardGameState copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
