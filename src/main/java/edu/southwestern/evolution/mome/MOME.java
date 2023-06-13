package edu.southwestern.evolution.mome;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.Stream;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.random.RandomNumbers;

/**
 * TODO: Explain a bit more, and also cite the paper whose algorithm we are implementing using ACM style
 * yay! I made this!
 * @author lewisj
 *
 * @param <T>
 */
public class MOME<T> implements SteadyStateEA<T>{

	protected MOMEArchive<T> archive;
								// TODO: Yes, iterations tracks the number of individuals generated. This needs to be incremented somewhere
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
		
		// Evaluate initial population and add to evaluated population
		evaluateStream.forEach( (g) -> {
			Score<T> s = task.evaluate(g);
			evaluatedPopulation.add(s);
		});
		
		//unsure if we need netio stuff
		CommonConstants.netio = backupNetIO;
	
//ask later if this is a flag?
		 // Add evaluated population to archive, if add is true
		evaluatedPopulation.parallelStream().forEach( (s) -> {
			archive.add(s); // Fill the archive with random starting individuals, only when this flag is true
		});
		
		//initializing a variable
		this.mating = Parameters.parameters.booleanParameter("mating");

	}

	@Override
	public void newIndividual() {
		//get random individual for parent 1
		Genotype<T> parentGenotype1 = archive.getRandomIndividaul().individual;
		long parentId1 = parentGenotype1.getId();
		long parentId2 = NUM_CODE_EMPTY;	//initialize second parent in case it's needed
		
		//creating the first child
		Genotype<T> childGenotype1 = parentGenotype1.copy(); // Copy with different Id (will be further modified below)
		childGenotype1.addParent(parentId1);

		// Potentially mate with second individual
		if (mating && RandomNumbers.randomGenerator.nextDouble() < crossoverRate) {
			//get a random individual for parent 2
			Genotype<T> parentGenotype2 = archive.getRandomIndividaul().individual;
			parentId2 = parentGenotype2.getId();	// Parent Id comes from original genome
			
			//create second child
			Genotype<T> childGenotype2 = parentGenotype2.copy(); // Copy with different Id (further modified below)
			
			// Replace child2 with a crossover result
			childGenotype2 = childGenotype1.crossover(childGenotype2);
			childGenotype2.mutate(); // Probabilistic mutation of child
			
			//add parent ids to both children
			childGenotype2.addParent(parentId2);
			childGenotype2.addParent(parentId1);
			childGenotype1.addParent(parentId2);
			
			//evolutionary history log data
			EvolutionaryHistory.logLineageData(parentId1,parentId2,childGenotype2);

			// Evaluate and add child to archive
			Score<T> s2 = task.evaluate(childGenotype2);
			// Indicate whether elite was added
			boolean child2WasElite = archive.add(s2);		//this variable is relevant to logging
			
			//some sort of logging should be placed here
			//fileUpdates(child2WasElite); // Log for each individual produced
		}
		
		childGenotype1.mutate(); // Was potentially modified by crossover
		
		//evolutionary history log data
		if (parentId2 == NUM_CODE_EMPTY) {
			EvolutionaryHistory.logLineageData(parentId1,childGenotype1);
		} else {
			EvolutionaryHistory.logLineageData(parentId1,parentId2,childGenotype1);
		}
		
		// Evaluate and add child 1 to archive
		Score<T> s1 = task.evaluate(childGenotype1);
		// Indicate whether elite was added
		boolean child1WasElite = archive.add(s1);	//this variable is relevant to logging
		
		//some sort of logging should be placed here
		//fileUpdates(child1WasElite); // Log for each individual produced
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
		return false; // TODO: This needs to be based on whether new individuals are added to the archive
	}

	public static void main (String[] args) {
		try {
			// This is a GECCO set of parameters with some minor changes for MOME
			MMNEAT.main("runNumber:1 randomSeed:1 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mome.MOME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:mometest log:MOMETest-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
