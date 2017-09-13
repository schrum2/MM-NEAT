package edu.southwestern.tasks.mspacman.data;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.agentcontroller.pacman.NNPacManController;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.PrefixFilter;
import edu.southwestern.util.random.RandomNumbers;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import pacman.game.Game;

/**
 *
 * @author Jacob
 */
public class TrainingCampManager {

	public static RecentPastQueue recentStates = null;
	public static int saveNumber = 0;
	public static String campsPath;
	private ArrayList<String> badCamps = new ArrayList<String>();
	private ArrayList<String> keeperCamps = new ArrayList<String>();
	private ArrayList<String> trainingCamps = new ArrayList<String>();
	private ArrayList<Integer> campPerformance = new ArrayList<Integer>();
	private int gen;
	private String genPath;
	private final boolean viewFinalCamps;
	private final double easyCampThreshold;
	private final double hardCampThreshold;
	private final int keeperCampLimit;

	public TrainingCampManager() {
		keeperCampLimit = Parameters.parameters.integerParameter("keeperCampLimit");
		easyCampThreshold = Parameters.parameters.doubleParameter("easyCampThreshold");
		hardCampThreshold = Parameters.parameters.doubleParameter("hardCampThreshold");
		viewFinalCamps = Parameters.parameters.booleanParameter("viewFinalCamps");
		int pastLength = Parameters.parameters.integerParameter("recentPastMemLength");
		if (pastLength > -1) {
			System.out.println("Remember a sliding window of the past " + pastLength + " steps");
			recentStates = new RecentPastQueue(pastLength);
			campsPath = FileUtilities.getSaveDirectory() + "/camps";
			File campDir = new File(campsPath);
			if (!campDir.exists()) {
				campDir.mkdir();
			}
			File badPath = new File(campsPath + "/bad");
			if (badPath.exists()) {
				loadCamps(badPath, badCamps, Integer.MAX_VALUE, "BadCamp", false);
			}
			File keeperPath = new File(campsPath + "/keeper");
			if (keeperPath.exists()) {
				loadCamps(keeperPath, keeperCamps, Integer.MAX_VALUE, "KeeperCamp", false);
			}
			prepareCamps(Parameters.parameters.integerParameter("lastSavedGeneration") - 1);
		}
		if (viewFinalCamps) {
			campsPath = FileUtilities.getSaveDirectory() + "/camps";
			prepareCamps(1);
		}

	}

	/**
	 * Load training camps from the specified generation (usually one before the
	 * current generation).
	 *
	 * @param gen
	 */
	private void prepareCamps(int gen) {
		if (gen >= 0) {
			assessPreviousCamps();
			int campLimit = Math.min((int) Math.floor(CommonConstants.campPercentOfTrials * CommonConstants.trials),
					CommonConstants.maxCampTrials);
			System.out.println("Number of camps: " + campLimit);
			if (campLimit > 0) {
				trainingCamps = new ArrayList<String>(campLimit);
				campPerformance = new ArrayList<Integer>(campLimit);
				// Keeper camps take precedence
				if (!viewFinalCamps) {
					for (int i = 0; i < keeperCamps.size(); i++) {
						System.out.println("Add keeper camp: " + i);
						trainingCamps.add(keeperCamps.get(i));
						campPerformance.add(0); // Start with 0 victories
					}
					System.out.println(trainingCamps.size() + " Keeper Camps Added");
					campLimit -= trainingCamps.size();
				}

				// Load a different portion of the different types of camps
				int numDeathTrials = (int) Math
						.ceil(Parameters.parameters.doubleParameter("percentDeathVsPPCamps") * campLimit);
				int numPPTrials = campLimit - numDeathTrials;

				genPath = campsPath + "/" + (viewFinalCamps ? "final" : gen);
				File genDir = new File(genPath);
				if (genDir.exists()) {
					loadCamps(genDir, trainingCamps, numDeathTrials, "PreDeathState", true);
					loadCamps(genDir, trainingCamps, numPPTrials, "PrePowerPillState", true);
				}
			}
		}
	}

	private void loadCamps(File genDir, ArrayList<String> campContainer, int limit, String prefix, boolean mainCamps) {
		if (mainCamps) {
			System.out.println("Up to " + limit + " camps with prefix '" + prefix + "'");
		} else {
			System.out.println("Add all camps with prefix '" + prefix + "'");
		}
		if (limit > 0) {
			List<File> camps = Arrays.asList(genDir.listFiles(new PrefixFilter(prefix)));
			Collections.shuffle(camps, RandomNumbers.randomGenerator);
			int added = 0;
			for (File c : camps) {
				if (mainCamps && added >= limit) {
					break;
				}
				try {
					String encoded = FileUtilities.simpleReadFile(c);
					if (campContainer.contains(encoded)) {
						System.out.println("Skip duplicate camp: " + c.getName());
					} else if (mainCamps && badCamps.contains(encoded)) {
						System.out.println("Skip known bad camp: " + c.getName());
					} else {
						System.out.println("Use camp: " + c.getName());
						campContainer.add(encoded);
						if (mainCamps) {
							campPerformance.add(0); // Start with 0 victories
						}
						added++;
					}
				} catch (FileNotFoundException ex) {
					System.out.println("Could not load camp: " + c);
					System.exit(1);
				}
			}
		}
	}

