package boardGame.fitnessFunction;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;

public class OthelloPieceFitness<T extends BoardGameState> implements BoardGameFitnessFunction<T> {

	private double[] fitness = new double[2]; // Othello always has two players
	
	@Override
	public double getFitness(BoardGamePlayer<T> player, int index){
		return fitness[index];
	}
	
	@Override
	public void updateFitness(T bgs, int index) {
		// Only calculate for end game
		if(bgs.endState()) {			
			int playerChips = 0;
			int opponentChips = 0;

			double[] description = bgs.getDescriptor();

			for (double d : description) {
				if (d == index) { // Found a Player Check
					playerChips++;
				}else if (d == (index + 1) % 2) { // Found an Enemy Check
					opponentChips++;
				}
			}
			fitness[index] = playerChips - opponentChips;
		}
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
