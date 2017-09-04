package edu.southwestern.boardGame.fitnessFunction;

import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.parameters.Parameters;

public class OpeningRandomMovesScore<T extends BoardGameState> implements BoardGameFitnessFunction<T> {

	@Override
	public double getFitness(BoardGamePlayer<T> player, int index) {
		return Parameters.parameters.integerParameter("boardGameOpeningRandomMoves");
	}

	@Override
	public void updateFitness(T bgs, int index) {
	}

	@Override
	public String getFitnessName() {
		return "OpeningRandomMovesScore";
	}

	@Override
	public void reset() {
	}

	@Override
	public double getMinScore() {
		return 0;
	}

}
