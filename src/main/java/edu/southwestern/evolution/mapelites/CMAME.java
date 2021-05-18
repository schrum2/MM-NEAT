package edu.southwestern.evolution.mapelites;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.random.RandomNumbers;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class CMAME extends MAPElites<ArrayList<Double>> {
	
	public CMAEvolutionStrategy optEmitter = new CMAEvolutionStrategy();
	
	public void initialize(Genotype<ArrayList<Double>> example) {
		super.initialize(example);
		optEmitter.setDimension(example.getPhenotype().size());
		optEmitter.setInitialX(0.05);
		optEmitter.setInitialStandardDeviation(0.2); // unsure hardcoded/not
		optEmitter.options.stopFitness = 1e-14;
		int mu = Parameters.parameters.integerParameter("mu");
		optEmitter.parameters.setMu(mu);
		optEmitter.parameters.setPopulationSize(mu); // lambda?
		optEmitter.init();
		optEmitter.writeToDefaultFilesHeaders(0);
	}
	
	public void newIndividual() {
		double[][] pop = optEmitter.samplePopulation(); // need to check feasibility?
		double[] deltaI = new double[pop.length]; // create delta array
		ArrayList<Genotype<ArrayList<Double>>> popGenotypes = PopulationUtil.genotypeArrayListFromDoubles(pop); // convert population to genotypes
		assert task.evaluate(popGenotypes.get(0)).usesMAPElitesBinSpecification() : "Cannot use a traditional behavior vector with CMA-ME";
		for (int i = 0; i < popGenotypes.size(); i++) { // for each individual
			Genotype<ArrayList<Double>> individual = popGenotypes.get(i);
			Score<ArrayList<Double>> individualScore = task.evaluate(individual); // evaluate score for individual
			int[] BinIndices = individualScore.MAPElitesBinIndex(); // get indices for bins
			Score<ArrayList<Double>> currentBinOccupant = archive.getElite(BinIndices); // get current elite of current bin
			int binIndex = archive.getBinMapping().oneDimensionalIndex(BinIndices); // get index of individual
			double individualBinScore = individualScore.behaviorIndexScore(binIndex); // get score of in
			double currentBinScore = currentBinOccupant == null ? Double.NEGATIVE_INFINITY : currentBinOccupant.behaviorIndexScore(binIndex);
			if (currentBinOccupant == null) { // empty bin
				System.out.println("Added bin"+individualBinScore);
				deltaI[i] = -individualBinScore; // TODO negate these?
				fileUpdates(true);
				archive.archive.set(binIndex, individualScore.copy());
				synchronized(this) {
					archive.occupiedBins++; // Shared variable
				}
				archive.conditionalEliteSave(individualScore, binIndex);
				EvolutionaryHistory.logLineageData(individual.getId(), individual);
			} else if (individualBinScore > currentBinScore) { // existing, but worse bin
				System.out.println("Improved bin "+currentBinScore+", replaced with "+individualBinScore);
				deltaI[i] = -(individualBinScore - currentBinScore);
				fileUpdates(true);
				archive.archive.set(binIndex, individualScore.copy());
				archive.conditionalEliteSave(individualScore, binIndex);
			} else {
				System.out.println("Bin "+currentBinScore+" was already better than or equal to "+individualBinScore);
				fileUpdates(false); // log failure of elite
			}
			
		}
		optEmitter.updateDistribution(deltaI); // perhaps 
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		System.out.println("Testing CMA-ME");
		int runNum = 1;
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" marioGANLevelChunks:10 marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME base:mariogan marioGANModel:GECCO2018GAN_World1-1_32_Epoch5000.pth GANInputSize:32 log:MarioGAN-CMAMETest saveTo:CMAMETest trials:1 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment").split(" "));
		//MMNEAT.main("runNumber:0 randomSeed:0 marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME base:mariogan marioGANModel:GECCO2018GAN_World1-1_32_Epoch5000.pth GANInputSize:32 log:MarioGAN-CMAMETest saveTo:CMAMETest trials:1 printFitness:true mu:50 maxGens:500 io:false netio:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment".split(" "));
		
	}
}
