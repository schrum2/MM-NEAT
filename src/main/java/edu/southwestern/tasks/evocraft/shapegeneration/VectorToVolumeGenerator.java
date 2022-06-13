package edu.southwestern.tasks.evocraft.shapegeneration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cern.colt.Arrays;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;

public class VectorToVolumeGenerator implements ShapeGenerator<ArrayList<Double>> {
	@Override
	public List<Block> generateShape(Genotype<ArrayList<Double>> genome, MinecraftCoordinates corner,
			BlockSet blockSet) {
		
		List<Block> blocks = new ArrayList<>();
		
		List<Double> doubles = new ArrayList<>();
		for(double d = 0.0; d < blockSet.getPossibleBlocks().length; d++) {
			doubles.add(d); // add the possible double values from the block list (0 to block list length)
		}
		
		ArrayList<Double> phenotype = genome.getPhenotype();	
		MinecraftCoordinates ranges = new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
		
		Orientation blockOrientation = Orientation.NORTH; // all blocks will have orientation of north for now
		int counter= 0; // used to count the number of blocks added
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					// intValue is used to cast from Double
					int blockTypeIndex = (int)(phenotype.get(counter)*(blockSet.getPossibleBlocks().length+1)); // blockType is index used to determine blocktype					
					
					Block b = null;
					// if the block is the last index in the list, then it is an AIRBLOCK, otherwise, it can be any other block in the list.
					if(blockTypeIndex == blockSet.getPossibleBlocks().length) b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), BlockType.AIR, blockOrientation);
					else b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], blockOrientation);
					
					blocks.add(b);
					//System.out.println(Arrays.toString(blockSet.getPossibleBlocks()));
					//System.out.println(b);
					
					counter++;
				}
			}
		}
		return blocks;	
	}

	@Override
	public String[] getNetworkOutputLabels() {
		return ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet);
	}
	
	public static void main(String[] args) {
		int seed = 0;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:100", "maxGens:100000",
					"base:minecraft", "log:Minecraft-VectorToVolumeTest", "saveTo:VectorToVolumeTest",
					"io:true", "netio:true", 
					"launchMinecraftServerFromJava:false",
					//"io:false", "netio:false", 
					"mating:true", "fs:false", 
					"minecraftContainsWholeMAPElitesArchive:true",
					"genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype",
					//"minecraftTypeCountFitness:true",
					//"minecraftDiversityBlockFitness:true",
					"minecraftChangeCenterOfMassFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					//"minecraftEvolveOrientation:true",
					//"minecraftRedirectConfinedSnakes:true",
					"minecraftStopConfinedSnakes:true",
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100",
					"minecraftXRange:2","minecraftYRange:2","minecraftZRange:5",
					"minecraftStopConfinedSnakes:true",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", 
					"watch:false", "cleanFrequency:-1", "saveAllChampions:true"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
