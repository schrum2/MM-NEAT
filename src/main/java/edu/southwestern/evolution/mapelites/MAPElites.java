package edu.southwestern.evolution.mapelites;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.random.RandomNumbers;

public class MAPElites<T> implements SteadyStateEA<T> {

	private boolean io;
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
		this.archive = new Archive<>(Parameters.parameters.booleanParameter("netio"));
		this.mating = Parameters.parameters.booleanParameter("mating");
		this.crossoverRate = Parameters.parameters.doubleParameter("crossoverRate");
		this.iterations = 0;
		this.iterationsWithoutElite = 0;
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
		int startSize = Parameters.parameters.integerParameter("mu");
		ArrayList<Genotype<T>> startingPopulation = PopulationUtil.initialPopulation(example, startSize);
		for(Genotype<T> g : startingPopulation) {
			Score<T> s = task.evaluate(g);
			boolean elite = archive.add(s); // Fill the archive with random starting individuals
			if(elite && io) {
				// TODO: Log information somehow
			}
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
		
		String label = archive.randomBinLabel();
		Genotype<T> parent1 = archive.getElite(label).individual;
		long parentId1 = parent1.getId(); // Parent Id comes from original genome
		long parentId2 = -1;
		Genotype<T> child1 = parent1.copy(); // Copy with different Id (will be further modified below)
		
		// Potentially mate with second individual
		if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
			String otherLabel = archive.randomBinLabel(); // From a different bin
			Genotype<T> parent2 = archive.getElite(otherLabel).individual;
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
			if(child2WasElite && io) {
				// TODO: Log information somehow
			}
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
		if(child1WasElite && io) {
			// TODO: Log information somehow
		}
		// Track total iterations
		iterations++;
		// Track how long we have gone without producing a new elite individual
		if(newEliteProduced) {
			iterationsWithoutElite = 0;
		} else {
			iterationsWithoutElite++;
		}
		System.out.println(iterations + "\t" + iterationsWithoutElite);
	}
	
	/**
	 * Number of times new individuals have been 
	 * generated to add to archive.
	 */
	@Override
	public int currentIteration() {
		return iterations;
	}
}
