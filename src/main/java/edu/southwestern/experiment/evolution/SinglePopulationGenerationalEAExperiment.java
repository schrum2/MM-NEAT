package edu.southwestern.experiment.evolution;

import edu.southwestern.data.SaveThread;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.ScoreHistory;
import edu.southwestern.evolution.SinglePopulationGenerationalEA;
import edu.southwestern.evolution.crossover.network.CombiningTWEANNCrossover;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Evolve a single population of genotypes one generation at a time.
 *
 * @author Jacob Schrum
 * @param <T> phenotype
 */
public abstract class SinglePopulationGenerationalEAExperiment<T> implements Experiment {

	// Current population
	protected ArrayList<Genotype<T>> population;
	// Evolutionary Algorithm to use
	protected SinglePopulationGenerationalEA<T> ea;
	// Where to save results
	public String saveDirectory;
	// Whether to save log data
	protected boolean writeOutput;
	// Whether each generation should be deleted once evolution has moved on to the next
	protected boolean deleteOld;
	// Whether initial population was loaded from disk
	private boolean loaded = false;
	// Whether file saving should occur in a parellel thread
	private boolean parallel;

	public SinglePopulationGenerationalEAExperiment() {
		// Dummy constructor used by Post Evolution Evaluation Experiment
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SinglePopulationGenerationalEAExperiment(SinglePopulationGenerationalEA<T> ea, Genotype<T> example,
			String lastSavedDir) {
		parallel = Parameters.parameters.booleanParameter("parallelSave");
		writeOutput = Parameters.parameters.booleanParameter("netio");
		deleteOld = Parameters.parameters.booleanParameter("cleanOldNetworks");
		boolean io = Parameters.parameters.booleanParameter("io");

		this.ea = ea;
		if (lastSavedDir == null || lastSavedDir.equals("")) {
			int popSeed = Parameters.parameters.integerParameter("initialPopulationSeed");
			if (popSeed != -1) {
				RandomNumbers.reset(popSeed);
			}
			this.population = ea.initialPopulation(example);
			EvolutionaryHistory.frozenPreferenceVsPolicyStatusUpdate(population, 0);
			RandomNumbers.reset();
		} else {
			loaded = this.load(lastSavedDir);
		}
		// Immediate changes that can be made to the initial population
		if (Parameters.parameters.booleanParameter("initMMD")) {
			// Perform MMD on each member, but maintain common innovation numbers
			assert population.get(0) instanceof TWEANNGenotype : "Cannot init MMD on genotype other than TWEANNGenotype";
			int multitaskModes = Parameters.parameters.integerParameter("multitaskModes");
			int startingModes = Parameters.parameters.integerParameter("startingModes");
			assert multitaskModes == 2 || startingModes == 2 : "Either the multitask modes or the starting modes has to equal 2";
			for (Genotype<T> t : population) {
				TWEANNGenotype tg = (TWEANNGenotype) t;
				if (tg.numModules == 1) { // Designed to move from one mode each to two modes each
					tg.moduleDuplication();
					if (multitaskModes == 2) {
						tg.standardMultitask = true;
					}
				}

				// Troubleshooting code for seeing the new networks
				// DrawingPanel p1 = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM,
				// TWEANN.NETWORK_VIEW_DIM, "Net 1");
				// tg.getPhenotype().draw(p1, true);
				// MiscUtil.waitForReadStringAndEnterKeyPress();
			}
		}

		/**
		 * The purpose behind this setting is to initialize a new population
		 * using a previously evolved population. Pairs of networks in the
		 * original population are combined to make single networks with two
		 * modules each, one corresponding to the first network, the other to
		 * the second.
		 * 
		 * I don't completely remember what the point of this really was, or if
		 * it worked.
		 */
		if (Parameters.parameters.booleanParameter("initCrossCombine")) {
			Genotype<T> g = population.get(0);
			assert g instanceof TWEANNGenotype : "Cannot init Combining Crossover on genotype other than TWEANNGenotype";
			if (((TWEANNGenotype) g).numModules == 1) {
				Collections.shuffle(population, RandomNumbers.randomGenerator);
				CombiningTWEANNCrossover combCross = new CombiningTWEANNCrossover();
				assert population.size()
						% 2 == 0 : "Need even number of individuals to properly pair off entire population";
				for (int i = 0; i < population.size(); i++) {
					// Trouble shooting code to analyze archetypes
					// ArrayList<TWEANNGenotype.NodeGene> before = new
					// ArrayList<TWEANNGenotype.NodeGene>();
					// before.addAll(EvolutionaryHistory.archetypes[0]);
					// System.out.println("Before: " + before);

					TWEANNGenotype tg1 = (TWEANNGenotype) population.get(i);
					i++;
					TWEANNGenotype tg2 = (TWEANNGenotype) population.get(i);
					// System.out.println("Combine nets " + tg1.getId() + " and
					// " + tg2.getId());
					population.set(i, (Genotype) combCross.crossover(tg1, tg2.copy()));
					// System.out.println("Nets " + tg1.getId() + " and " +
					// population.get(i).getId() + " created");

					// ArrayList<TWEANNGenotype.NodeGene> after =
					// EvolutionaryHistory.archetypes[0];
					// System.out.println("After: " + after);

					// More code for checking archetype correctness
					// System.out.println("Right Net Alignment");
					// TWEANNCrossover.printNodeAlignmentColumns(tg2.nodes, 0);
					// System.out.println("Combined Alignment");
					// TWEANNCrossover.printNodeAlignmentColumns(before, 0);

					// Troubleshooting code for seeing the new networks
					// DrawingPanel p1 = new
					// DrawingPanel(TWEANN.NETWORK_VIEW_DIM,
					// TWEANN.NETWORK_VIEW_DIM, "Net 1");
					// tg1.getPhenotype().draw(p1, true);
					// DrawingPanel p2 = new
					// DrawingPanel(TWEANN.NETWORK_VIEW_DIM,
					// TWEANN.NETWORK_VIEW_DIM, "Net 2");
					// p2.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
					// ((TWEANN) population.get(i).getPhenotype()).draw(p2,
					// true);
					// MiscUtil.waitForReadStringAndEnterKeyPress();
				}
				System.out.println("Population networks combined");
			} else {
				System.out.println("Population already had multimodal networks");
			}
		}

		/**
		 * Start with a previously evolved population of multitask networks and
		 * add preference neurons for each module so that the multitask networks
		 * become preference neuron networks, and can customize how they use
		 * their modules.
		 */
		if (Parameters.parameters.booleanParameter("initAddPreferenceNeurons")) {
			assert population.get(0) instanceof TWEANNGenotype : "Cannot initAddPreferenceNeurons on genotype other than TWEANNGenotype";
			if (((TWEANNGenotype) population.get(0)).standardMultitask) {
				System.out.println("Adding preference neurons to this population");
				for (Genotype<T> t : population) {
					TWEANNGenotype tg = (TWEANNGenotype) t;
					// DrawingPanel p1 = new
					// DrawingPanel(TWEANN.NETWORK_VIEW_DIM,
					// TWEANN.NETWORK_VIEW_DIM, "Before");
					// tg.getPhenotype().draw(p1, true);
					System.out.println("Adding preference neurons to network " + tg.getId());
					int modes = tg.numModules;
					for (int i = modes - 1; i >= 0; i--) {
						tg.insertPreferenceNeuron(i);
					}
					tg.standardMultitask = false;

					// Troubleshooting code for seeing the new networks
					// DrawingPanel p3 = new
					// DrawingPanel(TWEANN.NETWORK_VIEW_DIM,
					// TWEANN.NETWORK_VIEW_DIM, "After");
					// p3.setLocation(TWEANN.NETWORK_VIEW_DIM, 0);
					// tg.getPhenotype().draw(p3, true);
					// MiscUtil.waitForReadStringAndEnterKeyPress();
				}
			} else {
				System.out.println("Preference neurons already added to this population");
			}
		}

		saveDirectory = FileUtilities.getSaveDirectory();
		File dir = new File(saveDirectory);
		if ((writeOutput || io) && !dir.exists()) {
			dir.mkdir();
		}
		System.out.println("GenerationalEAExperiment: writeOutput = " + writeOutput);
	}

        @Override
	public void init() {
		// All work already done in constructor. Move here?
	}

        @Override
	public void run() {
		System.out.println("Evolving with " + ea + " to solve " + ea.getTask());
		if (writeOutput && !loaded) {
			save("initial");
			Parameters.parameters.saveParameters();
		}
		while (!shouldStop()) {
			System.out.println("Starting generation: " + ea.currentGeneration());
			population = ea.getNextGeneration(population);
			// ScoreHistory.clean();
			int gen = ea.currentGeneration();
			if (population.get(0) instanceof TWEANNGenotype) {
				ArrayList<TWEANNGenotype> tweannPopulation = new ArrayList<TWEANNGenotype>(population.size());
				for (Genotype<T> g : population) {
					tweannPopulation.add((TWEANNGenotype) g);
				}
				EvolutionaryHistory.cleanArchetype(0, tweannPopulation, gen);
			}
			if (writeOutput) {
				ScoreHistory.save(); // Only saves if actually being used
			}
			// If tracking score history, clean it up after each generation
			ScoreHistory.clean();
			if (writeOutput) {
				save("gen" + gen);
				Parameters.parameters.setInteger("lastSavedGeneration", gen);
				Parameters.parameters.saveParameters();
				if (deleteOld) {
					File lastDir = gen > 1 ? new File(saveDirectory + "/gen" + (gen - 1))
							: new File(saveDirectory + "/initial");
					if (lastDir.exists()) {
						FileUtilities.deleteDirectoryContents(lastDir);
						lastDir.delete();
					}
				}
			}
		}
		ea.close(population);
		System.out.println("Finished evolving");
	}

	public void save(String prefix) {
		save(prefix, saveDirectory, population, parallel);
	}

	/**
	 * Save all members of population as xml files in the dir saveDirectory.
	 *
	 * @param <T>
	 *            phenotype of genotype
	 * @param prefix
	 *            subdir in saveDirectory to save files (usually gen40 or
	 *            something similar)
	 * @param saveDirectory
	 *            directory where files are saved
	 * @param population
	 *            list of genotypes to save
	 * @param parallel
	 *            whether or not the save is executed with parallel threads
	 */
	public static <T> void save(String prefix, String saveDirectory, ArrayList<Genotype<T>> population, boolean parallel) {
		String experimentPrefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber");
		String fullSaveDir = saveDirectory + "/" + prefix;
		prefix = experimentPrefix + "_" + prefix + "_";

		new File(fullSaveDir).mkdir();
		Parameters.parameters.setString("lastSavedDirectory", fullSaveDir);
		System.out.println("Saving to \"" + fullSaveDir + "\" with prefix \"" + prefix + "\"");

		ExecutorService poolExecutor = null;
		ArrayList<Future<Boolean>> futures = null;
		ArrayList<SaveThread<Genotype<T>>> saves = new ArrayList<SaveThread<Genotype<T>>>(population.size());

		for (int i = 0; i < population.size(); i++) {
			String filename = fullSaveDir;
			if (!filename.equals("")) {
				filename = filename + "/";
			}
			filename += prefix + i + ".xml";
			// System.out.println("Saving " + population.get(i).getId());
			saves.add(new SaveThread<Genotype<T>>(population.get(i), filename));
		}

		if (parallel) {
			poolExecutor = Executors.newCachedThreadPool();
			futures = new ArrayList<Future<Boolean>>(population.size());
			for (int i = 0; i < population.size(); i++) {
				futures.add(poolExecutor.submit(saves.get(i)));
			}
		}

		for (int i = 0; i < saves.size(); i++) {
			try {
				Boolean result = parallel ? futures.get(i).get() : saves.get(i).call();
				if (!result) {
					System.out.println("Failure saving " + population.get(i));
					System.exit(1);
				}
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
				System.out.println("Failure saving " + population.get(i));
				System.exit(1);
			}
		}

		if (parallel) {
			poolExecutor.shutdown();
		}

		// Better to save archetype immediately after network saves so that
		// unexpected crash/restarts don't make archetype out of sync with
		// population.
		EvolutionaryHistory.saveArchetype(0);
	}

	/**
	 * Load population and return true on success
	 *
	 * @param directory
	 *            directory full of xml files of saved genotypes
	 * @return true if successful, false otherwise
	 */
	public final boolean load(String directory) {
		this.population = PopulationUtil.load(directory);
		return population != null;
	}
}
