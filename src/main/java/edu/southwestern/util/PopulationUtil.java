package edu.southwestern.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.data.SaveThread;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.CPPNOrBlockVectorGenotype;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.CombinedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNAndSubstrateArchitectureGenotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNforDL4JGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNPlusParametersGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.evolution.mutation.tweann.ActivationFunctionRandomReplacement;
import edu.southwestern.evolution.mutation.tweann.CauchyDeltaCodeMutation;
import edu.southwestern.evolution.mutation.tweann.WeightRandomReplacement;
import edu.southwestern.evolution.nsga2.NSGA2;
import edu.southwestern.evolution.nsga2.NSGA2Score;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Better;
import edu.southwestern.scores.Score;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.SERFilter;
import edu.southwestern.util.file.Serialization;
import edu.southwestern.util.file.XMLFilter;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Several utility classes dealing with the creation
 * and management of populations.
 * 
 * @author Jacob Schrum
 */
public class PopulationUtil {

	/**
	 * Assuming a lineage log was previously saved, this method loads that lineage
	 * based on directories and files specified in command-line parameters.
	 * 
	 * @return generation # generations logged in file
	 * @throws FileNotFoundException No lineage file found
	 */
	public static int loadLineage() throws FileNotFoundException {
		String base = Parameters.parameters.stringParameter("base");
		String log =  Parameters.parameters.stringParameter("log");
		int runNumber = Parameters.parameters.integerParameter("runNumber");
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		//String loadFrom = Parameters.parameters.stringParameter("loadFrom");
		String originalPrefix = base + "/" + saveTo + runNumber + "/" + log + runNumber + "_";
		return loadLineage(originalPrefix + "Lineage_log.txt");
	}

	/**
	 * Load offspring's lineage
	 * 
	 * @param filename name of lineage log file
	 * @return generation # generations logged in file
	 * @throws FileNotFoundException if lineage file cannot be found 
	 */
	public static int loadLineage(String filename) throws FileNotFoundException {
		System.out.println("Load lineage from: " + filename);
		Scanner s = new Scanner(new File(filename));
		int generation = 0;
		while (s.hasNextLine()) {
			String next = s.nextLine();
			if (next.startsWith("--")) {
				generation++;
			} else {
				Scanner pattern = new Scanner(next);
				long parentId1 = pattern.nextLong();
				long parentId2 = -1;
				String symbol = pattern.next();
				if (symbol.equals("X")) {
					parentId2 = pattern.nextLong();
					symbol = pattern.next();
				}
				if (symbol.equals("->")) {
					long offspringId = pattern.nextLong();
					Offspring.addOffspring(new Offspring(offspringId, parentId1, parentId2, generation));
				} else {
					System.out.println("WTF: " + symbol);
					System.out.println("Format error");
					System.exit(1);
				}
				pattern.close();
			}
		}
		s.close();
		return generation;
	}

	// Does this method work? Looks like it was added by Lauren at some point,
	// perhaps to explore the lineage in Picbreeder.
	public static long loadID() throws FileNotFoundException { 
		String base = Parameters.parameters.stringParameter("base");
		String log =  Parameters.parameters.stringParameter("log");
		int runNumber = Parameters.parameters.integerParameter("runNumber");//this one is being a butt. Isn't being set in the batch file and so can't be found from parameters
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		//String loadFrom = Parameters.parameters.stringParameter("loadFrom");
		String prefix = base + "/" + saveTo + runNumber + "/" + log + runNumber + "_";
		String originalPrefix = base + "/" + saveTo + runNumber + "/" + log + runNumber + "_";
		System.out.println("Prefix: " + prefix);
		return loadID(originalPrefix + "Lineage_log.txt");
	}

	// Does this method work? Looks like it was added by Lauren at some point,
	// perhaps to explore the lineage in Picbreeder.
	public static long loadID(String filename) throws FileNotFoundException {
		Scanner s = new Scanner(new File(filename));
		int generation = 0;
		long offspringId = -1;
		while (s.hasNextLine()) {
			String next = s.nextLine();
			if (next.startsWith("--")) {
				generation++;
			} else {
				Scanner pattern = new Scanner(next);
				long parentId1 = pattern.nextLong();
				long parentId2 = -1;
				String symbol = pattern.next();
				if (symbol.equals("X")) {
					parentId2 = pattern.nextLong();
					symbol = pattern.next();
				}
				if (symbol.equals("->")) {
					offspringId = pattern.nextLong();
					Offspring.addOffspring(new Offspring(offspringId, parentId1, parentId2, generation));
				} else {
					System.out.println("WTF: " + symbol);
					System.out.println("Format error");
					System.exit(1);
				}
				pattern.close();
			}
		}
		s.close();
		return offspringId;
	}

