package edu.southwestern.tasks.evocraft.characterizations;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.NegativeSpaceCountFitness;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;
/**
 * Binning scheme based on block count and negative space in shapes
 * @author MuellMar
 *
 */
public class MinecraftMAPElitesBlockCountEmptyCountBinLabels extends MinecraftMAPElitesBinLabels {

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new OccupiedCountFitness(), new NegativeSpaceCountFitness());
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { 
			int xDim = Parameters.parameters.integerParameter("minecraftXRange");
			int yDim = Parameters.parameters.integerParameter("minecraftYRange");
			int zDim = Parameters.parameters.integerParameter("minecraftZRange");
			
			// everything from block count is from OccupiedCountFitness
			int sizeBlockCount = xDim*yDim*zDim; 
			int sizeNegativeSpace = xDim*yDim*zDim-1; // Max possible negative space
			labels = new ArrayList<String>(sizeBlockCount*sizeNegativeSpace);
			
			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities , -1 for negative space(j < size would just give a range of 0-999)
			for(int i = 0; i < sizeBlockCount; i++) {
				for(int j = 0; j < sizeNegativeSpace; j++)
				labels.add("BlockCount"+i+"NegativeSpace"+j); 
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) { // Based on 2d archive
		multi[1]++; // Needs to be done so no negative indexes
		int binIndex = (multi[0])*dimensionSizes()[1] + multi[1];
		return binIndex;
	}
	
	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int[] result = super.multiDimensionalIndices(keys);
		// Actual block count could be 0, but such shapes are discarded, only for block Count
		result[0]--;
		return result;
	}

	@Override
	public int[] dimensionSizes() {
		int xtimesYtimez = Parameters.parameters.integerParameter("minecraftXRange")*Parameters.parameters.integerParameter("minecraftYRange")*Parameters.parameters.integerParameter("minecraftZRange");
		return new int[] {xtimesYtimez,xtimesYtimez-1}; // Makes sure no empty fences are placed
	}

	@Override
	public List<MinecraftFitnessFunction> properties() {
		return properties;
	}
	
	@Override
	public boolean discard(HashMap<String, Object> behaviorMap) {
		return ((Double) behaviorMap.get("OccupiedCountFitness")).doubleValue() == 0; // IF empty, discards it (mostly first row of blockCount
	}

	public static void main(String[] args) {
		int seed = 1;
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:100", "maxGens:100000",
					"base:minecraft", "log:Minecraft-MAPElitesCountEmptyFlyingMachineVectorNS", "saveTo:MAPElitesCountEmptyFlyingMachineVectorNS",
					"minecraftContainsWholeMAPElitesArchive:true","forceLinearArchiveLayoutInMinecraft:false",
					"launchMinecraftServerFromJava:false",
					"io:true", "netio:true",
					"interactWithMapElitesInWorld:true",
					//"io:false", "netio:false", 
					"mating:true", "fs:false",
					"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet",
					//"minecraftTypeCountFitness:true",
					//"minecraftDiversityBlockFitness:true",
					"minecraftChangeCenterOfMassFitness:true",
					//"minecraftTypeTargetFitness:true", 
					//"minecraftDesiredBlockCount:40",
					//"minecraftOccupiedCountFitness:true",
					"minecraftEvolveOrientation:true",
					"minecraftNorthSouthOnly:true",
					//"minecraftRedirectConfinedSnakes:true",
					//"minecraftStopConfinedSnakes:true", 
					//"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels",
					"mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountEmptyCountBinLabels",
					"ea:edu.southwestern.evolution.mapelites.MAPElites", 
					"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
					"steadyStateIndividualsPerGeneration:100", 
					//FOR TESTING
					"spaceBetweenMinecraftShapes:8","parallelMAPElitesInitialize:false",
					"minecraftXRange:2","minecraftYRange:2","minecraftZRange:5",
					//"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator",
					"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator",
					"vectorPresenceThresholdForEachBlock:true",
					"voxelExpressionThreshold:0.5",
					"genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype",
					"netChangeActivationRate:0.0",
					
					"task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask", "allowMultipleFunctions:true",
					"ftype:0", "watch:false", "cleanFrequency:-1",
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
