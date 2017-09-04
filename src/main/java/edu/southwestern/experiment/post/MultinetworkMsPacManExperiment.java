package edu.southwestern.experiment.post;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.GroupTask;
import edu.southwestern.tasks.mspacman.CooperativeMultitaskSchemeMsPacManTask;
import edu.southwestern.tasks.mspacman.init.MsPacManInitialization;
import edu.southwestern.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.southwestern.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.tasks.mspacman.sensors.mediators.GhostTaskMediator;
import edu.southwestern.tasks.mspacman.sensors.mediators.PillTaskMediator;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.CombinatoricUtilities;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.random.RandomNumbers;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import wox.serial.Easy;

/**
 * Designed to take several pre-evolved populations and evaluate them (not
 * evolve)
 *
 * @author Jacob Schrum
 */
public final class MultinetworkMsPacManExperiment<T extends Network> implements Experiment {

	protected ArrayList<Genotype<T>>[] populations;
	protected MsPacManModeSelector ms;
	private final MsPacManControllerInputOutputMediator[] mediators;

	public MultinetworkMsPacManExperiment() throws NoSuchMethodException {
		this((MsPacManModeSelector) ClassCreation.createObject("pacmanMultitaskScheme"),
				new String[] { Parameters.parameters.stringParameter("multinetworkPopulation1"),
						Parameters.parameters.stringParameter("multinetworkPopulation2"),
						Parameters.parameters.stringParameter("multinetworkPopulation3"),
						Parameters.parameters.stringParameter("multinetworkPopulation4") },
				new String[] { Parameters.parameters.stringParameter("multinetworkScores1"),
						Parameters.parameters.stringParameter("multinetworkScores2"),
						Parameters.parameters.stringParameter("multinetworkScores3"),
						Parameters.parameters.stringParameter("multinetworkScores4") },
				new MsPacManControllerInputOutputMediator[] {
						(MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass1"),
						(MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass2"),
						(MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass3"),
						(MsPacManControllerInputOutputMediator) ClassCreation.createObject("pacManMediatorClass4") });
	}

	@SuppressWarnings("unchecked")
	public MultinetworkMsPacManExperiment(MsPacManModeSelector ms, String[] populationDirs, String[] scoreFiles,
			MsPacManControllerInputOutputMediator[] tempMediators) {
		this.ms = ms;
		int numPops = 0;
		for (int i = 0; i < populationDirs.length; i++) {
			if (!populationDirs[i].equals("")) {
				numPops++;
			}
		}

		boolean justOne = false;
		if (numPops == 0) {
			justOne = true;
			numPops = 2;
		}

		this.mediators = new MsPacManControllerInputOutputMediator[numPops];
		this.populations = new ArrayList[numPops];
		if (justOne) {
			mediators[0] = new GhostTaskMediator();
			mediators[1] = new PillTaskMediator();

			populations[0] = new ArrayList<Genotype<T>>(1);
			populations[0].add((Genotype<T>) Easy.load(Parameters.parameters.stringParameter("ghostEatingSubnetwork")));
			populations[1] = new ArrayList<Genotype<T>>(1);
			populations[1].add((Genotype<T>) Easy.load(Parameters.parameters.stringParameter("pillEatingSubnetwork")));
		} else {
			for (int i = 0; i < populations.length; i++) {
				mediators[i] = tempMediators[i];
				System.out.println("Loading population " + i);
				populations[i] = PopulationUtil.load(populationDirs[i]);
				if (scoreFiles != null && scoreFiles[i] != null && !scoreFiles[i].equals("")) {
					System.out.println("Loading scores " + i);
					NSGA2Score<T>[] scores = null;
					try {
						scores = PopulationUtil.loadScores(scoreFiles[i]);
					} catch (FileNotFoundException ex) {
						ex.printStackTrace();
						System.exit(1);
					}
					if (Parameters.parameters.booleanParameter("onlyWatchPareto")) {
						int topLayers = Parameters.parameters.integerParameter("layersToView");
						PopulationUtil.pruneDownToTopParetoLayers(populations[i], scores, topLayers);
					}
				}
			}
		}
		MsPacManInitialization.setupMsPacmanParameters();
	}

	public void init() {
		// All work already done in constructor ... should move that here?
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void run() {
		try {
			MMNEAT.directionalSafetyFunction = (VariableDirectionBlock) ClassCreation
					.createObject("directionalSafetyFunction");
		} catch (NoSuchMethodException ex) {
			System.out.println("No directional safety check");
		}
		RandomNumbers.reset();
		ArrayList<Integer> lengths = new ArrayList<Integer>(populations.length);
		for (int i = 0; i < populations.length; i++) {
			lengths.add(populations[i].size());
		}
		ArrayList<ArrayList<Integer>> combos = CombinatoricUtilities.getAllCombinations(lengths);
		CooperativeMultitaskSchemeMsPacManTask mpmTask = new CooperativeMultitaskSchemeMsPacManTask(mediators);
		for (int c = Parameters.parameters.integerParameter("multinetworkComboReached"); c < combos.size(); c++) {
			Parameters.parameters.setInteger("multinetworkComboReached", c);
			Parameters.parameters.saveParameters();

			ArrayList<Integer> combo = combos.get(c);
			Genotype<T>[] genotypes = new Genotype[combo.size()];
			// Create combination of networks
			long[] ids = new long[genotypes.length];
			for (int i = 0; i < genotypes.length; i++) {
				genotypes[i] = populations[i].get(combo.get(i));
				ids[i] = genotypes[i].getId();
			}
			DrawingPanel[] panels = null;
			if (CommonConstants.showNetworks) {
				panels = GroupTask.drawNetworks(genotypes);
			}

			// This code evaluates the first network; a simpel test
			// MONE.pacmanInputOutputMediator = mediators[0];
			// Score<T> next = mpmTask.task.evaluate(genotypes[0]);
			ArrayList<Score> result = mpmTask.evaluate(genotypes);
			Score<T> next = result.get(0);
			System.out.println("Scores: " + next);

			if (panels != null) {
				GroupTask.disposePanels(panels);
			}
			System.out.println("Eval:" + combo + Arrays.toString(ids) + "\n" + next
					+ "\n-------------------------------------------------");
		}
	}

	/*
	 * Never called
	 */
	public boolean shouldStop() {
		return true;
	}
}
