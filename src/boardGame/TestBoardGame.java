package boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.agents.BoardGamePlayer;

public class TestBoardGame<T extends BoardGameState> implements BoardGame<T>{

	@Override
	public int getNumPlayers() {
		return 2;
	}

	@Override
	public boolean isGameOver() {
		return true;
	}

	@Override
	public double[] getDescription() {
		return new double[]{};
	}

	@Override
	public List<Integer> getWinners() {
		List<Integer> winners = new ArrayList<Integer>();
		winners.add(0);
		return winners;
	}

	@Override
	public void move(BoardGamePlayer<T> bgp) {
	}

	@Override
	public int getCurrentPlayer() {
		return 0;
	}

	@Override
	public BoardGameState getCurrentState() {
		return null;
	}

	@Override
	public String getName() {
		return "Test Board Game: Player 1 always wins";
	}

	@Override
	public void reset() {
	}

}
