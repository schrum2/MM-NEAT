package edu.utexas.cs.nn.experiment;

import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.MultiplePopulationGenerationalEA;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.SimpleBlueprintGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.mulambda.CooperativeCoevolutionMuLambda;
import edu.utexas.cs.nn.log.PlotLog;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.BlueprintTask;
import edu.utexas.cs.nn.tasks.MultiplePopulationTask;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class MultiplePopulationGenerationalEAExperiment implements Experiment {

	/**
	 * evolving subpopulations *
	 */
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
		if (MMNEAT.blueprints) {
			ArrayList<String> labels = new ArrayList<String>();
			labels.add("Missing Reference Replacements");
			labels.add("Ratio Missing Reference Replacements");
			labels.add("Child Blueprint Parent References");
			labels.add("Ratio Child Blueprint Parent References");
			labels.add("Unevaluated Parent Genotypes");
			labels.add("Ratio Unevaluated Parent Genotypes");
			labels.add("Unevaluated Child Genotypes");
			labels.add("Ratio Unevaluated Child Genotypes");
			labels.add("Successful Mutation Swaps From Parent To Child Genotype");
			labels.add("Ratio Successful Mutation Swaps From Parent To Child Genotype");
			labels.add("Full Parent Blueprints");
			labels.add("Ratio Full Parent Blueprints");
			labels.add("Full Child Blueprints");
			labels.add("Ratio Full Child Blueprints");
			blueprintLog = new PlotLog("Blueprints", labels);
		}

		this.ea = ea;
	}

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
			/**
			 * Initializing the blueprints is tricky because they need access to
			 * the other populations. So, do the others first, then do the
			 * blueprints
			 *
			 */
			if (MMNEAT.blueprints) {
				// Remove the blueprint example
				Genotype last = examples.remove(examples.size() - 1);
				// Initialize the other populations
				this.populations = ea.initialPopulations(examples);
				int mu = ((CooperativeCoevolutionMuLambda) ea).mu[examples.size()];
				// Initialize blueprint population, using other populations
				this.populations.add(PopulationUtil.initialPopulation(last, mu));
				// Put blueprint example back
				examples.add(last);
				assert(examples.size() == populations.size());
			} else {
				this.populations = ea.initialPopulations(examples);
			}
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
			// May need to fix blueprints whose member networks are no longer in
			// population
			if (MMNEAT.blueprints) {
				// The last population must have the blueprints, check each one
				int replacements = 0;
				int potentialReplacements = 0;
				for (Genotype g : populations.get(populations.size() - 1)) {
					SimpleBlueprintGenotype bp = (SimpleBlueprintGenotype) g;
					ArrayList<Long> ids = bp.getPhenotype();
					// Check the genotype ids pointed to by each blueprint
					for (int i = 0; i < ids.size(); i++) {
						// -1 for not found
						long oldId = ids.get(i);
						potentialReplacements++;
						if (PopulationUtil.indexOfGenotypeWithId(populations.get(i), oldId) == -1) {
							long newId = randomIdFromSubpop(i);
							bp.setValue(i, newId);
							replacements++;
							// Wrong type of logging
							// blueprintLog.log("Subpop:" + i + ":" + oldId + "
							// -> " + newId + ": in blueprint:" + bp.getId());
						}
					}
				}
				BlueprintTask bpt = (BlueprintTask) MMNEAT.task;
				CooperativeCoevolutionMuLambda coopEA = (CooperativeCoevolutionMuLambda) ea;
				/**
				 * Log: generation, number of post-gen blueprint fixes,
				 * percentage of such fixes, number of children blueprints that
				 * referenced a parent population, percentage of such
				 * references, number unevaluated child references, percentage
				 * of such references, number parent-to-child mutations that
				 * succeed, percent of such successes out of all attempts
				 *
				 */
				ArrayList<Double> logValues = new ArrayList<Double>(8);
				logValues.add((double) replacements);
				logValues.add((replacements * 1.0) / potentialReplacements);
				logValues.add((double) bpt.getNumberBlueprintParentReferences());
				logValues.add((bpt.getNumberBlueprintParentReferences() * 1.0) / bpt.getTotalBlueprintReferences());
				logValues.add((double) bpt.getPreviousNumberUnevaluatedReferences());
				logValues.add((bpt.getPreviousNumberUnevaluatedReferences() * 1.0)
						/ bpt.getPreviousTotalBlueprintReferences());
				logValues.add((double) bpt.getNumberUnevaluatedReferences());
				logValues.add((bpt.getNumberUnevaluatedReferences() * 1.0) / bpt.getTotalBlueprintReferences());
				logValues.add((double) coopEA.successfulOffspringSearches);
				logValues.add((coopEA.successfulOffspringSearches * 1.0) / coopEA.totalOffspringSearches);
				logValues.add((double) bpt.getNumberFullParentBlueprints());
				logValues.add((bpt.getNumberFullParentBlueprints() * 1.0) / coopEA.lambda[coopEA.lambda.length - 1]);
				logValues.add((double) bpt.getNumberFullChildBlueprints());
				logValues.add((bpt.getNumberFullChildBlueprints() * 1.0) / coopEA.lambda[coopEA.lambda.length - 1]);
				blueprintLog.log(gen, logValues);
			}
			// Each TWEANN sub-pop has its own archetype
			for (int i = 0; i < populations.size(); i++) {
				ArrayList<Genotype> pop = populations.get(i);
				if (pop.get(0) instanceof TWEANNGenotype) {
					// Have to copy each item individually because of Java's
					// Generics
					ArrayList<TWEANNGenotype> tweannPopulation = new ArrayList<TWEANNGenotype>(pop.size());
					for (Genotype p : pop) {
						tweannPopulation.add((TWEANNGenotype) p);
					}
					EvolutionaryHistory.cleanArchetype(i, tweannPopulation, gen);
				}
			}
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
	public long randomIdFromSubpop(int subpop) {
		ArrayList<Genotype> pop = populations.get(subpop);
		return pop.get(RandomNumbers.randomGenerator.nextInt(pop.size())).getId();
	}
}