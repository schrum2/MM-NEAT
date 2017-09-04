package edu.southwestern.boardGame.fitnessFunction;

import java.util.Arrays;

import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.othello.OthelloState;

public class OthelloPieceFitness implements BoardGameFitnessFunction<OthelloState> {

	private double[] fitness = new double[2]; // Othello always has two players
	
	@Override
	public double getFitness(BoardGamePlayer<OthelloState> player, int index){
		return fitness[index];
	}
	
	@Override
	public void updateFitness(OthelloState bgs, int index) {
		int playerChips = bgs.numberOfPieces(index);
		int opponentChips = bgs.numberOfPieces((index+1)%2);
		// Always recalculate
		fitness[index] = playerChips - opponentChips;
		//System.out.println("\nupdateFitness("+index+"):" + bgs + "\n" + Arrays.toString(fitness) + "\n");
		assert fitness[index] >= -64 && fitness[index] <= 64 : "There are only 64 spaces on the board: " + Arrays.toString(fitness);
	}


	@Override
	public String getFitnessName() {
		return "Othello Piece Fitness";
	}

	@Override
	public void reset() {
		fitness = new double[2]; // Othello always has two players;
	}

	@Override
	public double getMinScore() {
		return -64; // 0 Player Pieces - 64 Opponent Pieces
	}

}