	public void preEval() {
		gen = 0;
		genPath = null;
		boolean finalEval = MMNEAT.experiment.shouldStop() && !CommonConstants.watch;
		if (recentStates != null) {
			recentStates.empty();
		}
		if (recentStates != null || viewFinalCamps) {
			gen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
			genPath = campsPath + "/" + (finalEval ? "final" : gen);
			File genDir = new File(genPath);
			if (!genDir.exists()) { // Starting new generation
				genDir.mkdir();
				saveNumber = 0;
				// Prepare the camps for this gen
				prepareCamps(gen - 1);
			}
			// Delete old gens
			if (!finalEval) {
				genPath = campsPath + "/" + (gen - 2);
				genDir = new File(genPath);
				if (genDir.exists()) { // Starting new generation
					FileUtilities.deleteDirectoryContents(genDir);
					genDir.delete();
				}
				genPath = campsPath + "/" + gen;
				// Clear old keepers and save current
				String keeperPath = campsPath + "/keeper";
				File keeperDir = new File(keeperPath);
				if (keeperDir.exists()) {
					FileUtilities.deleteDirectoryContents(keeperDir);
				} else {
					keeperDir.mkdir();
				}
				for (int i = 0; i < keeperCamps.size(); i++) {
					FileUtilities.simpleFileWrite(keeperPath + "/KeeperCamp-" + keeperCamps.size() + ".txt",
							keeperCamps.get(i));
				}
			}
		}
	}

	/**
	 * Return number of camp being used, or -1 otherwise
	 *
	 * @param game
	 * @param num
	 * @return
	 */
	public int campSetup(GameFacade game, int num) {
		if (num < trainingCamps.size()) {
			if (CommonConstants.watch) {
				System.out.println("Eval in Training Camp: " + num);
			}
			game.newG.setGameState(trainingCamps.get(num));
			return num;
		}
		return -1;
	}

	public void postEval(GameFacade game, int campNum, int startingLevel) {
		// Means pacman died at end of eval (not time limit or cleared all
		// levels)
		int remainingLives = game.getPacmanNumberOfLivesRemaining();
		if (remainingLives == 0) {
			NNPacManController.timesDied++;
			if (recentStates != null && recentStates.memoryFull()
					&& RandomNumbers.randomGenerator.nextDouble() < CommonConstants.percentDeathCampsToSave) {

				Game shortlyBeforeDeath = recentStates.beginningOfMemory();
				FileUtilities.simpleFileWrite(genPath + "/PreDeathState-" + gen + "-" + (saveNumber++) + ".txt",
						shortlyBeforeDeath.getGameState());
			}
		}
		// See if we started with a camp, and whether it was overcome
		if (campNum != -1) {
			// See if agent beat the camp
			if (remainingLives > 0 || game.getCurrentLevel() > startingLevel) {
				campPerformance.set(campNum, campPerformance.get(campNum) + 1);
			}
		}
	}

	public static void ghostEatingCamp(int time) {
		// Make training camp out of this situation
		if (TrainingCampManager.recentStates != null && TrainingCampManager.recentStates.memoryFull()
				&& RandomNumbers.randomGenerator.nextDouble() < CommonConstants.percentPowerPillCampsToSave) {
			Game shortlyBeforeEatingPowerPill = TrainingCampManager.recentStates.beginningOfMemory();
			int gen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
			String genPath = FileUtilities.getSaveDirectory() + "/camps/" + gen;
			FileUtilities.simpleFileWrite(genPath + "/PrePowerPillState-" + gen + "-" + time + ".txt",
					shortlyBeforeEatingPowerPill.getGameState());
		}
	}

	/**
	 * Look at camps from previous generation and decide if they are too hard
	 * (impossible) or too easy.
	 */
	private void assessPreviousCamps() {
		if (MMNEAT.ea == null) {
			return;
		}
		// trainingCamps and campPerformance should always sync up with the
		// initial spots of keeperCamps
		System.out.println("Camp Success Rates:");
		for (int i = campPerformance.size() - 1; i >= 0; i--) {
			int performance = campPerformance.get(i);
			int maxEvals = ((GenerationalEA) MMNEAT.ea).evaluationsPerGeneration();
			String camp = trainingCamps.get(i);
			System.out.println("Camp " + i + ": " + performance + "/" + maxEvals + "("
					+ ((100.0 * performance) / maxEvals) + "%)");
			if (performance == 0) {
				// No one could beat it, so it is just to hard, and may even be
				// impossible to beat
				System.out.println("Remember bad camp: " + i);
				rememberToAvoidCamp(camp, i);
			} else if (performance > maxEvals * easyCampThreshold) {
				// Too many beat it, so it is too easy
				System.out.println("Remember easy camp: " + i);
				rememberToAvoidCamp(camp, i);
			} else if (performance < maxEvals * hardCampThreshold) {
				// Not enough beat it, so keep it
				if (keeperCamps.size() >= keeperCampLimit) {
					System.out.println("Keeper camps full: " + i);
				} else if (keeperCamps.contains(camp)) {
					System.out.println("Already have hard camp: " + i);
				} else {
					System.out.println("Remember hard camp: " + i);
					keeperCamps.add(camp);
				}
			} else if (performance >= maxEvals * hardCampThreshold) {
				if (keeperCamps.size() > i) {
					System.out.println("Camp no longer a keeper: " + i);
					keeperCamps.remove(i);
				}
			}

		}
	}

	private void rememberToAvoidCamp(String camp, int index) {
		badCamps.add(camp);
		if (keeperCamps.size() > index) {
			keeperCamps.remove(index);
		}
		String badPath = campsPath + "/bad";
		File badDir = new File(badPath);
		if (!badDir.exists()) {
			badDir.mkdir();
		}
		FileUtilities.simpleFileWrite(badPath + "/BadCamp-" + badCamps.size() + ".txt", camp);
	}
}
