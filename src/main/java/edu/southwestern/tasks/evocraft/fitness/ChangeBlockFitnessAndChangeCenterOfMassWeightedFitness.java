package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.fitness.FitnessFunction;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class ChangeBlockFitnessAndChangeCenterOfMassWeightedFitness extends MinecraftWeightedSumFitnessFunction{

	public ChangeBlockFitnessAndChangeCenterOfMassWeightedFitness(List<MinecraftFitnessFunction> fitnessFunctions, List<Double> weights) {
		//		super(BlockType.FLOWING_LAVA.ordinal());
		//new BlockType[] {BlockType.COBBLESTONE, BlockType.OBSIDIAN, BlockType.STONE}
		//super(new Orientation[] {Orientation.EAST, Orientation.WEST})
		super(fitnessFunctions,weights);
		// TODO Auto-generated constructor stub
	}

//	public ChangeBlockFitnessAndChangeCenterOfMassWeightedFitness(List<MinecraftFitnessFunction> fitnessFunctions) {
//		//		super(BlockType.FLOWING_LAVA.ordinal());
//		//new BlockType[] {BlockType.COBBLESTONE, BlockType.OBSIDIAN, BlockType.STONE}
//		//super(new Orientation[] {Orientation.EAST, Orientation.WEST})
////		List<MinecraftFitnessFunction> fitnessFunctions = new ArrayList<MinecraftFitnessFunction>();
////		fitnessFunctions.add(ChangeBlocksFitness);
//		List<Double> weights;
//		weights.add(0.5);
//		weights.add(0.5);
//		List<MinecraftFitnessFunction> fitnessFunction;
//		fitnessFunction.add(ChangeBlocksFitness);
//		super(fitnessFunctions,weights);
//		// TODO Auto-generated constructor stub
//	}
}
