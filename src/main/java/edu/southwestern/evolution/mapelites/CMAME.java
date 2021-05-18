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
		optEmitter.setInitialX(0.05); //TODO test 0.0
		optEmitter.setInitialStandardDeviation(0.2); // unsure if should be hardcoded or not
		optEmitter.options.stopFitness = 1e-14; // unsure about value here
		int mu = Parameters.parameters.integerParameter("mu");
		optEmitter.parameters.setMu(mu);
		optEmitter.parameters.setPopulationSize(mu); 
		optEmitter.init();
		optEmitter.writeToDefaultFilesHeaders(0); // Overwrite existing CMA-ES files
	}
	
	/**
	 * Create new individuals based on set
	 * population size of Emitters, and update
	 * distribution of Emitters with fitnesses 
	 * afterwards
	 */
	public void newIndividual() {
		double[][] pop = optEmitter.samplePopulation(); // need to check feasibility?
		double[] deltaI = new double[pop.length]; // create delta array
		ArrayList<Genotype<ArrayList<Double>>> popGenotypes = PopulationUtil.genotypeArrayListFromDoubles(pop); // convert population to genotypes
		assert task.evaluate(popGenotypes.get(0)).usesMAPElitesBinSpecification() : "Cannot use a traditional behavior vector with CMA-ME";
		for (int i = 0; i < popGenotypes.size(); i++) { // for each individual
			Genotype<ArrayList<Double>> individual = popGenotypes.get(i);
			Score<ArrayList<Double>> individualScore = task.evaluate(individual); // evaluate score for individual
			boolean replacedBool = archive.add(individualScore); // attempt to add individual to archive
			
			double individualBinScore = individualScore.behaviorIndexScore(archive.getBinMapping().oneDimensionalIndex(individualScore.MAPElitesBinIndex()));
			double currentBinScore = archive.getElite(individualScore.MAPElitesBinIndex()) == null ? Double.NEGATIVE_INFINITY : archive.getElite(individualScore.MAPElitesBinIndex()).behaviorIndexScore(archive.getBinMapping().oneDimensionalIndex(individualScore.MAPElitesBinIndex()));
			
			if (currentBinScore >= individualBinScore) { // if bin was better or equal
				System.out.println("Current bin ("+currentBinScore+") was already better than or equal to new bin ("+individualBinScore+").");
			} else if (currentBinScore == Double.NEGATIVE_INFINITY) { // if bin was empty
				System.out.println("Added new bin ("+individualBinScore+").");
				deltaI[i] = -individualBinScore;
			} else { // if bin existed, but was worse than the new one
				System.out.println("Improved current bin ("+currentBinScore+"), replaced with new bin ("+individualBinScore+").");
				deltaI[i] = -(individualBinScore - currentBinScore);
			}
			
			fileUpdates(replacedBool); // log failure or success
		}
		optEmitter.updateDistribution(deltaI); // update distribution once population is done evaluating
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		System.out.println("Testing CMA-ME");
		int runNum = 1;
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" marioGANLevelChunks:10 marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME base:mariogan marioGANModel:GECCO2018GAN_World1-1_32_Epoch5000.pth GANInputSize:32 log:MarioGAN-CMAMETest saveTo:CMAMETest trials:1 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment").split(" "));
		//MMNEAT.main("runNumber:0 randomSeed:0 marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME base:mariogan marioGANModel:GECCO2018GAN_World1-1_32_Epoch5000.pth GANInputSize:32 log:MarioGAN-CMAMETest saveTo:CMAMETest trials:1 printFitness:true mu:50 maxGens:500 io:false netio:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment".split(" "));
		
	}
}
