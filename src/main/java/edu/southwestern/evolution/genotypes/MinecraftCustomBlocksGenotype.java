package edu.southwestern.evolution.genotypes;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
/**
 * 
 * @author richey2
 *
 */
public class MinecraftCustomBlocksGenotype extends TWEANNPlusParametersGenotype<ArrayList<Integer>>{

	public MinecraftCustomBlocksGenotype() {
		super(new TWEANNGenotype(), new BoundedIntegerValuedGenotype());
	}

	public static void main(String[] args) {
		int seed = 0;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:10", "maxGens:100",
					"base:minecraft", "log:Minecraft-CenterOfMass", "saveTo:CenterOfMass",
					"io:true", "netio:true", 
					//"io:false", "netio:false", 
					"mating:true", "fs:false", 
					"genotype:edu.southwestern.evolution.genotypes.MinecraftCustomBlocksGenotype",
					"launchMinecraftServerFromJava:false",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.UndroppableBlockSet",
					//"minecraftTypeCountFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					//"minecraftChangeCenterOfMassFitness:true",
					"minecraftDiversityBlockFitness:true",
					//"minecraftEvolveOrientation:true",
					//"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true",
					"minecraftXRange:1", "minecraftYRange:2", "minecraftZRange:5",
					//"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftShapeTask", "allowMultipleFunctions:true",
					"ftype:0", "watch:false", "netChangeActivationRate:0.3", "cleanFrequency:-1",
					"recurrency:false", "saveAllChampions:true", "cleanOldNetworks:false",
					"includeFullSigmoidFunction:true", "includeFullGaussFunction:true", "includeCosineFunction:true", 
					"includeGaussFunction:false", "includeIdFunction:true", "includeTriangleWaveFunction:false", 
					"includeSquareWaveFunction:false", "includeFullSawtoothFunction:false", "includeSigmoidFunction:false", 
					"includeAbsValFunction:false", "includeSawtoothFunction:false"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
