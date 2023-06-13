package edu.southwestern.evolution.mome;

import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.Stream;

import org.jenetics.internal.math.random;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.random.RandomNumbers;

/**
 * yay! I made this!
 * @author lewisj
 *
 * @param <T>
 */
public class MOME<T> implements SteadyStateEA<T>{

	protected MOMEArchive<T> archive;
	protected int iterations;	//might want to rename? what is it, just the number of individuals created so far?
	protected LonerTask<T> task; ///seems to be for cleanup, not sure what else
	
	//below deals with new individual creation
	private static final int NUM_CODE_EMPTY = -1;	//initialization number for a non existent parent
	private boolean mating;	//determines if using mating functionality
	private double crossoverRate;

	@Override
	public void initialize(Genotype<T> example) {
		// Do not allow Minecraft to contain archive when using MOME
		Parameters.parameters.setBoolean("minecraftContainsWholeMAPElitesArchive", false);
		
		// TODO Auto-generated method stub
		ArrayList<Genotype<T>> startingPopulation; // Will be new or from saved archive

		System.out.println("Fill up initial archive");		
		// Start from scratch
		int startSize = Parameters.parameters.integerParameter("mu");
		startingPopulation = PopulationUtil.initialPopulation(example, startSize);			
		
		assert startingPopulation.size() == 0 || !(startingPopulation.get(0) instanceof BoundedRealValuedGenotype) || ((BoundedRealValuedGenotype) startingPopulation.get(0)).isBounded() : "Initial individual not bounded: "+startingPopulation.get(0);
	
		
		//add initial population to the archive
		Vector<Score<T>> evaluatedPopulation = new Vector<Score<T>>(startingPopulation.size());
		//not sure if we need netio stuff at all
		boolean backupNetIO = CommonConstants.netio;
		CommonConstants.netio = false; // Some tasks require archive comparison to do this, but it does not exist yet.
		Stream<Genotype<T>> evaluateStream = Parameters.parameters.booleanParameter("parallelMAPElitesInitialize") ? 
												startingPopulation.parallelStream() :
												startingPopulation.stream();
		if(Parameters.parameters.booleanParameter("parallelMAPElitesInitialize"))										
			System.out.println("Evaluate archive in parallel");
		// Evaluate initial population
		evaluateStream.forEach( (g) -> {
			Score<T> s = task.evaluate(g);
			evaluatedPopulation.add(s);
		});
		CommonConstants.netio = backupNetIO;
		
		 // Add initial population to archive, if add is true
		evaluatedPopulation.parallelStream().forEach( (s) -> {
			archive.add(s); // Fill the archive with random starting individuals, only when this flag is true
		});
		
		//initializing a variable
		this.mating = Parameters.parameters.booleanParameter("mating");

	}

	@Override
	public void newIndividual() {
		// TODO Auto-generated method stub
		//steal a lot
		//get random individual for parent
		Genotype<T> parentGenotype1 = archive.getRandomIndividaul().individual;
		long parentId1 = parentGenotype1.getId();
		long parentId2 = NUM_CODE_EMPTY;	//initialize second parent in case it's needed
		
		//creating the first child
		Genotype<T> childGenotype1 = parentGenotype1.copy(); // Copy with different Id (will be further modified below)
		childGenotype1.addParent(parentId1);

		// Potentially mate with second individual
		if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
		}
		/**
		 * // Potentially mate with second individual
		if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
			int otherIndex = archive.randomOccupiedBinIndex(); // From a different bin
			Genotype<T> parent2 = archive.getElite(otherIndex).individual;
			parentId2 = parent2.getId(); // Parent Id comes from original genome
			Genotype<T> child2 = parent2.copy(); // Copy with different Id (further modified below)
			
			// Replace child2 with a crossover result, and modify child1 in the process (two new children)
			child2 = child1.crossover(child2);
			child2.mutate(); // Probabilistic mutation of child
			child2.addParent(parent2.getId());
			child2.addParent(parent1.getId());
			child1.addParent(parent2.getId());
			EvolutionaryHistory.logLineageData(parentId1,parentId2,child2);
			// Evaluate and add child to archive
			Score<T> s2 = task.evaluate(child2);
			// Indicate whether elite was added
			boolean child2WasElite = archive.add(s2);
			fileUpdates(child2WasElite); // Log for each individual produced
		}
		 */
	}

	@Override
	public int currentIteration() {
		// TODO Auto-generated method stub
		return iterations;
	}

	@Override
	public void finalCleanup() {
		// TODO Auto-generated method stub
		task.finalCleanup();
	}

	/**
	 * gets an ArrayList of the populations genotypes
	 */
	@Override
	public ArrayList<Genotype<T>> getPopulation() {
		// TODO Auto-generated method stub
		 ArrayList<Genotype<T>> result = new ArrayList<Genotype<T>>(archive.archive.size());

		 archive.archive.forEach( (coords, subpop) -> {	////goes through the archive
			 for(Score<T> s : subpop) {		//goes through the scores of the subpop
				 result.add(s.individual);
			 }
		 });
		 
		return result;
	}

	@Override
	public boolean populationChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main (String[] args) {
		//
	}
}
