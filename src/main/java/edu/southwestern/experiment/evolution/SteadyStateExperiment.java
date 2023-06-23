package edu.southwestern.experiment.evolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNPlusParametersGenotype;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.evolution.mome.MOME;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.file.FileUtilities;

/**
 * An experiment that involves a gradually changing state, such as a population
 * that gets one individual added at a time as opposed to having the whole
 * population replaced each generation. The main example of this experiment
 * type is MAP Elites and its variants.
 * 
 * @author Jacob Schrum
 *
 * @param <T>
 */
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
		
		// If we are evolving Minecraft shapes, then save each shape in the archive/population as a block list at the end
		if(MMNEAT.task instanceof MinecraftLonerShapeTask) {
			// TODO: https://github.com/schrum2/MM-NEAT/issues/911
			if(Parameters.parameters.booleanParameter("saveWholeMinecraftArchiveAtEnd")) {
				
				
				String saveDir = FileUtilities.getSaveDirectory() + "/finalArchiveOfShapes";
				File dir = new File(saveDir);
				// Create dir	-is this create directory or creating a text file?
				if (!dir.exists()) {
					dir.mkdir();
				}
				
				if(MMNEAT.ea instanceof MAPElites) {
					@SuppressWarnings("unchecked")
					Vector<Score<T>> archive = ((MAPElites<T>) MMNEAT.ea).getArchive().getArchive();
					
					// loop
					//		Convert score.individual using MMNEAT.shapeGenerator
					//		MinecraftUtilClass.writeBlockListFile(originalBlocks, saveDir + File.separator + "Shape"+(++savedShapes), "FITNESS_"+finalResults[i]+".txt");
					//		Put score.scores and also 
					MinecraftCoordinates corner = new MinecraftCoordinates(0, 0, 0);
					for(int i = 0; archive.size() > i; i ++ ) {
						Score<T> score = archive.get(i);
						if(score != null) {
							Genotype<T> individual = score.individual;
							@SuppressWarnings("unchecked")
							List<MinecraftClient.Block> blocks = MMNEAT.shapeGenerator.generateShape(individual, corner, MMNEAT.blockSet);

							BinLabels archiveBinLabelsClass = MMNEAT.getArchiveBinLabelsClass();
							String label = archiveBinLabelsClass.binLabels().get(archiveBinLabelsClass.oneDimensionalIndex(score.MAPElitesBehaviorMap()));

							MinecraftUtilClass.writeBlockListFile(blocks, saveDir + File.separator + individual.getId(), "BC_"+label+"FITNESS_"+Arrays.toString(score.scores)+".txt");
						}
					}
				} else if(MMNEAT.ea instanceof MOME) {
					throw new UnsupportedOperationException("MOME cannot save final archive yet");
				}
			}
		}
	}

	@Override
	public boolean shouldStop() {
		return ea.currentIteration() >= maxIterations;
	}

	// Benchmark the parallelism using Minecraft
	public static void main(String[] args) {
		try {
			MMNEAT.main("runNumber:77 randomSeed:77 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:10 maxGens:5 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:testing log:TESTING-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" ")); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
