package edu.southwestern.evolution.nsga2;

import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mulambda.MuPlusLambda;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Better;
import edu.southwestern.scores.MultiObjectiveScore;
import edu.southwestern.scores.ObjectiveComparator;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementation of Deb's NSGA2 multiobjective EA. NSGA2 stands for non-sorting
 * genetic algorithm 2
 * 
 * @param <T> phenotype
 * @commented Lauren Gillespie
 */
public class NSGA2<T> extends MuPlusLambda<T> {

	protected boolean mating;// whether or not mating will occur
	protected double crossoverRate;// rate at which phenotypes are crossed over

	/**
	 * Default constructor
	 */
	public NSGA2() {
		this(Parameters.parameters.booleanParameter("io"));
	}

	/**
	 * Constructor for NSGA2
	 * 
	 * @param io
	 *            whether or not to output files
	 */
	@SuppressWarnings("unchecked")
	public NSGA2(boolean io) {
		this((SinglePopulationTask<T>) MMNEAT.task, Parameters.parameters.integerParameter("mu"), io);
	}

	/**
	 * Constructor for NSGA2
	 * 
	 * @param io
	 *            whether or not to output files
	 * @param task
	 *            task to be evolved
	 * @param mu
	 *            Size of parent population
	 *
	 */
	public NSGA2(SinglePopulationTask<T> task, int mu, boolean io) {
		super(task, mu, mu, io);
		mating = Parameters.parameters.booleanParameter("mating");
		crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
	}

	/**
	 * Generates children genotypes based on NSGA2 evolution scheme and scores
	 * of parents
	 * 
	 * @param numChildren
	 *            number of children to be created in evolved population
	 * @param parentScores
	 *            array list of parent scores
	 * @return child genotypes
	 */
	@Override
	public ArrayList<Genotype<T>> generateChildren(int numChildren, ArrayList<Score<T>> parentScores) {
		NSGA2Score<T>[] scoresArray = getNSGA2Scores(parentScores);
		return generateNSGA2Children(numChildren, scoresArray, currentGeneration(), mating, crossoverRate);
	}