	/**
	 * Given the best scoring individuals in each objective from the
	 * current generation, save those genotypes to file with naming
	 * conventions that make them easy to load later. Champions from the
	 * previous generation may need to be deleted first.
	 * 
	 * Note: Methods seems to only be used by SelectiveBreedingEA
	 * 
	 * @param <T> Phenotype associated with genotype
	 * @param bestScores Score entities that contain the best genotypes in each objective
	 *                   (one objective champion per ArrayList entry)
	 */
	public static <T> void saveCurrentGen(ArrayList<Score<T>> bestScores) {
		int currentGen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
		String filePrefix = "gen" + currentGen + "_";
		// Save best in each objective
		String bestDir = FileUtilities.getSaveDirectory() + "/bestObjectives";
		File dir = new File(bestDir);
		// Delete old contents/team
		if (dir.exists() && !Parameters.parameters.booleanParameter("saveAllChampions")) {
			FileUtilities.deleteDirectoryContents(dir);
		} else {
			dir.mkdir();
		}
		// save all of the best objectives
		for (int j = 0; j < bestScores.size(); j++) {
			Serialization.save(bestScores.get(j), bestDir + "/" + filePrefix + "keptGenotypesIn" + j);
			FileUtilities.simpleFileWrite(bestDir + "/" + filePrefix + "genotypes" + j + ".txt", bestScores.get(j).individual.toString());
		}
	}

