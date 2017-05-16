package edu.utexas.cs.nn.evolution.nsga2.bd;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2;
import edu.utexas.cs.nn.evolution.nsga2.bd.characterizations.BehaviorCharacterization;
import edu.utexas.cs.nn.evolution.nsga2.bd.vectors.BehaviorVector;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;

import java.util.ArrayList;

/**
 * Non-sorting genetic algorithm 2 with a behavioral diversity objective added.
 * 
 * @author Jacob Schrum
 * @commented Lauren Gillespie
 */
public class BDNSGA2<T> extends NSGA2<T> {

	public BDLog bdLog;
	@SuppressWarnings("rawtypes") // Will work the same regardless of genotype
	public BehaviorCharacterization characterization;
	private ArrayList<Score<T>> archive = null;
	private ArrayList<BehaviorVector> archiveBehaviors;
	private int maxArchiveSize;
	private int indexToAdd;

	/**
	 * Constructor for BD NSGA2 genetic algorithm
	 */
	@SuppressWarnings("rawtypes")
	public BDNSGA2() {
		try {// creates a new class instantiation of behavior characterization
			characterization = (BehaviorCharacterization) ClassCreation.createObject("behaviorCharacterization");
			if (writeOutput) {// only necessary if a log is to be kept
				bdLog = new BDLog("BD");
			}
		} catch (NoSuchMethodException ex) {
			System.out.println("Behavior characterization does not exist");
			System.exit(1);
		}
		// used to cap size of archive
		maxArchiveSize = Parameters.parameters.integerParameter("bdArchiveSize");
		if (maxArchiveSize > 0) {
			// archive keeps track of all scores from all pheno
			archive = new ArrayList<Score<T>>(maxArchiveSize);
		}
	}

	/**
	 * Get the behavior vectors corresponding to each member of the population
	 * 
	 * @param population
	 *            Population of scores: Genotypes have already been evaluated.
	 * @return List of one behavior vector per population member
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<BehaviorVector> getBehaviorVectors(ArrayList<Score<T>> population) {
		ArrayList<BehaviorVector> behaviorVectors = new ArrayList<BehaviorVector>(population.size());
		for (int i = 0; i < population.size(); i++) {// one behavior vector per
														// member of the
														// population
			behaviorVectors.add(characterization.getBehaviorVector(population.get(i)));
		}
		if (maxArchiveSize > 0) { // Use an archive
			archiveBehaviors = new ArrayList<BehaviorVector>(archive.size());
			// Get archive behaviors
			for (int i = 0; i < archive.size(); i++) {
				archiveBehaviors.add(characterization.getBehaviorVector(archive.get(i)));
			}
		}
		return behaviorVectors;
	}

	/**
	 * Given the behavior vectors of the entire population, return the diversity
	 * of the one individual specified by individualIndex, which is the distance
	 * from individualIndex to its nearest neighbor in behavior space.
	 * 
	 * @param behaviorVectors
	 *            List of behavior vectors
	 * @param individualIndex
	 *            index of individual in the list to compare to the others
	 * @param compareArchive
	 *            Whether or not to also compare the individual to behaviors in
	 *            the archive.
	 * @return Resulting score, which is behavioral distance from other
	 *         individuals.
	 */
	public double diversityScore(ArrayList<BehaviorVector> behaviorVectors, int individualIndex,
			boolean compareArchive) {
		double diversityScore = Double.MAX_VALUE;
		BehaviorVector individualBehavior = behaviorVectors.get(individualIndex);
		for (int i = 0; i < behaviorVectors.size(); i++) {
			if (i != individualIndex) {// finds the lowest diversity between all
										// other population members for
										// comparison
				diversityScore = Math.min(diversityScore, behaviorVectors.get(i).distance(individualBehavior));
			}
		}
		if (compareArchive) { // Use an archive
			for (int i = 0; i < archiveBehaviors.size(); i++) {
				double distance = archiveBehaviors.get(i).distance(individualBehavior);
				if (distance > 0) { // Assume that only identical agent would
									// have zero distance (fix later?)
					diversityScore = Math.min(diversityScore, distance);
				}
			}
		}
		return diversityScore;
	}

	/**
	 * Given the behavior vectors of all members of the population, calculate
	 * each member's diversity score.
	 * 
	 * @param behaviorVectors
	 *            Behavior vectors of all population members
	 * @param compareArchive
	 *            Whether or not to compare behaviors against archive of past
	 *            behaviors
	 * @return List of behavior scores for each individual in population.
	 */
	public ArrayList<Double> allDiversityScores(ArrayList<BehaviorVector> behaviorVectors, boolean compareArchive) {
		ArrayList<Double> result = new ArrayList<Double>(behaviorVectors.size());
		double maxDiversity = -Double.MAX_VALUE;// more negative = more diverse
		int mostDiverseIndex = -1;
		for (int i = 0; i < behaviorVectors.size(); i++) {
			double score = diversityScore(behaviorVectors, i, compareArchive);
			result.add(score);
			if (score > maxDiversity) {
				maxDiversity = score;
				mostDiverseIndex = i;
			}
		}
		if (compareArchive) { // Use an archive: add most diverse individual
								// from new population
			indexToAdd = mostDiverseIndex;
		}
		if (writeOutput) {
			bdLog.log(result, generation);
		}
		return result;
	}

	/**
	 * Adds behavioral scores to each individual before they are subjected to
	 * NSGA2 selection. Also adds some individuals to behavior archive, if it is
	 * being used.
	 */
	@Override
	public ArrayList<Score<T>> prepareSourcePopulation(ArrayList<Score<T>> parentScores,
			ArrayList<Score<T>> childrenScores) {
		ArrayList<Score<T>> population = super.prepareSourcePopulation(parentScores, childrenScores);
		characterization.prepare();// gets a random syllabus
		ArrayList<BehaviorVector> behaviorVectors = getBehaviorVectors(population);
		ArrayList<Double> diversityScores = allDiversityScores(behaviorVectors, maxArchiveSize > 0);

		if (maxArchiveSize > 0) { // Adjust archive
			// If overfull, remove least diverse individual from archive
			if (archive.size() >= maxArchiveSize) {
				// Get diversity scores for archive members with respect to
				// archive only
				ArrayList<Double> archiveDiversityScores = allDiversityScores(archiveBehaviors, false);
				int leastDiverseIndex = -1;
				double leastDiverseScore = Double.MAX_VALUE;
				for (int i = 0; i < archiveDiversityScores.size(); i++) {
					double score = archiveDiversityScores.get(i);
					if (score < leastDiverseScore) {
						leastDiverseIndex = i;
						score = leastDiverseScore;
					}
				}
				archive.remove(leastDiverseIndex);
			}
			// Add most diverse individual from current population
			archive.add(population.get(indexToAdd));
		}

		for (int i = 0; i < diversityScores.size(); i++) {
			population.get(i).extraScore(diversityScores.get(i));
		}

		return population;
	}

	/**
	 * Closes BDNSGA
	 * 
	 * @param population
	 *            population of genotypes
	 */
	@Override
	public void close(ArrayList<Genotype<T>> population) {
		super.close(population);
		if (writeOutput) {
			this.bdLog.close();
		}
	}
}
