package edu.utexas.cs.nn.tasks.mspacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 * One pacman eval consists of two separate evals: First a pill task Second in
 * game where ghosts start edible, but pacman starts from power pills.
 *
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public class MsPacManPillsVsEdibleFromCornersMultitask<T extends Network> extends MsPacManTask<T> {

	public MsPacManPillsVsEdibleFromCornersMultitask() {
		Parameters.parameters.setBoolean("imprisonedWhileEdible", true);
		CommonConstants.imprisonedWhileEdible = true;
		noPowerPills = true;
		MsPacManOnlyPillScoreInFullVsEdibleFromCornersMultitask.loadMapPowerPillGhostMap(Parameters.parameters.stringParameter("mazePowerPillGhostMapping"));
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		removePillsNearPowerPills = Parameters.parameters.booleanParameter("removePillsNearPowerPills");
		noPills = false;
		endOnlyOnTimeLimit = false;
		exitLairEdible = false;
		lairExitDatabase = false;
		simultaneousLairExit = false;
		ghostsStartOutsideLair = false;
		endAfterGhostEatingChances = false;
		onlyOneLairExitAllowed = false;
		CommonConstants.pacmanStartingPowerPillIndex = -1;
		saveFilePrefix = "PillTask-";
		Pair<double[], double[]> full = super.oneEval(individual, num);
		// Need to remove ghost eating score
		full.t1[usedGhostScoreIndex] = 0;

		// Now do an eval where ghosts start edible
		removePillsNearPowerPills = false;
		noPills = true;
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
			// System.out.println("Power pill: " +
			// CommonConstants.pacmanStartingPowerPillIndex);
			saveFilePrefix = "GhostTask" + CommonConstants.pacmanStartingPowerPillIndex + "-";
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
