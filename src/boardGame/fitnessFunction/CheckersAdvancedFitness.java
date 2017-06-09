package boardGame.fitnessFunction;

import boardGame.BoardGameState;

/**
 * Fitness Function from:
 * 
 * A Case Study on the Critical Role
 * of Geometric Regularity in Machine Learning
 * 
 * (Jason Gauci and Kenneth O. Stanley)
 * 
 * http://eplex.cs.ucf.edu/papers/gauci_aaai08.pdf
 */
public class CheckersAdvancedFitness<T extends BoardGameState> implements BoardGameFitnessFunction<T> {

	private static final int KING = 2; // Checkers is structured so that the
										// Index for Kings is PlayerIndex + 2

	private double fitness = 0;
	
	public double getFitness(){
		return fitness;
	}
	
	@Override
	public void updateFitness(T bgs, int index) {

		int playerChecks = 0;
		int playerKings = 0;

		int opponentChecks = 0;
		int opponentKings = 0;

		double[] description = bgs.getDescriptor();

		for (double d : description) {
			if (d == index) { // Found a Player Check
				playerChecks++;
			} else if (d == index + KING) { // Found a Player King
				playerKings++;
			} else if (d == (index + 1) % 2) { // Found an Enemy Check
				opponentChecks++;
			} else if (d == (index + 1) % 2 + KING) { // Found an Enemy King
				opponentKings++;
			}
		}
		fitness += (100 + 2 * playerChecks + 3 * playerKings + 2 * (12 - opponentChecks) + 3 * (12 - opponentKings));
	}


	@Override
	public String getFitnessName() {
		return "Checkers Advanced Fitness";
	}


}
