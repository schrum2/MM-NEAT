package edu.southwestern.evolution.mapelites;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class CMAME extends MAPElites<ArrayList<Double>> {
	
	private int emitterCount = 1; // TODO make number of emitters not hard coded
	private Emitter[] emitters = new Emitter[emitterCount]; // array of emitters
	private double[][] parentPopulation;
	private double[] deltaI;
	private int additionCounter = 0;
	private boolean printDebug = true; // prints out debug text if true
	
	public void initialize(Genotype<ArrayList<Double>> example) {
		super.initialize(example);
		int dimension = MMNEAT.getLowerBounds().length;
		for (int i = 0; i < emitterCount; i++) {
			emitters[i] = newEmitter(dimension, "Emitter "+(i+1));
		}
		int populationSize = Parameters.parameters.integerParameter("mu");
		parentPopulation = new double[populationSize][dimension]; // TODO should be independent for each emitter
		deltaI = new double[populationSize]; // TODO should be independent for each emitter
	}
	
	/**
	 * Create new individuals based on set
	 * population size of Emitters, and update
	 * distribution of Emitters with fitnesses 
	 * afterwards
	 */
	public void newIndividual() {
		Arrays.sort(emitters); // sort emitters by their solution counts
		Emitter thisEmitter = emitters[0]; // pick the lowest one
		double[] rawIndividual = thisEmitter.sampleSingle();

		Genotype<ArrayList<Double>> individual = new BoundedRealValuedGenotype(rawIndividual);
		
		Score<ArrayList<Double>> individualScore = task.evaluate(individual); // evaluate score for individual
		assert individualScore.usesMAPElitesBinSpecification() : "Cannot use a traditional behavior vector with CMA-ME";
		
		double individualBinScore = individualScore.behaviorIndexScore(); // TODO make new variable names that are better and don't all sound the same
		Score<ArrayList<Double>> currentOccupant = archive.getElite(individualScore.MAPElitesBinIndex());
		double currentBinScore = currentOccupant == null ? Double.NEGATIVE_INFINITY : currentOccupant.behaviorIndexScore();

		if (currentBinScore >= individualBinScore) { // if bin was better or equal
			if (printDebug) {System.out.println("Current bin ("+currentBinScore+") was already better than or equal to new bin ("+individualBinScore+").");}
			deltaI[additionCounter] = 0; // Schrum: I don't like this, but we may have to do it
		} else if (Double.isInfinite(currentBinScore)) { // if bin was empty (infinite magnitude must be negative infinity)
			if (printDebug) {System.out.println("Added new bin ("+individualBinScore+").");}
			thisEmitter.solutionCount++; // TODO should change how these if statements are laid out so this doesn't have to be written twice
			deltaI[additionCounter] = -individualBinScore; // Negate score because CMA-ES is a minimizer
		} else { // if bin existed, but was worse than the new one
			if (printDebug) {System.out.println("Improved current bin ("+currentBinScore+") with new bin ("+individualBinScore+")");}
			thisEmitter.solutionCount++;
			deltaI[additionCounter] = -(individualBinScore - currentBinScore);	// Negate difference score because CMA-ES is a minimizer	
		}
		parentPopulation[additionCounter] = rawIndividual;
		additionCounter++; // TODO should be independent for each emitter
		boolean replacedBool = archive.add(individualScore); // attempt to add individual to archive
		
		if (printDebug) {System.out.println("Emitter: \""+thisEmitter.emitterTag+"\"\tSolutions: "+thisEmitter.solutionCount+"\t\tAmount of Parents: "+additionCounter);}
		fileUpdates(replacedBool); // log failure or success
		
		if (additionCounter == Parameters.parameters.integerParameter("mu")) { // TODO should be independent for each emitter
			for (Emitter e : emitters) {
				e.updateDistribution(parentPopulation, deltaI); // TODO should be independent for each emitter // Update distribution with new	parents and fitness values	
			}
			additionCounter = 0; // TODO should be independent for each emitter
		}
	}

	/**
	 * Create a new emitter with the default parameters, 
	 * and a provided name
	 * 
	 * @param dimension Dimension of the emitter
	 * @param tag Name corresponding to this emitter
	 * @return An initalized Emitter with the provided name
	 */
	private Emitter newEmitter(int dimension, String tag) { // TODO move this inside the emitters themselves? Kinda stupid to not just have "new Emitter(...)"
		CMAEvolutionStrategy optEmitter = new CMAEvolutionStrategy();
		optEmitter.setDimension(dimension);
		optEmitter.setInitialX(0.0);
		optEmitter.setInitialStandardDeviation(0.5); // TODO unsure if should be hardcoded or not
		int lambda = Parameters.parameters.integerParameter("lambda"); // TODO what's the deal with mu and lambda???
		optEmitter.parameters.setMu(lambda);
		optEmitter.parameters.setPopulationSize(lambda); // not sure about mu/lambda is mu appropriate?
		optEmitter.init();
		optEmitter.writeToDefaultFilesHeaders(0); // Overwrite existing CMA-ES files
		return new Emitter(optEmitter, tag);
	}
	
	
	private class Emitter implements Comparable<Emitter> { // TODO make more generic for different emitter types
		int solutionCount = 0; // amount of solutions found by this emitter
		int populationCounter; // counter for getting the next solution from the population
		int additionCounter; // amount of parents currently stored, to be used in distribution update
		double[][] parentPopulation = null; // stored parents
		double[][] sampledPopulation = null; // current population to pull individuals from
		String emitterTag; // name of the emitter
		CMAEvolutionStrategy CMAESInstance; // internal instance of CMA-ES 
		
		// Create the emitter with a CMAEvolutionStrategy instance and "tag" name
		public Emitter(CMAEvolutionStrategy CMAESInstance, String tag) {
			this.CMAESInstance = CMAESInstance;
			this.emitterTag = tag; // TODO change "tag" to "name" or something
		}
		
		public void updateDistribution(double[][] parentPopulation2, double[] deltaI) {
			CMAESInstance.updateDistribution(parentPopulation2, deltaI); // TODO change to work with internal data
			
		}

		// get the population from the internal CMA-ES instance
		private double[][] samplePopulation() {
			return CMAESInstance.samplePopulation();
		}
		
		// comparable through solution count
		@Override
		public int compareTo(Emitter other) {
			return this.solutionCount - other.solutionCount;
		}
		
		public void resetSample() {
			sampledPopulation = this.samplePopulation();
			populationCounter = 0;
		}
		
		public double[] sampleSingle() {
			if (sampledPopulation == null || (sampledPopulation.length-1) < populationCounter) { // at start or when counter is above
				resetSample(); // TODO setup parentPopulation and additionCounter etc.
			}
			double[] newIndividual = sampledPopulation[populationCounter];
			populationCounter++;
			return newIndividual; 
		}
		
	}
	
	// Test CMA-ME
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		System.out.println("Testing CMA-ME");
		int runNum = 0;
		// MarioGAN test
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" marioGANLevelChunks:10 marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME base:mariogan marioGANModel:GECCO2018GAN_World1-1_32_Epoch5000.pth GANInputSize:32 log:MarioGAN-CMAMETest saveTo:CMAMETest trials:1 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment").split(" "));
		// Rastrigin test: 500 bin 		20 solution vector 
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" io:true mu:50 lambda:50 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-CMAMEFunctionOptimization saveTo:CMAMEFunctionOptimization netio:false maxGens:50000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
		// TODO switch to (smaller) faster settings and test with normal MAPElites to compare
	}
}
