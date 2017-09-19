package edu.southwestern.evolution.mapelites;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.random.RandomNumbers;
import wox.serial.Easy;

public class MAPElites<T> implements SteadyStateEA<T> {

	private boolean io;
	private MMNEATLog log = null;
	private LonerTask<T> task;
	private Archive<T> archive;
	private boolean mating;
	private double crossoverRate;
	private int iterations;
	private int iterationsWithoutElite;
	
	@SuppressWarnings("unchecked")
	public MAPElites() {
		this.task = (LonerTask<T>) MMNEAT.task;
		this.io = Parameters.parameters.booleanParameter("io"); // write logs
		if(io) {
			log = new MMNEATLog("MAPElites");
		}
		this.archive = new Archive<>(Parameters.parameters.booleanParameter("netio"));
		this.mating = Parameters.parameters.booleanParameter("mating");
		this.crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
		this.iterations = Parameters.parameters.integerParameter("lastSavedGeneration");
		this.iterationsWithoutElite = 0; // Not accurate on resume
	}
	
	/**
	 * Get the archive
	 * @return
	 */
	public Archive<T> getArchive() {
		return archive;
	}
	
	/**
	 * Fill the archive with a set number of random initial genotypes,
	 * according to where they best fit.
	 * @param example Starting genotype used to derive new instances
	 */
	@Override
	public void initialize(Genotype<T> example) {
		if(iterations > 0) {
			// Loading from saved archive
			String archiveDir = archive.getArchiveDirectory();
			List<String> binLabels = archive.getBinMapping().binLabels();
			// Load each elite from xml file into archive
			for(int i = 0; i < binLabels.size(); i++) {
				String binDir = archiveDir + "/" + binLabels.get(i) + "/";
				@SuppressWarnings("unchecked")
				Genotype<T> elite = (Genotype<T>) Easy.load(binDir + "elite.xml"); // Load genotype
				// Load behavior scores
				@SuppressWarnings("unchecked")
				ArrayList<Double> scores = (ArrayList<Double>) Easy.load(binDir + "scores.xml"); // Load scores
				// Package in a score
				Score<T> score = new Score<T>(elite, new double[0], scores);
				archive.archive.set(i, score); // Directly set the bin contents
			}
		} else {
			// Start from scratch
			int startSize = Parameters.parameters.integerParameter("mu");
			ArrayList<Genotype<T>> startingPopulation = PopulationUtil.initialPopulation(example, startSize);
			for(Genotype<T> g : startingPopulation) {
				Score<T> s = task.evaluate(g);
				archive.add(s); // Fill the archive with random starting individuals
			}	
		}
	}

	private void log() {
		if(io) {
			log.log(iterations + "\t" + iterationsWithoutElite + "\t" + StringUtils.join(ArrayUtils.toObject(archive.getEliteScores()), "\t"));
		}
	}
	
	/**
	 * Create one (maybe two) new individuals by randomly
	 * sampling from the elites in random bins. The reason
	 * that two individuals may be added is if crossover occurs.
	 * In this case, both children can potentially be added 
	 * to the archive.
	 */
	@Override
	public void newIndividual() {
		boolean newEliteProduced = false; // Asume no new elites will be produced
		
		int index = archive.randomBinIndex();
		Genotype<T> parent1 = archive.getElite(index).individual;
		long parentId1 = parent1.getId(); // Parent Id comes from original genome
		long parentId2 = -1;
		Genotype<T> child1 = parent1.copy(); // Copy with different Id (will be further modified below)
		
		// Potentially mate with second individual
		if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
			int otherIndex = archive.randomBinIndex(); // From a different bin
			Genotype<T> parent2 = archive.getElite(otherIndex).individual;
			parentId2 = parent2.getId(); // Parent Id comes from original genome
			Genotype<T> child2 = parent2.copy(); // Copy with different Id (further modified below)
			
			// Replace child2 with a crossover result, and modify child1 in the process (two new children)
			child2 = child1.crossover(child2);
			child2.mutate(); // Probabilistic mutation of child
			EvolutionaryHistory.logLineageData(parentId1,parentId2,child2);
			// Evaluate and add child to archive
			Score<T> s2 = task.evaluate(child2);
			// Indicate whether elite was added
			boolean child2WasElite = archive.add(s2);
			newEliteProduced = newEliteProduced || child2WasElite; 
		}
		
		child1.mutate(); // Was potentially modified by crossover
		if (parentId2 == -1) {
			EvolutionaryHistory.logLineageData(parentId1,child1);
		} else {
			EvolutionaryHistory.logLineageData(parentId1,parentId2,child1);
		}
		// Evaluate and add child to archive
		Score<T> s1 = task.evaluate(child1);
		// Indicate whether elite was added
		boolean child1WasElite = archive.add(s1);
		newEliteProduced = newEliteProduced || child1WasElite;
		// Log to file
		log();
		Parameters.parameters.setInteger("lastSavedGeneration", iterations);
		// Track total iterations
		iterations++;
		// Track how long we have gone without producing a new elite individual
		if(newEliteProduced) {
			iterationsWithoutElite = 0;
		} else {
			iterationsWithoutElite++;
		}
		System.out.println(iterations + "\t" + iterationsWithoutElite + "\t");
	}
	
	/**
	 * Number of times new individuals have been 
	 * generated to add to archive.
	 */
	@Override
	public int currentIteration() {
		return iterations;
	}

	@Override
	public void finalCleanup() {
		task.finalCleanup();
	}

	/**
	 * Take members from archive and place them in an ArrayList
	 */
	@Override
	public ArrayList<Genotype<T>> getPopulation() {
		ArrayList<Genotype<T>> result = new ArrayList<Genotype<T>>(archive.archive.size());
		for(Score<T> s : archive.archive) {
			result.add(s.individual);
		}
		return result;
	}
}
