package edu.southwestern.tasks.evocraft.fitness;

import java.util.Arrays;

public class WeightedSumsAccumulateNewBlockPositionsAndChangeCenterOfMassFitness extends MinecraftWeightedSumFitnessFunction{

	public WeightedSumsAccumulateNewBlockPositionsAndChangeCenterOfMassFitness() {
		super(Arrays.asList(new MinecraftFitnessFunction[] {new AccumulateNewBlockPositionsFitness(), new ChangeCenterOfMassFitness()}),Arrays.asList(new Double[] {0.5,0.5}));
	}

	@Override
	public boolean needsSimulation() {
		return true; // for both
	}

}
