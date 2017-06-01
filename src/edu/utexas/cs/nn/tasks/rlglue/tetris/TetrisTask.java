package edu.utexas.cs.nn.tasks.rlglue.tetris;

import org.rlcommunity.environments.tetris.Tetris;
import org.rlcommunity.environments.tetris.TetrisState;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueTask;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class TetrisTask<T extends Network> extends RLGlueTask<T> {

	private final boolean tetrisTimeSteps;
	private final boolean tetrisBlocksOnScreen;
	
	/**
	 * Default constructor
	 */
	public TetrisTask() {
		super();
		boolean tetris = (MMNEAT.rlGlueEnvironment instanceof Tetris);
		tetrisTimeSteps = tetris && Parameters.parameters.booleanParameter("tetrisTimeSteps");
		tetrisBlocksOnScreen = tetris && Parameters.parameters.booleanParameter("tetrisBlocksOnScreen");

		if (tetrisTimeSteps) { // Staying alive is good
			MMNEAT.registerFitnessFunction("Time Steps");
		}
		if (tetrisBlocksOnScreen) { // On game over, more blocks left is better
			MMNEAT.registerFitnessFunction("Blocks on Screen");
		}
		MMNEAT.registerFitnessFunction("RL Return");

		// Now register the other scores for Tetris
		MMNEAT.registerFitnessFunction("Rows of 1", null, false);
		MMNEAT.registerFitnessFunction("Rows of 2", null, false);
		MMNEAT.registerFitnessFunction("Rows of 3", null, false);
		MMNEAT.registerFitnessFunction("Rows of 4", null, false);
	}

	/**
	 * Returns number of other scores
	 */
	@Override
	public int numOtherScores() {
		return 4;
	}

	/**
	 * Calculates fitness for episode result based on blocks on screen and number of steps, and 
	 * saves this value and number of rows on screen into a pair of arrays
	 * 
	 * @param num index of return value in RL array
	 * @return pair of arrays containing fitness and number of rows on screen
	 */
	@Override
	public Pair<double[], double[]> episodeResult(int num) {
		double[] fitness = new double[] { rlReturn[num] }; // default
		Tetris game = (Tetris) environment;
		if (tetrisBlocksOnScreen || tetrisTimeSteps) {
			if (tetrisBlocksOnScreen) {
				@SuppressWarnings("unchecked")
				TetrisAfterStateAgent<T> tasa = (TetrisAfterStateAgent<T>) agent;
				int numberOfBlocksInState;
				// Checks if the we have reached the last step allowed
				if (rlNumSteps[num] == maxStepsPerEpisode) {
					// Sets to max to reward not losing for this long
					numberOfBlocksInState = TetrisState.worldHeight * TetrisState.worldWidth;

				} else {
					numberOfBlocksInState = tasa.getNumberOfBlocksInLastState();
				}
				if (tetrisBlocksOnScreen && tetrisTimeSteps) {
					fitness = new double[] { rlNumSteps[num], numberOfBlocksInState, rlReturn[num] };
				} else if (tetrisBlocksOnScreen) {
					fitness = new double[] { rlNumSteps[num], numberOfBlocksInState, rlReturn[num] };
				}
			} else { // timeSteps only
				fitness = new double[] { rlNumSteps[num], rlReturn[num] };
			}
		}
		Pair<double[], double[]> p = new Pair<double[], double[]>(fitness, game.getNumberOfRowsCleared());
		return p;
	}

	/**
	 * Calculates the number of objectives based on fitness
	 * 
	 * @return number of objectives
	 */
	@Override
	public int numObjectives() {
		// There are special cases, but the default fitness is the total summed
		// reward
		if (tetrisTimeSteps && tetrisBlocksOnScreen)
			return 3; // both fitnesses used
		if (tetrisTimeSteps || tetrisBlocksOnScreen)
			return 2; // only one is used
		else
			return 1; // default: just the RL Return
	}

	/**
	 * Assumes that the TetrisAfterState agent is always used, which has one
	 * output: the utility of the after state
	 */
	@Override
	public String[] outputLabels() {
		return new String[] { "Utility" };
	}
}
