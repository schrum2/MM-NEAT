package edu.southwestern.tasks.evocraft.fitness;

import java.util.Arrays;

public class WeightedSumsTypeCountAndNegativeSpaceCountFitness extends MinecraftWeightedSumFitnessFunction{

	public WeightedSumsTypeCountAndNegativeSpaceCountFitness() {
		super(Arrays.asList(new MinecraftFitnessFunction[] {new TypeCountFitness(), new NegativeSpaceCountFitness()}),Arrays.asList(new Double[] {0.5,0.5}));
	}
	
//	public WeightedSumsTypeCountAndNegativeSpaceCountFitness(List<MinecraftFitnessFunction> fitnessFunctions,
//			List<Double> weights) {
//		super(fitnessFunctions, weights);
//		// TODO Auto-generated constructor stub
//	}

	@Override
	public boolean needsSimulation() {
		return false;	//for both
	}

}