	/**
	 * This method seems very similar to the saveCurrentGen method above, but
	 * is used in the LonerTask. Can they be combined somehow?
	 * 
	 * @param <T>
	 * @param bestObjectives Array of best objective scores in each objective
	 * @param bestGenotypes Genotypes corresponding to those scores
	 * @param bestScores Score entities corresponding to the genotypes (redundant?)
	 */
	public static <T> void saveBestOfCurrentGen(double[] bestObjectives, Genotype<T>[] bestGenotypes, Score<T>[] bestScores) {
		int currentGen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
		String filePrefix = "gen" + currentGen + "_";
		// Save best in each objective
		String bestDir = FileUtilities.getSaveDirectory() + "/bestObjectives";
		File dir = new File(bestDir);
		// Delete old contents/team
		if (dir.exists() && !Parameters.parameters.booleanParameter("saveAllChampions")) {
			FileUtilities.deleteDirectoryContents(dir);
		} else {
			dir.mkdir();
		}
		// save all of the best objectives
		for (int j = 0; j < bestObjectives.length; j++) {
			Serialization.save(bestGenotypes[j], bestDir + "/" + filePrefix + "bestIn" + j);
			FileUtilities.simpleFileWrite(bestDir + "/" + filePrefix + "score" + j + ".txt", bestScores[j].toString());
		}
	}
	/**
	 * Generate initial parent population
	 * 
	 * @param <T> Type of phenotype evolved
	 * @param example example genotype used to derive initial population
	 * @param size Population size
	 * @return List of genotypes for initial population
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> ArrayList<Genotype<T>> initialPopulation(Genotype<T> example, int size) {//TODO add randomization code here
		ArrayList<Genotype<T>> parents = new ArrayList<Genotype<T>>(size);
		if (MMNEAT.seedExample) { // Seed whole population with particular starting genotype
			WeightRandomReplacement wrr = new WeightRandomReplacement();
			for (int i = 0; i < size; i++) {
				// Exact copies of seed network
				Genotype<T> temp = example.copy();
				if(Parameters.parameters.booleanParameter("randomizeSeedWeights")){
					wrr.mutate((Genotype<TWEANN>) temp);
				}
				parents.add(temp.copy());
			}
		} else { // Random population
			for (int i = 0; i < size; i++) {
				parents.add(example.newInstance());
			}
			// If activation functions can mutate, then they 
			// should start randomized. 
			if(CommonConstants.netChangeActivationRate > 0) {
				ActivationFunctionRandomReplacement afrr = new ActivationFunctionRandomReplacement();
				if(parents.get(0) instanceof CombinedGenotype) {
					// If a combined genotype, assume the first of the pair is a network
					for (int i = 0; i < size; i++) {
						afrr.mutate((Genotype<TWEANN>) ((Pair) parents.get(i)).t1);
					}	
				} else if (parents.get(0) instanceof TWEANNPlusParametersGenotype) {
					// If a combined genotype, assume the first of the pair is a network
					for (int i = 0; i < size; i++) {
						afrr.mutate(((TWEANNPlusParametersGenotype) parents.get(i)).getTWEANNGenotype());
					}	
				} else if(parents.get(0) instanceof TWEANNGenotype) {
					// TWEANNGenotype is standard network genotype
					for (int i = 0; i < size; i++) {
						afrr.mutate((Genotype<TWEANN>) parents.get(i));
					}	
				} else if(parents.get(0) instanceof CPPNOrDirectToGANGenotype) {
					// TWEANNGenotype is the first form of this genotype
					for (int i = 0; i < size; i++) {
						afrr.mutate((Genotype<TWEANN>) ((CPPNOrDirectToGANGenotype) parents.get(i)).getCurrentGenotype());
					}	
				} else if(parents.get(0) instanceof CPPNOrBlockVectorGenotype) {
					// TWEANNGenotype is the first form of this genotype
					for (int i = 0; i < size; i++) {
						afrr.mutate((Genotype<TWEANN>) ((CPPNOrBlockVectorGenotype) parents.get(i)).getCurrentGenotype());
					}	
				} else if(parents.get(0) instanceof HyperNEATCPPNforDL4JGenotype) {
					// Mutate the CPPN within the HyperNEATCPPNforDL4JGenotype
					for (int i = 0; i < size; i++) {
						afrr.mutate(((HyperNEATCPPNforDL4JGenotype) parents.get(i)).getCPPN());
					}	
				} else {
					throw new IllegalArgumentException("Cannot change activation function of genotype that has no network: " + parents.get(0).getClass().getName());
				}
			}
		}
		return parents;
	}    

	/**
	 * Given a whole population of scores, get the Pareto front and use them as
	 * exemplars to create a delta-coded population.
	 * 
	 * @param <T> phenotype
	 * @param populationScores
	 *            population with scores
	 * @return new soft restart population
	 */
	public static <T> ArrayList<Genotype<T>> getBestAndDeltaCode(ArrayList<Score<T>> populationScores) {
		ArrayList<Genotype<T>> front = NSGA2.staticSelection(populationScores.size(), NSGA2.staticNSGA2Scores(populationScores));
		return deltaCodePopulation(populationScores.size(), front);
	}

