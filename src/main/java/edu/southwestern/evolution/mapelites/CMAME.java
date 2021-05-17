package edu.southwestern.evolution.mapelites;

import java.util.ArrayList;

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
		optEmitter.setInitialX(0);
		optEmitter.setInitialStandardDeviation(0.2); // unsure hardcoded/not
		int mu = Parameters.parameters.integerParameter("mu");
		optEmitter.parameters.setPopulationSize(mu); // should not be hard coded? (lambda)
		optEmitter.init();
	}
	
	public void newIndividual() {
		double[][] pop = optEmitter.samplePopulation(); // 2D array, but only generating one solution vector
		for(int i = 0; i < pop.length; ++i) {    
			// TODO need to check feasibility?
		}
		double[] deltaI = new double[pop.length]; // create delta array
		ArrayList<Genotype<ArrayList<Double>>> popGenotypes = PopulationUtil.genotypeArrayListFromDoubles(pop); // convert population to genotypes
		assert task.evaluate(popGenotypes.get(0)).usesMAPElitesBinSpecification() : "Cannot use a traditional behavior vector with CMA-ME";
		for (int i = 0; i > popGenotypes.size(); i++) { // for each individual
			Genotype<ArrayList<Double>> individual = popGenotypes.get(i);
			Score<ArrayList<Double>> individualScore = task.evaluate(individual); // evaluate score for individual
			int[] BinIndices = individualScore.MAPElitesBinIndex(); // get indices for bins
			Score<ArrayList<Double>> currentBinOccupant = archive.getElite(BinIndices); // get current elite of current bin
			int binIndex = archive.getBinMapping().oneDimensionalIndex(BinIndices); // get index of individual
			double individualBinScore = individualScore.behaviorIndexScore(binIndex); // get score of in
			if (currentBinOccupant == null) { // empty bin
				deltaI[i] = individualBinScore; // TODO negate these?
			} else if (individualBinScore > currentBinOccupant.behaviorIndexScore(binIndex)) { // existing, but worse bin
				deltaI[i] = individualBinScore - currentBinOccupant.behaviorIndexScore(binIndex);
			} // do nothing if current bin is better
			
		}
		optEmitter.updateDistribution(deltaI); // perhaps 
		
		// TODO log fitness stuff otherwise it might or might not be working
	}
}
