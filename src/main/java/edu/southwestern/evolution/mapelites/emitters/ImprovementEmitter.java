package edu.southwestern.evolution.mapelites.emitters;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.parameters.Parameters;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class ImprovementEmitter extends Emitter {

	public ImprovementEmitter(int dimension, Archive<ArrayList<Double>> archive, int id) {
		super(dimension, archive, id);
	}
	
	protected CMAEvolutionStrategy newCMAESInstance(Archive<ArrayList<Double>> archive) {
		CMAEvolutionStrategy optEmitter = new CMAEvolutionStrategy();
		optEmitter.setDimension(dimension);
		Genotype<ArrayList<Double>> elite = archive.getElite(archive.randomOccupiedBinIndex()).individual;
		Double[] phenoD = elite.getPhenotype().toArray(new Double[0]);
		double[] phenod = new double[phenoD.length];
		for (int i = 0; i < phenoD.length; i++) {
			phenod[i] = phenoD[i];
		}
		optEmitter.setInitialX(phenod); // start at random bin
		optEmitter.setInitialStandardDeviation(0.5); // TODO unsure if should be hardcoded or not
		int lambda = Parameters.parameters.integerParameter("lambda"); // TODO what's the deal with mu and lambda???
		optEmitter.parameters.setMu(lambda);
		optEmitter.parameters.setPopulationSize(lambda); // not sure about mu/lambda is mu appropriate?
		optEmitter.init();
		optEmitter.writeToDefaultFilesHeaders(0); // Overwrite existing CMA-ES files
		return optEmitter;
	}
	
	public void addFitness(double[] parent, double fitness, Archive<ArrayList<Double>> archive) {
		deltaIFitnesses[additionCounter] = fitness;
		parentPopulation[additionCounter] = parent;
		additionCounter++;
		if (additionCounter == populationSize) {
			if (allInvalid()) {
				this.CMAESInstance = newCMAESInstance(archive);
			} else {
				updateDistribution(parentPopulation, deltaIFitnesses);
			}
			additionCounter = 0;
		}
	}

	@Override
	protected String getEmitterSuffix() {
		return "Improvement";
	}
}
