package edu.southwestern.tasks.evocraft.shapegeneration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;

public class VectorToVolumeGenerator implements ShapeGenerator<ArrayList<Double>> {

	@Override
	public List<Block> generateShape(Genotype<ArrayList<Double>> genome, MinecraftCoordinates corner,
			BlockSet blockSet) {
	
//		System.out.println("fine here.");
		
		List<Block> blocks = new ArrayList<>();
		
//		System.out.println("new list blocks was created.");
		
		// TODO: make double list/array to correspond to every block in the block list
		List<Double> doubles = new ArrayList<>();
		for(double d = 0.0; d < blockSet.getPossibleBlocks().length; d++) {
			doubles.add(d); // add the possible double values from the block list (0 to block list length)
		}
		// index of counter of list for genome.getPhenotype() will determine block
//		System.out.println("doubles array was also made");
		
		//System.out.println("genotype position 0: " + genome.getPhenotype().get(0).intValue());
		ArrayList<Double> phenotype = null;
		try {
			phenotype = genome.getPhenotype();	
		} catch(Exception e) {
			System.out.println("genome.getPhenotype() caused crash");
			System.exit(1);
		}
		
		System.out.println("phenotype looks like: " + phenotype.size());
		//boolean distanceInEachPlane = Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane");
		MinecraftCoordinates ranges = new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
		Orientation blockOrientation = Orientation.NORTH;
		int counter= 0; // used to count the number of blocks added
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					// intValue is used to cast from Double
					int blockTypeIndex = phenotype.get(counter).intValue(); // blockType is index used to determine blocktype
					Block b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[blockTypeIndex], blockOrientation);
					blocks.add(b);
					System.out.println("added block of block type " + blockSet.getPossibleBlocks()[1]);
					System.out.println("counter: " + counter);
					
					counter++;
				}
			}
		}
		
		
		// TODO: block is equal to the value of double list truncated down to an int (this would match the block list)
		
		// TODO: implicitly allow AIR as the next option beyond the final one ( this would likely be length)
		
		System.out.println("Size of blocks: " + blocks.size());
		return blocks;
			
	}

	@Override
	public String[] getNetworkOutputLabels() {
		// TODO Auto-generated method stub
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
					//"minecraftTypeCountFitness:true",
					"minecraftDiversityBlockFitness:true",
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
					"minecraftXRange:1","minecraftYRange:2","minecraftZRange:5",
					"minecraftStopConfinedSnakes:true",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator",
					//"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator",
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", "allowMultipleFunctions:true",
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