	/**
	 * Generates a list of offspring genotypes created through NSGA2 sort of
	 * parent genotypes
	 * 
     * @param <T> phenotype
	 * @param numChildren
	 * @param scoresArray
	 * @param generation
	 * @param mating
	 * @param crossoverRate
	 * @return list of offspring genotypes from sort
	 */
	public static <T> ArrayList<Genotype<T>> generateNSGA2Children(int numChildren, NSGA2Score<T>[] scoresArray,
			int generation, boolean mating, double crossoverRate) {
		assignCrowdingDistance(scoresArray);
		fastNonDominatedSort(scoresArray);

		ArrayList<Genotype<T>> offspring = new ArrayList<Genotype<T>>(numChildren);
		Better<NSGA2Score<T>> judge;// will hold the comparator that is used
		if (generation == 0) {// first generation is a unique case that requires a different comparator
			judge = new Domination<T>();
		} else {// else a comparator that uses parent data is used to critique for children population
			judge = new ParentComparator<T>();
		}

		for (int i = 0; i < numChildren; i++) {
			int e1 = RandomNumbers.randomGenerator.nextInt(scoresArray.length);
			int e2 = RandomNumbers.randomGenerator.nextInt(scoresArray.length);
			// determines which of the two randomly chosen scores is better
			NSGA2Score<T> better = judge.better(scoresArray[e1], scoresArray[e2]);
			Genotype<T> source = better.individual;// stores better genotype
			long parentId1 = source.getId();
			long parentId2 = -1;
			Genotype<T> e = source.copy();
			
			// This restriction on mutation and crossover only makes sense when
			// using pacman coevolution with a fitness/population for each 
			// individual level
			if (!CommonConstants.requireFitnessDifferenceForChange || better.scores[0] > 0) {
				// If neither net has reached a given level, the scores of 0
				// will prevent mating.
				if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
					e1 = RandomNumbers.randomGenerator.nextInt(scoresArray.length);
					e2 = RandomNumbers.randomGenerator.nextInt(scoresArray.length);

					Genotype<T> otherSource = judge.better(scoresArray[e1], scoresArray[e2]).individual;
					parentId2 = otherSource.getId();
					Genotype<T> otherOffspring;

					if (CommonConstants.cullCrossovers) {
						ArrayList<Genotype<T>> keepers = cullCrossovers(e, otherSource);
						// Best two of litter get kept
						e = keepers.get(0);
						otherOffspring = keepers.get(1);
					} else {// keeps all crossovers
						Genotype<T> other = otherSource.copy();
						// Genotype e is directly modified by the crossover call.
						// Genotype otherOffspring is now a modified version of other.
						otherOffspring = e.crossover(other);
						assert otherOffspring.getId() != otherSource.getId() : "otherOffspring should be a newly created genotype. otherSource.getId() is " + otherSource.getId() + 
																	           ", and otherOffspring.getId() is "+otherOffspring.getId()+", and other.getId() is "+other.getId();
					}
					i++;
					/*
					 * The offspring e will be added no matter what. Because i
					 * is increased and then checked, otherOffspring will NOT
					 * always be added.
					 */
					if (i < numChildren) {
						otherOffspring.mutate();
						offspring.add(otherOffspring);
						EvolutionaryHistory.logLineageData(parentId1,parentId2,otherOffspring);
					}
				}

				e.mutate();// randomly mutates copied source
			}

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
	 * gets the NSGA2 modified scores from raw scores
	 * 
	 * @param scores
	 *            list of scores
	 * @return modified scores
	 */
	public NSGA2Score<T>[] getNSGA2Scores(ArrayList<Score<T>> scores) {
		return staticNSGA2Scores(scores);
	}

	/**
	 * static version of getNSGA2Scores converts array list form of scores to
	 * NSGA2Score array
	 * 
	 * @param <T> phenotype
	 * @param scores
	 *            scores to convert
	 * @return array of NSGA2Scores
	 */
	public static <T> NSGA2Score<T>[] staticNSGA2Scores(ArrayList<Score<T>> scores) {
		@SuppressWarnings("unchecked")
		NSGA2Score<T>[] scoresArray = new NSGA2Score[scores.size()];
		for (int i = 0; i < scores.size(); i++) {
			scoresArray[i] = new NSGA2Score<T>(scores.get(i));
		}
		return scoresArray;
	}

	/**
	 * non-static version of selection method
	 * 
	 * @param numParents
	 *            number of parents to select from
	 * @param scores
	 *            scores of parents
	 * @return list of selected genotypes
	 */
	@Override
	public ArrayList<Genotype<T>> selection(int numParents, ArrayList<Score<T>> scores) {
		return staticSelection(numParents, staticNSGA2Scores(scores));
	}

	/**
	 * static version of NSGA2 selection method
	 * 
	 * @param <T> phenotype
	 * @param numParents
	 *            number of parents to select from
	 * @param scoresArray
	 *            array of scores from parents
	 * @return array list of selected genotypes
	 */
	public static <T> ArrayList<Genotype<T>> staticSelection(int numParents, NSGA2Score<T>[] scoresArray) {
		assignCrowdingDistance(scoresArray);
		// gets the pareto front of scores using a fast non-dominated sort
		ArrayList<ArrayList<NSGA2Score<T>>> fronts = fastNonDominatedSort(scoresArray);

		ArrayList<Genotype<T>> newParents = new ArrayList<Genotype<T>>(numParents);
		int numAdded = 0;
		int currentFront = 0;

		while (numAdded < numParents) {
			ArrayList<NSGA2Score<T>> front = fronts.get(currentFront);
                        // necessary if front is bigger than original number of parents
			if (front.size() <= (numParents - numAdded)) {
				for (int i = 0; i < front.size(); i++) {
					newParents.add(front.get(i).individual);
					numAdded++;
				}
			} else {
				@SuppressWarnings("unchecked")
				NSGA2Score<T>[] lastFront = front.toArray(new NSGA2Score[front.size()]);
				Arrays.sort(lastFront, new CrowdingDistanceComparator<T>());
				int index = lastFront.length - 1;
				while (numAdded < numParents) {
					newParents.add(lastFront[index--].individual);
					numAdded++;
				}
			}
			currentFront++;
		}
		return newParents;
	}

	/**
	 * Given the whole population of scores (after evaluation), assign crowding
	 * distances to each individual.
	 * 
	 * @param scores
	 *            Each instance is a set of multiple scores for a member of the
	 *            population that was just evaluated.
	 */
	private static <T> void assignCrowdingDistance(NSGA2Score<T>[] scores) {
		// reset distances
		for (int i = 0; i < scores.length; i++) {
			scores[i].setCrowdingDistance(0);
		}
		int numObjectives = scores[0].numObjectives();

		for (int j = 0; j < numObjectives; j++) {
			if (scores[0].useObjective(j)) {
				Arrays.sort(scores, new ObjectiveComparator<T>(j));

				scores[0].setCrowdingDistance(Float.POSITIVE_INFINITY);
				scores[scores.length - 1].setCrowdingDistance(Float.POSITIVE_INFINITY);

				double min = scores[0].objectiveScore(j);
				double max = scores[scores.length - 1].objectiveScore(j);

				// Just leave all crowding distances at 0 if all objective
				// scores are the same
				if (max - min > 0) {
					for (int k = 1; k < scores.length - 1; k++) {
						scores[k].crowdingDistance += Math
								.abs(scores[k + 1].objectiveScore(j) - scores[k - 1].objectiveScore(j)) / (max - min);
					}
				}
			}
		}
	}

	/**
	 * Sort the evaluated population into Pareto fronts according to their
	 * objective scores.
	 * 
	 * @param scores
	 *            Each instance is a set of multiple scores for a member of the
	 *            population that was just evaluated.
	 * @return List of lists in which each sublist is one Pareto front. Each
	 *         member of the original scores array will be in exactly one of the
	 *         sublists that is returned. Earlier sublists dominate subsequent
	 *         sublists.
	 */
	private static <T> ArrayList<ArrayList<NSGA2Score<T>>> fastNonDominatedSort(NSGA2Score<T>[] scores) {

		for (int i = 0; i < scores.length; i++) {
			assert scores[i] != null : "Score is null! " + i;
			scores[i].reset();
		}

		for (int i = 0; i < scores.length; i++) {
			NSGA2Score<T> p = scores[i];
			for (int j = 0; j < scores.length; j++) {
				if (i != j) {
					NSGA2Score<T> q = scores[j];
					if (p.isBetter(q)) {// here is where actual sorting occurs
						p.addDominatedIndividual(q);
						q.increaseNumDominators();
					}
				}
			}
		}

		int numAssigned = 0;
		int currentFront = 0;
		ArrayList<ArrayList<NSGA2Score<T>>> frontSet = new ArrayList<ArrayList<NSGA2Score<T>>>(scores.length);
		// adds an array list of front members to an array list of fronts
		while (numAssigned < scores.length) {
			// Although this sizing scheme will waste space, it will assure that
			// no resize is ever needed
			frontSet.add(new ArrayList<NSGA2Score<T>>(scores.length - numAssigned));

			for (int i = 0; i < scores.length; i++) {
				if (!scores[i].isAssigned && scores[i].numDominators == 0) {
					frontSet.get(currentFront).add(scores[i]);
					scores[i].assign(currentFront);
					numAssigned++;
				}
			}

			for (int i = 0; i < scores.length; i++) {
				if (scores[i].isAssigned && !scores[i].processed) {
					scores[i].process();
				}
			}

			currentFront++;
		}

		return frontSet;
	}

	/**
	 * Return just the Pareto front for a given population of scores.
	 * 
	 * @param <T> phenotype
	 * @param scores
	 *            Multiobjective scores for evaluated individuals.
	 * @return All non-dominated individuals in population: The Pareto front
	 */
	public static <T> ArrayList<NSGA2Score<T>> getParetoFront(NSGA2Score<T>[] scores) {
		// gets first member from return array b/c that corresponds with first front
		return fastNonDominatedSort(scores).get(0);
	}

	/**
	 * Why is this needed in addition to fastNonDominatedSort? Not sure.
	 * Basically a public way to access fastNonDominatedSort
	 * 
	 * @param <T> phenotype
	 * @param scores
	 *            See fastNonDominatedSort
	 * @return See fastNonDominatedSort
	 */
	public static <T> ArrayList<ArrayList<NSGA2Score<T>>> getParetoLayers(NSGA2Score<T>[] scores) {
		return fastNonDominatedSort(scores);
	}

	/**
	 * Method mates two genotypes multiple times and evaluates the offspring.
	 * Only the best resulting offspring is actually kept as the real children of
	 * the pairing.
	 * 
	 * @param <T> Phenotype
	 * @param parent1 First parent
	 * @param parent2 Second parent
	 * @return List of best two offspring
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<Genotype<T>> cullCrossovers(Genotype<T> parent1, Genotype<T> parent2) {
		ArrayList<Score<T>> litter = new ArrayList<Score<T>>(CommonConstants.litterSize);
		// Fill litter
		while (litter.size() < CommonConstants.litterSize) {
			// Try crossover
			// Will be a candidate once crossover modifies it
			Genotype<T> candidate1 = parent1.copy();
			Genotype<T> other = parent2.copy();
			Genotype<T> candidate2 = candidate1.crossover(other);// crossover  of  candidate
			// Evaluate and add to litter
			Pair<double[], double[]> score = ((NoisyLonerTask<T>) MMNEAT.task).oneEval(candidate1, 0);
			MultiObjectiveScore<T> s = new MultiObjectiveScore<T>(candidate1, score.t1, null, score.t2);
			litter.add(s);// adds either candidate or cross over candidate

			if (litter.size() < CommonConstants.litterSize) {
				score = ((NoisyLonerTask<T>) MMNEAT.task).oneEval(candidate2, 0);
				s = new MultiObjectiveScore<T>(candidate2, score.t1, null, score.t2);
				litter.add(s);
			}
		}
		// Cull litter
		ArrayList<Genotype<T>> keepers = staticSelection(2, staticNSGA2Scores(litter));
		return keepers;
	}
}
