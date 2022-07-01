package edu.southwestern.tasks.evocraft.shapegeneration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.CPPNOrBlockVectorGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;

/**
 * This shape generator uses either CPPN or VectorToVolume generation
 * depending on the type/form of the genotype.
 * 
 * @author Alejandro Medina
 *
 */
@SuppressWarnings("rawtypes")
public class CPPNOrVectorToVolumeGenerator implements ShapeGenerator, BoundedVectorGenerator {

	private VectorToVolumeGenerator forVectors;
	private ThreeDimensionalVolumeGenerator forCPPN;
	
	public CPPNOrVectorToVolumeGenerator() {
		forVectors = new VectorToVolumeGenerator(); // instance of VectorToVolumeGenerator
		forCPPN = new ThreeDimensionalVolumeGenerator(); // instance of ThreeDimensionalVolumeGenerator
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Block> generateShape(Genotype genome, MinecraftCoordinates corner, BlockSet blockSet) {
		
		List<Block> blocks;
		CPPNOrBlockVectorGenotype either = (CPPNOrBlockVectorGenotype) genome;
		if(either.getFirstForm()) { // first form is CPPN, use ThreeDimensionalVolumeGenerator
			assert either.getFirstForm();
			Genotype<TWEANN> first =  either.getCurrentGenotype();
			blocks = forCPPN.generateShape((Genotype) first, corner, blockSet);
		} else { // second form is Vector, use VectorToVolumeGenerator
			assert !either.getFirstForm();
			Genotype<ArrayList<Double>> second = either.getCurrentGenotype();
			blocks = forVectors.generateShape(second, corner, blockSet);
		}
		return blocks;
	}

	@Override
	public String[] getNetworkOutputLabels() {
		return ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet);
	}

	public static void main(String[] args) {
		int seed = 67;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:20", "maxGens:150",
					"base:minecraft", "log:Minecraft-TestingCPPNtoVector", "saveTo:TestingCPPNtoVector",
					"io:true", "netio:true", 
					//"io:false", "netio:false", 
					"mating:true", "fs:false", 
					//"startX:-10", "startY:15", "startZ:10",
					//"displayDiagonally:false",
					
					"genotype:edu.southwestern.evolution.genotypes.CPPNOrBlockVectorGenotype",
					//"genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype",
					
					
					//"indirectToDirectTransitionRate:0.0", // Testing with No conversion, but comment this out later
					
					
					"oneOutputLabelForBlockTypeCPPN:true",
					
					"oneOutputLabelForBlockOrientationCPPN:true",
					
					"vectorPresenceThresholdForEachBlock:true",
					//"vectorPresenceThresholdForEachBlock:false",
					
					"voxelExpressionThreshold:0.5",
					"launchMinecraftServerFromJava:false",
					//"minecraftTypeCountFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					//"minecraftChangeCenterOfMassFitness:true",
					//"NegativeSpaceCountFitness:true",
					"minecraftChangeCenterOfMassFitness:true",
					"minecraftEndEvalNoMovement:true",
					"shortTimeBetweenMinecraftReads:" + 500L,
					"minecraftAccumulateChangeInCenterOfMass:true",
					//"minecraftDiversityBlockFitness:true",
					//"minecraftEvolveOrientation:true",
					//"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true",
					"minecraftXRange:2", "minecraftYRange:2", "minecraftZRange:4",
					//"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator",
					
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.CPPNOrVectorToVolumeGenerator",
					
					
					"minecraftContainsWholeMAPElitesArchive:true",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesNorthSouthPistonCountBinLabels",
					
					
					//"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator",
				
					"task:edu.southwestern.tasks.evocraft.MinecraftShapeTask", 
					
					"allowMultipleFunctions:true",
					"ftype:0", "watch:false", "netChangeActivationRate:0.0", "cleanFrequency:-1",
					"recurrency:false", "saveAllChampions:true", "cleanOldNetworks:false",
					"includeFullSigmoidFunction:true", "includeFullGaussFunction:true", "includeCosineFunction:true", 
					"includeGaussFunction:false", "includeIdFunction:true", "includeTriangleWaveFunction:false", 
					"includeSquareWaveFunction:false", "includeFullSawtoothFunction:false", "includeSigmoidFunction:false",
					//"extraSpaceBetweenMinecraftShapes:0",
					"includeAbsValFunction:false", "includeSawtoothFunction:false"}); 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double[] getLowerBounds() {
		return forVectors.getLowerBounds();
	}

	@Override
	public double[] getUpperBounds() {
		return forVectors.getUpperBounds();
	}
}
