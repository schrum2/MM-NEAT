package edu.southwestern.tasks.evocraft.fitness;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.southwestern.evolution.fitness.FitnessFunction;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.datastructures.Pair;

public abstract class MinecraftWeightedSumFitnessFunction extends TimedEvaluationMinecraftFitnessFunction{

	//a list of MinecraftFitnessFunctions, and
	//a list of doubles (weights)
	//public MinecraftWeightedSumFitnessFunction(List<FitnessFunction<T>> fitnessFunctionsLis)
	
	
	@Override
	public double calculateFinalScore(ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates corner,
			List<Block> originalBlocks) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double maxFitness() {
		// TODO Auto-generated method stub
		return 0;
	}

}
