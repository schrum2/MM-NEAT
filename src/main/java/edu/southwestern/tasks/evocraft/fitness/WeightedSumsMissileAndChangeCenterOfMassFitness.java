package edu.southwestern.tasks.evocraft.fitness;

import java.util.Arrays;

/**
 * For evolving missiles and flying at the same time
 * @author schrum2
 *
 */
public class WeightedSumsMissileAndChangeCenterOfMassFitness extends MinecraftWeightedSumFitnessFunction {

	public WeightedSumsMissileAndChangeCenterOfMassFitness() {
		super(Arrays.asList(new MinecraftFitnessFunction[] {new MissileFitness(), new ChangeCenterOfMassFitness()}),Arrays.asList(new Double[] {0.5,0.5}));
	}

	@Override
	public boolean needsSimulation() {
		return true; // for both
	}
}
