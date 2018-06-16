package edu.southwestern.tasks.mspacman;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import oldpacman.game.Constants;
import oldpacman.game.internal.Ghost;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * One pacman eval consists of two separate evals: First in the full interleaved
 * game, but where only pill score counts. Second in game where ghosts start
 * edible.
 *
 * @author Jacob Schrum
 * @param <T>
 */
public class MsPacManOnlyPillScoreInFullVsEdibleFromCornersMultitask<T extends Network> extends MsPacManTask<T> {

	public static int[][][][] mazePowerPillGhostMap;
	public static int NUM_GHOST_LOCATION_OPTIONS = 100;

	public static int newGhostLocation(int mazeIndex, Ghost ghost) {
		int randomChoice = RandomNumbers.randomGenerator.nextInt(NUM_GHOST_LOCATION_OPTIONS);
		// System.out.println("mazeIndex:"+mazeIndex+",pacmanStartingPowerPillIndex:"+CommonConstants.pacmanStartingPowerPillIndex+",randomChoice:"+randomChoice+",ghostIndex:"+GameFacade.ghostToIndex(ghost.type));
		return mazePowerPillGhostMap[mazeIndex][CommonConstants.pacmanStartingPowerPillIndex][randomChoice][GameFacade
				.ghostToIndex(ghost.type)];
	}

	public MsPacManOnlyPillScoreInFullVsEdibleFromCornersMultitask() {
		Parameters.parameters.setBoolean("imprisonedWhileEdible", true);
		CommonConstants.imprisonedWhileEdible = true;
		loadMapPowerPillGhostMap(Parameters.parameters.stringParameter("mazePowerPillGhostMapping"));
	}

	public static void loadMapPowerPillGhostMap(String filename) {
		// 4 is number of power pills ... no constant for this?
		final int NUM_POWER_PILLS = 4;
		mazePowerPillGhostMap = new int[Constants.NUM_MAZES][NUM_POWER_PILLS][NUM_GHOST_LOCATION_OPTIONS][Constants.NUM_GHOSTS];
		int[][] progressIndices = new int[Constants.NUM_MAZES][NUM_POWER_PILLS]; // Initialized to 0
		try {
			Scanner s = new Scanner(new File(filename));
			while (s.hasNextInt()) {
				int mazeIndex = s.nextInt();
				int powerPillIndex = s.nextInt();
				int ghostNode1 = s.nextInt();
				int ghostNode2 = s.nextInt();
				int ghostNode3 = s.nextInt();
				int ghostNode4 = s.nextInt();
				if (progressIndices[mazeIndex][powerPillIndex] < NUM_GHOST_LOCATION_OPTIONS) {
					mazePowerPillGhostMap[mazeIndex][powerPillIndex][progressIndices[mazeIndex][powerPillIndex]++] = new int[] {
							ghostNode1, ghostNode2, ghostNode3, ghostNode4 };
				}
			}

			for (int m = 0; m < Constants.NUM_MAZES; m++) {
				for (int g = 0; g < NUM_POWER_PILLS; g++) {
					if (progressIndices[m][g] < NUM_GHOST_LOCATION_OPTIONS) {
						System.out.println("Not enough data for maze " + m + " and power pill " + g);
						System.exit(1);
					}
				}
			}
			s.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Could not load: " + filename);
			System.exit(1);
		}
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		// Do an eval in interleave domain, just don't count ghost score at end
		removePillsNearPowerPills = Parameters.parameters.booleanParameter("removePillsNearPowerPills");
		noPills = false;
		noPowerPills = false;
		endOnlyOnTimeLimit = false;
		exitLairEdible = false;
		// randomLairExit = false;
		lairExitDatabase = false;
		simultaneousLairExit = false;
		ghostsStartOutsideLair = false;
		endAfterGhostEatingChances = false;
		onlyOneLairExitAllowed = false;
		CommonConstants.pacmanStartingPowerPillIndex = -1;
		Pair<double[], double[]> full = super.oneEval(individual, num);
		// Need to remove ghost eating score
		full.t1[usedGhostScoreIndex] = 0;

		// Now do an eval where ghosts start edible
		removePillsNearPowerPills = false;
		noPills = true;
		noPowerPills = true;
		endOnlyOnTimeLimit = false;
		exitLairEdible = true;
		// randomLairExit = true;
		lairExitDatabase = true;
		simultaneousLairExit = true;
		ghostsStartOutsideLair = true;
		endAfterGhostEatingChances = true;
		onlyOneLairExitAllowed = true;

		// evaluations happen here so that pacman can start at each of the 4
		// power pill positions
		Pair<double[], double[]> ghostEating = null;
		for (CommonConstants.pacmanStartingPowerPillIndex = 0; CommonConstants.pacmanStartingPowerPillIndex < 4; CommonConstants.pacmanStartingPowerPillIndex++) {
			Pair<double[], double[]> trial = super.oneEval(individual, num);
			if (Parameters.parameters.booleanParameter("rawTimeScore")) {
				// Need to subtract time alive in edible task, since it is
				// always the max
				trial.t1[rawTimeScoreIndex] = 0;
			}
			if (ghostEating == null) {
				ghostEating = trial;
			} else { // Scores are added up
				ghostEating.t1 = ArrayUtil.zipAdd(ghostEating.t1, trial.t1);
				ghostEating.t2 = ArrayUtil.zipAdd(ghostEating.t2, trial.t2);
			}
		}

		double[] combinedScores = new double[full.t1.length];
		for (int i = 0; i < combinedScores.length; i++) {
			combinedScores[i] = full.t1[i] + ghostEating.t1[i];
		}
		double[] combinedOthers = new double[full.t2.length];
		for (int i = 0; i < combinedOthers.length; i++) {
			combinedOthers[i] = full.t2[i] + ghostEating.t2[i];
		}

		Pair<double[], double[]> combo = new Pair<double[], double[]>(combinedScores, combinedOthers);
		return combo;
	}
}
