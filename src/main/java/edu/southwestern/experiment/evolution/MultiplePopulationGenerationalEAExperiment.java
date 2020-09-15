package edu.southwestern.experiment.evolution;

import java.io.File;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.MultiplePopulationGenerationalEA;
import edu.southwestern.evolution.ScoreHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.log.PlotLog;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.MultiplePopulationTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MultiplePopulationGenerationalEAExperiment implements Experiment {

	/**
	 * evolving subpopulations *
	 */
	@SuppressWarnings("rawtypes") // Each population can be a different type
	protected ArrayList<ArrayList<Genotype>> populations = null;
	/**
	 * Evolutionary Algorithms supporting multiple populations *
	 */
	protected MultiplePopulationGenerationalEA ea;
	/**
	 * Where to save data *
	 */
	public String saveDirectory;
	/**
	 * Whether or not to log output data *
	 */
	protected boolean writeOutput;
	/**
	 * Whether or not to delete each previous population when new one is saved *
	 */
	protected boolean deleteOld;
	/**
	 * whether populations was loaded from files (as opposed to initialized from
	 * scratch) *
	 */
	private boolean loaded = false;
	/**
	 * should file IO be done in parallel threads *
	 */
	private final boolean parallel;
	/**
	 * Track information about blueprints if they are used *
	 */
	private PlotLog blueprintLog = null;

	public MultiplePopulationGenerationalEAExperiment(MultiplePopulationGenerationalEA ea) {
		System.out.println("Init MultiplePopulationGenerationalEAExperiment");

		parallel = Parameters.parameters.booleanParameter("parallelSave");
		writeOutput = Parameters.parameters.booleanParameter("netio");
		deleteOld = Parameters.parameters.booleanParameter("cleanOldNetworks");

		this.ea = ea;
	}

	@SuppressWarnings({ "rawtypes"}) // Raw types needed to allow more flexibility
	@Override
	public void init() {
		String lastSavedDir = Parameters.parameters.stringParameter("lastSavedDirectory");
		boolean io = Parameters.parameters.booleanParameter("io");
		ArrayList<Genotype> examples = MMNEAT.genotypeExamples;

		if (lastSavedDir == null || lastSavedDir.equals("")) {
			System.out.println("Create new populations");
			int popSeed = Parameters.parameters.integerParameter("initialPopulationSeed");
			if (popSeed != -1) {
				RandomNumbers.reset(popSeed);
			}
			this.populations = ea.initialPopulations(examples);
			RandomNumbers.reset();
		} else {
			int numPops = ((MultiplePopulationTask) ea.getTask()).numberOfPopulations();
			populations = PopulationUtil.loadSubPops(lastSavedDir, numPops);
			loaded = populations != null;
		}
		saveDirectory = FileUtilities.getSaveDirectory();
		File dir = new File(saveDirectory);
		if ((writeOutput || io) && !dir.exists()) {
			dir.mkdir();
		}
		System.out.println("GenerationalEAExperiment: writeOutput = " + writeOutput);
	}

	@SuppressWarnings("rawtypes") // each population can be a different type
	@Override
	public void run() {
		System.out.println("Evolving with " + ea + " to solve " + ea.getTask());
		if (writeOutput && !loaded) {
			save("initial");
			Parameters.parameters.saveParameters();
		}
		while (!shouldStop()) {
			System.out.println("Starting generation: " + ea.currentGeneration());
			populations = ea.getNextGeneration(populations);
			int gen = ea.currentGeneration();
			// Each TWEANN sub-pop has its own archetype
			for (int i = 0; i < populations.size(); i++) {
				ArrayList<Genotype> pop = populations.get(i);
				if (pop.get(0) instanceof TWEANNGenotype) {
					// Have to copy each item individually because of Java's Generics
					ArrayList<TWEANNGenotype> tweannPopulation = new ArrayList<TWEANNGenotype>(pop.size());
					for (Genotype p : pop) {
						tweannPopulation.add((TWEANNGenotype) p);
					}
					EvolutionaryHistory.cleanArchetype(i, tweannPopulation, gen);
				}
			}
			if (writeOutput) {
				ScoreHistory.save(); // Only saves if actually being used
			}
			// If tracking score history, clean it up after each generation
			ScoreHistory.clean();
			// Write output
			if (writeOutput) {
				save("gen" + gen);
				Parameters.parameters.setInteger("lastSavedGeneration", gen);
				Parameters.parameters.saveParameters();
				/**
				 * With multiple populations, there is a subdirectory for each
				 * type of network, and the files from each subdirectory need to
				 * be deleted before the subdirectories are deleted, and finally
				 * the top-level dir.
				 */
				if (deleteOld) {
					File lastDir = new File(saveDirectory + (gen > 1 ? "/gen" + (gen - 1) : "/initial"));
					File[] dirs = lastDir.listFiles();
					for (File dir : dirs) {
						FileUtilities.deleteDirectoryContents(dir);
						dir.delete();
					}
					lastDir.delete();
				}
			}
		}
		ea.close(populations);
		if (blueprintLog != null) {
			blueprintLog.close();
		}
		System.out.println("Finished evolving");
	}

	public void save(String prefix) {
		PopulationUtil.saveAllSubPops(prefix, saveDirectory, populations, parallel);
	}

	/**
	 * Used by co-evolution methods that use blueprints. Should only be used to
	 * get initial population of blueprints. It is assumed that populations is
	 * already filled with all subpopulations of genotypes except for the
	 * blueprint population, which will occupy the last slot. This method picks
	 * a random genotype from each population except the blueprint population,
	 * and puts the ids of these genotypes in an ArrayList that is returned. The
	 * ArrayList is a choice of one genotype from each subpopulation, and
	 * therefore represents a blueprint.
	 *
	 * pre: populations contains all subpopulations except the blueprint
	 * population
	 *
	 * @return random blueprint of genotype ids from each subpop except last.
	 */
	public ArrayList<Long> randomBlueprint() {
		ArrayList<Long> result = new ArrayList<Long>(populations.size());
		for (int i = 0; i < populations.size(); i++) {
			result.add(randomIdFromSubpop(i));
		}
		return result;
	}

	/**
	 * Get genotype id from a random member of subpopulation at index subpop in
	 * populations. Will only ever be the index of a parent.
	 *
	 * @param subpop
	 *            index in populations
	 * @return id of random genotype in that subpop
	 */
	@SuppressWarnings("rawtypes") // Each population can be a different type
	public long randomIdFromSubpop(int subpop) {
		ArrayList<Genotype> pop = populations.get(subpop);
		return pop.get(RandomNumbers.randomGenerator.nextInt(pop.size())).getId();
	}
}