	/**
	 * Take some example genotypes (e.g. a Pareto front) and create a whole
	 * population based off of them by delta coding their network weights.
	 * 
	 * @param <T> phenotype
	 * @param size
	 *            of population to be
	 * @param exemplars
	 *            starting point of new population
	 * @return the new population
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<Genotype<T>> deltaCodePopulation(int size, ArrayList<Genotype<T>> exemplars) {
		CauchyDeltaCodeMutation cauchy = new CauchyDeltaCodeMutation();
		ArrayList<Genotype<T>> newPop = new ArrayList<Genotype<T>>(size);
		assert exemplars.get(0) instanceof TWEANNGenotype : "Cannot delta-code non-TWEANN genotype";
		for (int i = 0; i < size; i++) {
			// Keep cycling through the exemplars
			Genotype<T> exemplar = exemplars.get(i % exemplars.size()).copy();
			cauchy.mutate((Genotype<TWEANN>) exemplar);
			newPop.add(exemplar);
		}
		return newPop;
	}

	/**
	 * Given parent scores and a means of comparing individuals, use elitist
	 * tournament selection to choose individuals and/or parents for creating an
	 * offspring population.
	 *
	 * @param numChildren
	 *            children to generate
	 * @param parentScores
	 *            scores of parents, plus genotypes
	 * @param judge
	 *            means of comparing two parents
	 * @param mating
	 *            whether to mate or not
	 * @param crossoverRate
	 *            crossover rate if mating
	 * @return population of children
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList<Genotype> childrenFromTournamentSelection(int numChildren, ArrayList<Score> parentScores, Better<Score> judge, boolean mating, double crossoverRate) {
		ArrayList<Genotype> offspring = new ArrayList<Genotype>(numChildren);

		for (int i = 0; i < numChildren; i++) {
			int e1 = RandomNumbers.randomGenerator.nextInt(parentScores.size());
			int e2 = RandomNumbers.randomGenerator.nextInt(parentScores.size());

			Genotype source = judge.better(parentScores.get(e1), parentScores.get(e2)).individual;
			long parentId1 = source.getId();
			long parentId2 = -1;
			Genotype e = source.copy();

			if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
				e1 = RandomNumbers.randomGenerator.nextInt(parentScores.size());
				e2 = RandomNumbers.randomGenerator.nextInt(parentScores.size());

				Genotype otherSource = judge.better(parentScores.get(e1), parentScores.get(e2)).individual;
				parentId2 = otherSource.getId();
				Genotype otherOffspring;

				Genotype other = otherSource.copy();
				otherOffspring = e.crossover(other);
				i++;
				/*
				 * The offspring e will be added no matter what. Because i is
				 * increased and then checked, otherOffspring will NOT always be
				 * added.
				 */
				if (i < numChildren) {
					// System.out.println(i + ":Mutate Child");
					otherOffspring.mutate();
					offspring.add(otherOffspring);
					EvolutionaryHistory.logLineageData(parentId1,parentId2,otherOffspring);
				}
			}

			e.mutate();
			offspring.add(e);
			if (parentId2 == -1) {
				EvolutionaryHistory.logLineageData(parentId1,e);
			} else {
				EvolutionaryHistory.logLineageData(parentId1,parentId2,e);
			}
		}
		return offspring;
	}

	/**
	 * Modifies population sent in so it contains only members of Pareto front
	 *
	 * @param <T>
	 *            Type of phenotype
	 * @param population
	 *            full population to be reduced to Pareto front
	 * @param scores
	 *            loaded scores corresponding to individuals in population
	 */
	public static <T> void pruneDownToParetoFront(ArrayList<Genotype<T>> population, NSGA2Score<T>[] scores) {
		pruneDownToTopParetoLayers(population, scores, 1);
	}

	/**
	 * Modifies population so it contains only top Pareto layers
	 *
	 * @param <T>
	 *            Phenotype
	 * @param population
	 *            genotypes to prune
	 * @param scores
	 *            scores corresponding to genotypes
	 * @param layers
	 *            How many layers to keep
	 */
	public static <T> void pruneDownToTopParetoLayers(ArrayList<Genotype<T>> population, NSGA2Score<T>[] scores, int layers) {
		assert layers > 0;
		assert scores.length > 0;
		ArrayList<ArrayList<NSGA2Score<T>>> fronts = NSGA2.getParetoLayers(scores);		
		assert fronts.size() > 0;
		// Reduce population to only contain top Pareto layers
		Iterator<Genotype<T>> itr = population.iterator();
		System.out.println("Reducing to top " + layers + " Pareto layers");
		while (itr.hasNext()) {
			Genotype<T> g = itr.next();
			boolean found = false;
			for (int i = 0; !found && i < layers; i++) {
				ArrayList<NSGA2Score<T>> front = fronts.get(i);
				for (NSGA2Score<T> s : front) {
					if (s.individual.getId() == g.getId()) {
						found = true;
						System.out.println(s.individual.getId() + ":" + Arrays.toString(s.scores) + " in layer " + i);
						break;
					}
				}
			}
			if (!found) {
				itr.remove();
				assert population.size() > 0 : Arrays.stream(scores).map( s -> Arrays.toString(s.scores)).collect(Collectors.toList());
			}
		}
	}

	/**
	 * Load all genotypes that are xml files in the given directory
	 *
	 * @param <T>
	 *            Phenotype
	 * @param directory
	 *            directory to load from
	 * @return loaded population of genotypes
	 */
	public static <T> ArrayList<Genotype<T>> load(String directory) {
		System.out.println("Attempting to load from: " + directory);

		FilenameFilter filter = (Parameters.parameters.booleanParameter("useWoxSerialization")) ? new XMLFilter() : new SERFilter();

		ArrayList<Genotype<T>> population = new ArrayList<Genotype<T>>(Parameters.parameters.integerParameter("mu"));

		File dir = new File(directory);
		String[] children = dir.list(filter);
		if (!dir.exists() || children == null) {
			System.err.println("Can't load population, folder '" + directory + "' does not exist");
			System.exit(1);
		} else {
			for (int i = 0; i < children.length; i++) {
				String file = directory + "/" + children[i];
				Genotype<T> individual = extractGenotype(file);
				population.add(individual);
			}
		}
		return population;
	}

	@SuppressWarnings("unchecked")
	public static <T> Genotype<T> extractGenotype(String file) {
		System.out.print("Load File: \"" + file + "\"");
		Object loaded = Serialization.load(file);
		Genotype<T> individual = null;
		if (loaded instanceof Genotype) {
			individual = (Genotype<T>) loaded;
			System.out.println(", ID = " + individual.getId());
			assert !(loaded instanceof HyperNEATCPPNAndSubstrateArchitectureGenotype) || ((HyperNEATCPPNAndSubstrateArchitectureGenotype) individual).allSubstrateConnectivity.get(0).sourceSubstrateName != null;
		}
		return individual;
	}

	/**
	 * Loads score information from the score file pertaining to a single
	 * generation. Only works for score files saved by single-population
	 * experiments.
	 *
	 * @param <T>
	 *            phenotype of saved individuals: irrelevant because scores
	 *            contain anonymous dummy individuals
	 * @param generation
	 *            generation to load scores for
	 * @return Array of NSGA2Scores for given generation
	 * @throws FileNotFoundException
	 *             if score file does not exist
	 */
	public static <T> NSGA2Score<T>[] loadScores(int generation) throws FileNotFoundException {
		String base = Parameters.parameters.stringParameter("base");
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		int run = Parameters.parameters.integerParameter("runNumber");
		String log = Parameters.parameters.stringParameter("log");
		String filePrefix = base + "/" + saveTo + run + "/" + log + run + "_";
		String infix = "parents_gen";
		String filename = filePrefix + infix + generation + ".txt";
		return loadScores(filename);
	}

	/**
	 * Same as above, but for coevolution
	 *
	 * @param generation
	 *            generation to load from
	 * @param pop
	 *            subpopulation index
	 * @return scores of subpop in designated generation
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	public static NSGA2Score[] loadSubPopScores(int generation, int pop) throws FileNotFoundException {
		String base = Parameters.parameters.stringParameter("base");
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		int run = Parameters.parameters.integerParameter("runNumber");
		String log = Parameters.parameters.stringParameter("log");
		String filePrefix = base + "/" + saveTo + run + "/" + log + run + "_";
		String infix = "pop" + pop + "parents_gen";
		String filename = filePrefix + infix + generation + ".txt";
		return loadScores(filename);
	}

	/**
	 * Loads scores from a specific filename and creates score entries with
	 * dummy individuals to return.
	 *
	 * @param <T>
	 *            phenotype: irrelevant since anonymous dummy individuals are used
	 * @param filename
	 *            file to load scores from
	 * @return array of scores
	 * @throws FileNotFoundException
	 *             if filename does not exist
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> NSGA2Score<T>[] loadScores(String filename) throws FileNotFoundException {
		Scanner s = new Scanner(new File(filename));
		NSGA2Score<T>[] populationScores = new NSGA2Score[Parameters.parameters.integerParameter("mu")];
		int i = 0;
		while (s.hasNextLine()) {
			Scanner line = new Scanner(s.nextLine());
			@SuppressWarnings("unused")
			int withinGen = line.nextInt(); // Not used, but must be read from file
			long offspringId = line.nextLong();
			ArrayList<Double> scores = new ArrayList<Double>();
			while (line.hasNext()) {
				double x = line.nextDouble();
				// System.out.print(x + "\t");
				scores.add(x);
			}
			// System.out.println();
			double[] scoreArray = new double[scores.size()];
			for (int j = 0; j < scoreArray.length; j++) {
				scoreArray[j] = scores.get(j);
			}
			Genotype<T> anonymous = anonymousIdIndividual(offspringId);
			populationScores[i++] = new NSGA2Score(anonymous, scoreArray, null, null);
			assert populationScores[i - 1] != null : "Null Score! " + i;
			line.close();
		}
		s.close();
		return populationScores;
	}

	/**
	 * Used when creating a score instance where only the score matters. Score
	 * instances normally contain a copy of the genotype as well, but when
	 * loading scores from a file, the genotype would take an extra effort to
	 * load. All that really matters is the genotype id, so that the score can be
	 * associated with the right genotype, even though that genotype is
	 * contained within this particular score instance. This method creates an
	 * instance of an anonymous Genotype that only stores a genotype id. Any
	 * attempt to use any other methods defined by Genotype will lead to
	 * exceptions.
	 *
	 * @param <T>
	 *            phenotype of genotype: irrelevant in dummy individual.
	 * @param offspringId
	 *            genotype id of individual
	 * @return Genotype where only the id is accessible.
	 */
	public static <T> Genotype<T> anonymousIdIndividual(final long offspringId) {
		return new Genotype<T>() {

			@Override
			public Genotype<T> copy() {
				throw new UnsupportedOperationException("Not supported in dummy genotype.");
			}

			@Override
			public void mutate() {
				throw new UnsupportedOperationException("Not supported in dummy genotype.");
			}

			@Override
			public Genotype<T> crossover(Genotype<T> g) {
				throw new UnsupportedOperationException("Not supported in dummy genotype.");
			}

			@Override
			public T getPhenotype() {
				throw new UnsupportedOperationException("Not supported in dummy genotype.");
			}

			@Override
			public Genotype<T> newInstance() {
				throw new UnsupportedOperationException("Not supported in dummy genotype.");
			}

			@Override
			public long getId() {
				return offspringId;
			}

			@Override
			public void addParent(long id) {
				throw new UnsupportedOperationException("Not supported in dummy genotype.");
			}

			@Override
			public List<Long> getParentIDs() {
				throw new UnsupportedOperationException("Not supported in dummy genotype.");
			}
		};
	}

	/**
	 * Save each sub-population of a co-evolutionary run.
	 * 
	 * @param prefix File prefix of files to save
	 * @param saveDirectory Directory to save in
	 * @param populations List of all sub-populations of genotypes
	 * @param parallel Whether or not to save using parallel threads
	 */
	@SuppressWarnings("rawtypes")
	public static void saveAllSubPops(String prefix, String saveDirectory, ArrayList<ArrayList<Genotype>> populations, boolean parallel) {
		String fullSaveDir = saveDirectory + "/" + prefix;
		new File(fullSaveDir).mkdir();
		Parameters.parameters.setString("lastSavedDirectory", fullSaveDir);

		for (int i = 0; i < populations.size(); i++) {
			saveSubpop(i, prefix, saveDirectory, addListGenotypeType(populations.get(i)), parallel);
			if (populations.get(i).get(0) instanceof TWEANNGenotype) {
				EvolutionaryHistory.saveArchetype(i);
			}
		}
	}

	/**
	 * Save an individual sub-population from a co-evolutionary run.
	 * 
	 * @param <T> Phenotype generated by genotype
	 * @param num Sub-pop index
	 * @param prefix File prefix for saves
	 * @param saveDirectory Directory to save in
	 * @param population Population of genotypes
	 * @param parallel Whether to save in parallel threads
	 */
	public static <T> void saveSubpop(int num, String prefix, String saveDirectory, ArrayList<Genotype<T>> population, boolean parallel) {
		String experimentPrefix = Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber");
		String fullSaveDir = saveDirectory + "/" + prefix + "/" + num;
		prefix = experimentPrefix + "_" + prefix + "_" + num + "_";

		new File(fullSaveDir).mkdir();
		System.out.println("Saving to \"" + fullSaveDir + "\" with prefix \"" + prefix + "\"");

		ExecutorService poolExecutor = null;
		ArrayList<Future<Boolean>> futures = null;
		ArrayList<SaveThread<Genotype<T>>> saves = new ArrayList<SaveThread<Genotype<T>>>(population.size());

		for (int i = 0; i < population.size(); i++) {
			String filename = fullSaveDir;
			if (!filename.equals("")) {
				filename = filename + "/";
			}
			filename += prefix + i;
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
	}

	/**
	 * Loads xml files from a given directory into a vector of vectors such that
	 * each sub-vector contains a subpopulation. The "directory" is presumed to
	 * itself contain subdirectories named numerically: 0, 1, ... , (numPops - 1). 
	 * So, numPops designates the end size of the result returned, and the
	 * number of populations subdirectories to look for.
	 *
	 * Each subdir should contain a collection of xml files that can be loaded
	 * as Genotypes instances. Also, each subdir should have the same number
	 * of xml files to create equal sized subpops.
	 *
	 * @param directory
	 *            directory where subdirs containing xml files for subpops are.
	 * @param numPops
	 *            number of subpops to load.
	 * @return vector of loaded subpopulations, each stored in a vector of
	 *         genotypes
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList<ArrayList<Genotype>> loadSubPops(String directory, int numPops) {
		System.out.println("Load multiple populations");
		ArrayList<ArrayList<Genotype>> populations = new ArrayList<ArrayList<Genotype>>(numPops);
		boolean success = true;
		for (int i = 0; i < numPops; i++) {
			ArrayList<Genotype<Object>> pop = PopulationUtil.load(directory + "/" + i);
			success = success && (pop != null);
			/**
			 * The only way for Java to compile this is to switch the generic
			 * Objects over to being completely unknown types.
			 */
			populations.add(removeListGenotypeType(pop));
		}
		return populations;
	}

	/**
	 * Given an ArrayList of Genotypes instances, remove the T type and
	 * return the resulting list.
	 *
	 * @param <T> phenotype
	 * @param genotypes list of genotypes encoding T phenotypes
	 * @return genotype with the T type stripped away
	 */
	@SuppressWarnings("rawtypes")
	public static <T> ArrayList<Genotype> removeListGenotypeType(ArrayList<Genotype<T>> genotypes) {
		ArrayList<Genotype> ungenericPop = new ArrayList<Genotype>(genotypes.size());
		for (Genotype g : genotypes) {
			ungenericPop.add(g);
		}
		return ungenericPop;
	}

	/**
	 * Puts back type T information for all genotypes in an array list. For this
	 * to be valid, all genotypes must be of the same type
	 *
	 * @param <T> phenotype
	 * @param genotypes list of genotypes with unspecified phenotype
	 * @return genotype with phenotype T explicitly specified
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> ArrayList<Genotype<T>> addListGenotypeType(ArrayList<Genotype> genotypes) {
		ArrayList<Genotype<T>> genericPop = new ArrayList<Genotype<T>>(genotypes.size());
		for (Genotype g : genotypes) {
			genericPop.add(g);
		}
		return genericPop;
	}

	/**
	 * Add generic type T to list of Scores
	 *
	 * @param <T> phenotype
	 * @param scores list of scores for unspecified phenotype
	 * @return score list with phenotype made explicit
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> ArrayList<Score<T>> addListScoreType(ArrayList<Score> scores) {
		ArrayList<Score<T>> genericPop = new ArrayList<Score<T>>(scores.size());
		for (Score g : scores) {
			genericPop.add(g);
		}
		return genericPop;
	}

	/**
	 * Finds the index in the subpopulation of the genotype with a specified id.
	 * Returns -1 if no such genotype is found.
	 *
	 * @param subpopulation
	 *            array of genotypes
	 * @param id
	 *            genotype id that might be in subpopulation
	 * @return index of genotype with id, or -1
	 */
	@SuppressWarnings("rawtypes")
	public static int indexOfGenotypeWithId(ArrayList<Genotype> subpopulation, long id) {
		for (int q = 0; q < subpopulation.size(); q++) {
			if (subpopulation.get(q).getId() == id) {
				return q;
			}
		}
		return -1;
	}

	@SuppressWarnings("rawtypes")
	public static <T> ArrayList<Long> getGenotypeIds(ArrayList<Genotype<T>> genotypes) {
		ArrayList<Long> result = new ArrayList<Long>();
		for (Genotype g : genotypes) {
			result.add(g.getId());
		}
		return result;
	}

	/**
	 * Take two populations and find out, by reference to id numbers, which
	 * members are in one but not the other.
	 *
	 * @param <T> phenotype
	 * @param lhs
	 *            pop 1
	 * @param rhs
	 *            pop 2
	 * @return The first member of the pair contains individuals in lhs but not
	 *         in rhs, while the second is members of rhs that are not in lhs
	 */
	public static <T> Pair<ArrayList<Genotype<T>>, ArrayList<Genotype<T>>> populationDifferences(ArrayList<Genotype<T>> lhs, ArrayList<Genotype<T>> rhs) {
		ArrayList<Genotype<T>> leftDiffRight = ArrayUtil.setDifference(lhs, rhs);
		ArrayList<Genotype<T>> rightDiffLeft = ArrayUtil.setDifference(rhs, lhs);
		return new Pair<ArrayList<Genotype<T>>, ArrayList<Genotype<T>>>(leftDiffRight, rightDiffLeft);
	}

	/**
	 * From an array of scores, return the one whose embedded individual has a
	 * designated id
	 *
	 * @param <T>
	 *            type of phenotype
	 * @param id
	 *            id of genotype
	 * @param staticScores
	 *            scores to search, each containing a genotype
	 * @return the score matching the id
	 */
	public static <T> NSGA2Score<T> scoreWithId(long id, NSGA2Score<T>[] staticScores) {
		for (NSGA2Score<T> s : staticScores) {
			if (s.individual.getId() == id) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Converts an ArrayList of Genotype<T> to an array of Genotypes.
	 * This strips off the phenotype T
	 * 
	 * @param <T> Phenotype
	 * @param arrayList List of genotypes
	 * @return array of same genotypes
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Genotype[] genotypeArrayFromArrayList(ArrayList<Genotype<T>> arrayList) {
		Genotype[] genos = new Genotype[arrayList.size()];
		for (int i = 0; i < genos.length; i++) {
			genos[i] = arrayList.get(i);
		}
		return genos;
	}
	
	/**
	 * Converts an ArrayList of HyperNEAT CPPN genotypes to an ArrayList of corresponding 
	 * TWEANN genotypes based on an input HyperNEATTask.
	 * 
	 * @param hnt HyperNEATTask used to define substrate description
	 * @param population array list of HyperNEAT CPPN genotypes
	 * @return array list of substrate genotypes from population ArrayList
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<Genotype<T>> getSubstrateGenotypesFromCPPNs(HyperNEATTask hnt, ArrayList<Genotype<T>> population, int archetypeIndex) {
		ArrayList<Genotype<T>> substrateGenotypes = new ArrayList<>();
		for(int i = 0; i < population.size(); i++) {
			TWEANNGenotype genotype = ((HyperNEATCPPNGenotype) population.get(i)).getSubstrateGenotype(hnt);
			// Since these networks will evolve now, they need a real archetype index
			genotype.archetypeIndex = archetypeIndex;
			substrateGenotypes.add((Genotype<T>) genotype);
		}
		return substrateGenotypes;
	}
	
	/**
	 * Converts a double[][] population into an ArrayList<Genotype<ArrayList<Double>>>
	 * @param Population to be converted
	 * @return A converted population
	 */
	public static ArrayList<Genotype<ArrayList<Double>>> genotypeArrayListFromDoubles(double[][] population) {
		ArrayList<Genotype<ArrayList<Double>>> children = new ArrayList<Genotype<ArrayList<Double>>>(population.length);
		for (double[] geneSet : population) {
			children.add(new BoundedRealValuedGenotype(geneSet));
		}
		return children;
	}
	
	/**
	 * Saves the text necessary to view the genotypes neural network via graphviz.
	 * 
	 * @param <T> Generic type T
	 * @param genotypes that will have their neural networks saved in a text file.
	 */
	public static <T> void saveGraphVizNetworks(ArrayList<Genotype<T>> genotypes) {
		// check
		// check if the first genome is a TWEANNGenotype
		if(genotypes.get(0) instanceof TWEANNGenotype) {
			// make new sub-dir "GraphVizNetworks"
			File f = new File(FileUtilities.getSaveDirectory() +"/GraphVizNetworks");
			if (!f.exists()) f.mkdirs();
			
			// loop through all genotypes and create a file for each one containing toGraphViz method result
			String[] inputs = ((NetworkTask) MMNEAT.task).sensorLabels(); // get input labels
			String[] outputs = ((NetworkTask) MMNEAT.task).outputLabels(); // get output labels
			for(int i = 0; i < genotypes.size(); i++) {
				// use ((NetworkTask) MMNEAT.task)
				String nnString = ((TWEANNGenotype) genotypes.get(i)).toGraphViz(inputs, outputs);
				//String nnString = TWEANNGenotype.toGraphViz(inputs, outputs);
				File newNeuralNetowrkFile = new File(FileUtilities.getSaveDirectory() + "/GraphVizNetworks/neuralnetwork"+genotypes.get(i).getId()+".txt");
				
				// write to new file the output of toGraphViz method
				try {
					FileWriter fw = new FileWriter(newNeuralNetowrkFile);
					fw.write(nnString);
					fw.close();
				} catch(Exception e) {
					System.out.println(e);
				}
				
			}

		}
	}

}
