package edu.southwestern.tasks.rlglue.tetris;

import org.rlcommunity.environments.tetris.Tetris;
import org.rlcommunity.environments.tetris.TetrisState;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.rlglue.RLGlueTask;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.stats.StatisticsUtilities;

public class TetrisTask<T extends Network> extends RLGlueTask<T> {

	private final boolean tetrisTimeSteps;
	private final boolean tetrisBlocksOnScreen;
	private final boolean tetrisAvgEmptySpaces;
	private final boolean tetrisAvgHoles;
	private final boolean tetrisLinesNotScore;
	
	/**
	 * Default constructor
	 */
	public TetrisTask() {
		super();
		tetrisTimeSteps = Parameters.parameters.booleanParameter("tetrisTimeSteps");
		tetrisBlocksOnScreen = Parameters.parameters.booleanParameter("tetrisBlocksOnScreen");
		tetrisAvgEmptySpaces = Parameters.parameters.booleanParameter("tetrisAvgEmptySpaces");
		tetrisAvgHoles = Parameters.parameters.booleanParameter("tetrisAvgNumHoles");
		tetrisLinesNotScore = Parameters.parameters.booleanParameter("tetrisLinesNotScore");
		
		if (tetrisTimeSteps) { // Staying alive is good
			MMNEAT.registerFitnessFunction("Time Steps");
		}
		if (tetrisBlocksOnScreen) { // On game over, more blocks left is better
			MMNEAT.registerFitnessFunction("Blocks on Screen");
		}
		if(tetrisAvgEmptySpaces) {
			MMNEAT.registerFitnessFunction("Average Number of Empty Spaces");
		}
		if(tetrisAvgHoles) {//sometimes a fitness, 2nd to last fitness
			MMNEAT.registerFitnessFunction("average holes on screen");
		}
		if(tetrisLinesNotScore) {
			MMNEAT.registerFitnessFunction("Lines cleared");
		} else {
			MMNEAT.registerFitnessFunction("RL Return");
		}


		
		// Now register the other scores for Tetris
		MMNEAT.registerFitnessFunction("Rows of 1", null, false);
		MMNEAT.registerFitnessFunction("Rows of 2", null, false);
		MMNEAT.registerFitnessFunction("Rows of 3", null, false);
		MMNEAT.registerFitnessFunction("Rows of 4", null, false);
		MMNEAT.registerFitnessFunction("Lines cleared",null,false);
		MMNEAT.registerFitnessFunction("Game Score",null,false); // same as RL Return
		MMNEAT.registerFitnessFunction("average num holes", null, false);//always an other score, sometimes a fitness
	}

	/**
	 * Returns number of other scores
	 */
	@Override
	public int numOtherScores() {
		return 7; // Each type of row, plus lines and score
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
		double[] fitness = new double[numObjectives()];
		int index = 0;
		if(tetrisTimeSteps) fitness[index++] = rlNumSteps[num]; // time steps
		if(tetrisBlocksOnScreen) { // more blocks in final state means an attempt was made to clear lines
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
			fitness[index++] = numberOfBlocksInState;
		}
		Tetris game = (Tetris) environment;
		// Average empty spaces across all piece placements
		double avgNumHoles = -game.getAverageNumHoles();
		if(tetrisAvgEmptySpaces) fitness[index++] = game.getAverageNumEmptySpaces();		
		if(tetrisAvgHoles)	fitness[index++] = avgNumHoles ;
		if(tetrisLinesNotScore) {
			fitness[index++] = game.getLinesCleared();
		} else {
			fitness[index++] = rlReturn[num]; // default
		}

		
		double[] rowCounts = game.getNumberOfRowsCleared();

		assert StatisticsUtilities.sum(ArrayUtil.zipMultiply(rowCounts, new double[]{1,2,3,4})) == game.getLinesCleared() : "Total of lines cleared of each type should equal total lines cleared";
		
		double[] otherScores = new double[numOtherScores()];
		
		otherScores[0] = rowCounts[0];
		otherScores[1] = rowCounts[1];
		otherScores[2] = rowCounts[2];
		otherScores[3] = rowCounts[3];
		otherScores[4] = game.getLinesCleared();
		otherScores[5] = rlReturn[num]; // Game score
		otherScores[6] = avgNumHoles;
		
		Pair<double[], double[]> p = new Pair<double[], double[]>(fitness, otherScores);
		return p;
	}

	/**
	 * Calculates the number of objectives based on fitness
	 * 
	 * @return number of objectives
	 */
	@Override
	public int numObjectives() {
		int total = 1; // Just RL Return
		if(tetrisAvgEmptySpaces) total++;
		if(tetrisTimeSteps) total++;
		if(tetrisBlocksOnScreen) total++;
		if(tetrisAvgHoles) total++;
		return total;
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
