package edu.southwestern.evolution.mapelites;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.emitters.*;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;

public class CMAME extends MAPElites<ArrayList<Double>> {
	
	private Emitter[] emitters; // array holding all emitters
	public static final boolean PRINT_DEBUG = false; // prints out debug text if true (applies to both this class and emitter classes)
	public static final double FAILURE_VALUE = Double.MAX_VALUE;
	private int totalEmitters;
	private int emitterCounter = 0;
	
	public void initialize(Genotype<ArrayList<Double>> example) {
		super.initialize(example);
		int dimension = MMNEAT.getLowerBounds().length;
		int numImprovementEmitters = Parameters.parameters.integerParameter("numImprovementEmitters");
		int numOptimizingEmitters = Parameters.parameters.integerParameter("numOptimizingEmitters");
		totalEmitters = numImprovementEmitters+numOptimizingEmitters;
		emitters = new Emitter[totalEmitters];
		int place = 0; // remember position in emitter array
		for (int i = 0; i < numImprovementEmitters; i++) {
			emitters[i] = new ImprovementEmitter(dimension, archive, i+1); // create improvement emitters
			place++;
		}
		for (int i = 0; i < numOptimizingEmitters; i++) {
			emitters[place+i] = new OptimizingEmitter(dimension, archive, i+1); // create optimizing emitters
		}
	}
	
	/**
	 * Create new individuals based on set
	 * population size of Emitters, and update
	 * distribution of Emitters with fitnesses 
	 * afterwards
	 */
	public void newIndividual() {
		incrementEmitterCounter();
		Emitter thisEmitter = emitters[emitterCounter]; // pick the lowest one
		double[] rawIndividual = thisEmitter.sampleSingle();
		Genotype<ArrayList<Double>> individual = new BoundedRealValuedGenotype(rawIndividual);
		
		Score<ArrayList<Double>> individualScore = task.evaluate(individual); // evaluate score for individual
		assert individualScore.usesMAPElitesBinSpecification() : "Cannot use a traditional behavior vector with CMA-ME";
		
		double individualBinScore = individualScore.behaviorIndexScore(); // extract new bin score
		Score<ArrayList<Double>> currentOccupant = archive.getElite(individualScore.MAPElitesBinIndex());
		double currentBinScore = currentOccupant == null ? Double.NEGATIVE_INFINITY : currentOccupant.behaviorIndexScore(); // extract current bin score

		thisEmitter.addFitness(rawIndividual, individualBinScore, currentBinScore, archive); // potentially add new fitness
		
		boolean replacedBool = archive.add(individualScore); // attempt to add individual to archive
		
		if (PRINT_DEBUG) {System.out.println("Emitter: \""+thisEmitter.emitterName+"\"\tSolutions: "+thisEmitter.solutionCount+"\t\tAmount of Parents: "+thisEmitter.additionCounter);}
		fileUpdates(replacedBool); // log failure or success
	}	

	/**
	 * Switches to the next emitter until all are processed, then
	 * loops back to the first emitter.
	 */
	private void incrementEmitterCounter() {
		emitterCounter = (emitterCounter + 1) % totalEmitters;
	}
	
	// Test CMA-ME
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		System.out.println("Testing CMA-ME");
		int runNum = 0;
		// MarioGAN test
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" marioGANLevelChunks:10 marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME base:mariogan marioGANModel:GECCO2018GAN_World1-1_32_Epoch5000.pth GANInputSize:32 log:MarioGAN-CMAMETest saveTo:CMAMETest trials:1 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment").split(" "));
		// Rastrigin test: 500 bin 		20 solution vector 
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" io:true mu:50 lambda:50 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-CMAMEFunctionOptimization saveTo:CMAMEFunctionOptimization netio:false maxGens:50000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		// Rastrigin test: 50 bin 		20 solution vector 		10000 gens
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" io:true numImprovementEmitters:2 numOptimizingEmitters:2 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-CMAMEFunctionOptimization saveTo:CMAMEFunctionOptimization netio:false maxGens:20000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:50 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
	}
	
	
}
