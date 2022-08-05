package edu.southwestern.experiment.evolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNPlusParametersGenotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;

public class SteadyStateExperiment<T> implements Experiment {

	private SteadyStateEA<T> ea;
	private int maxIterations;
	private boolean cleanArchetype;
	private boolean plainTWEANNGenotype;

	@SuppressWarnings("unchecked")
	public SteadyStateExperiment() {
		this((SteadyStateEA<T>) MMNEAT.ea, MMNEAT.genotype);
	}

	public SteadyStateExperiment(SteadyStateEA<T> ea, Genotype<T> example) {
		this.ea = ea;
		this.ea.initialize(example);
		// Overriding the meaning of maxGens to treat it like maxIterations
		maxIterations = Parameters.parameters.integerParameter("maxGens");
		this.plainTWEANNGenotype = MMNEAT.genotype instanceof TWEANNGenotype;
		this.cleanArchetype = plainTWEANNGenotype || MMNEAT.genotype instanceof TWEANNPlusParametersGenotype;
	}

	@Override
	public void init() {
		// Init of EA was called in constructor instead
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() {
		int numThreads = Parameters.parameters.booleanParameter("parallelEvaluations") ? Parameters.parameters.integerParameter("threads") : 1;
		Thread[] threads = new Thread[numThreads];
		for(int i = 0; i < numThreads; i++) {
			threads[i] = new Thread() {
				@Override
				public void run() {
					while(!shouldStop()) { // Until done
						ea.newIndividual(); // Make new individuals
						synchronized(SteadyStateExperiment.this) {
							Parameters.parameters.saveParameters(); // Save the parameters and the archetype
							if(Parameters.parameters.booleanParameter("steadyStateArchetypeSaving") && ea.populationChanged()) { // In steady state, not every individual is added to the population
								EvolutionaryHistory.saveArchetype(0);
							}
							if(cleanArchetype && EvolutionaryHistory.timeToClean(ea.currentIteration())) { // Periodically clean extinct genes from the archetype
								ArrayList<Genotype<T>> pop = ea.getPopulation();
								ArrayList<TWEANNGenotype> tweannPop = new ArrayList<TWEANNGenotype>(pop.size());
								for(Genotype<T> g : pop) tweannPop.add(plainTWEANNGenotype ? (TWEANNGenotype) g : ((TWEANNPlusParametersGenotype) g).getTWEANNGenotype());
								EvolutionaryHistory.cleanArchetype(0, tweannPop, ea.currentIteration());
							}
						}
					}
				}
			};
		}
		
		// There is only one thread. Just run it
		if(!Parameters.parameters.booleanParameter("parallelEvaluations")) {
			System.out.println("SteadyStateExperiment with 1 thread");
			threads[0].run();
		} else {
			// Launch all the threads in parallel
			System.out.println("SteadyStateExperiment with "+numThreads+" threads");
			for(Thread t : threads) {
				t.start();
			}
			
			// Make sure all of them stop
			for(Thread t : threads) {
				try {
					t.join();
					System.out.println("Thread "+t.getId()+" joined.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Time for EA clean-up.");
		ea.finalCleanup();
	}

	@Override
	public boolean shouldStop() {
		return ea.currentIteration() >= maxIterations;
	}

	// Benchmark the parallelism using Minecraft
	public static void main(String[] args) {
		int seed = 1;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:50", "maxGens:1000",
					//"base:minecraft", "log:Minecraft-SingleTest", "saveTo:SingleTest",
					"base:minecraft", "log:Minecraft-ParallelTest", "saveTo:ParallelTest",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false",
					"io:true", "netio:true",
					"interactWithMapElitesInWorld:true",
					//"io:false", "netio:false", 
					"mating:true", "fs:false",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet",
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels", 
					"minecraftXRange:6", "minecraftYRange:6", "minecraftZRange:6", 
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator", 
					"minecraftChangeCenterOfMassFitness:true", 
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet", 
					"mating:true", "fs:false", 
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment", 
					"steadyStateIndividualsPerGeneration:100", 
					"spaceBetweenMinecraftShapes:5",
					// Parallelism
					"parallelEvaluations:true",
					"threads:10", // Only matters if parallelEvaluations is true
					"parallelMAPElitesInitialize:true", 
					
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", 
					"allowMultipleFunctions:true", 
					"ftype:0", "watch:false", "netChangeActivationRate:0.3", "cleanFrequency:-1", "recurrency:false", "saveAllChampions:true", "cleanOldNetworks:false", 
					"includeFullSigmoidFunction:true", "includeFullGaussFunction:true", "includeCosineFunction:true", "includeGaussFunction:false",
					"includeIdFunction:true", "includeTriangleWaveFunction:false", "includeSquareWaveFunction:false", "includeFullSawtoothFunction:false", 
					"includeSigmoidFunction:false", "includeAbsValFunction:false", "includeSawtoothFunction:false", 
					"minecraftNorthSouthOnly:true"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
