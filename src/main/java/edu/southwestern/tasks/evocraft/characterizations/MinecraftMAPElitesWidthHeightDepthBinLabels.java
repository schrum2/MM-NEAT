package edu.southwestern.tasks.evocraft.characterizations;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.DepthFitness;
import edu.southwestern.tasks.evocraft.fitness.HeightFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.WidthFitness;

/**
 * Minecraft shapes are categorized based on width, height, and depth.
 * @author schrum2
 *
 */
public class MinecraftMAPElitesWidthHeightDepthBinLabels extends MinecraftMAPElitesBinLabels {

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new WidthFitness(), new HeightFitness(), new DepthFitness());
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			int xDim = Parameters.parameters.integerParameter("minecraftXRange");
			int yDim = Parameters.parameters.integerParameter("minecraftYRange");
			int zDim = Parameters.parameters.integerParameter("minecraftZRange");
			
			int size = xDim*yDim*zDim;
			labels = new ArrayList<String>(size);
			for(int i = 1; i <= xDim; i++) {
				for(int j = 1; j <= yDim; j++) {
					for(int k = 1; k <= zDim; k++) {
						labels.add("W"+i+"H"+j+"D"+k);
					}
				}	
			}		
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int yDim = Parameters.parameters.integerParameter("minecraftYRange");
		int zDim = Parameters.parameters.integerParameter("minecraftZRange");
		int binIndex = multi[0]*yDim*zDim + multi[1]*zDim + multi[2];
		assert binIndex < labels.size() : binIndex + " from " + Arrays.toString(multi) + ":yDim="+yDim+":zDim="+zDim;
		return binIndex;
	}
	
	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int[] result = super.multiDimensionalIndices(keys);
		// Original results give real width,height,depth. However, values of 0 are discarded, which shifts all values over
		result[0]--;
		result[1]--;
		result[2]--;
		return result;
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("minecraftXRange"), Parameters.parameters.integerParameter("minecraftYRange"), Parameters.parameters.integerParameter("minecraftZRange")};
	}

	/**
	 * Collection of fitness functions that calculate scores to based the
	 * behavior characterization on.
	 * @return List of fitness functions for Minecraft
	 */
	@Override
	public List<MinecraftFitnessFunction> properties() {
		return properties;
	}

	@Override
	public boolean discard(HashMap<String, Object> behaviorMap) {
		// Checking one should be sufficient, but check all just in case
		Double width = (Double) behaviorMap.get("WidthFitness");
		Double height = (Double) behaviorMap.get("HeightFitness");
		Double depth = (Double) behaviorMap.get("DepthFitness");
		return width.doubleValue() == 0 ||
			   height.doubleValue() == 0 ||
			   depth.doubleValue() == 0;
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		//MMNEAT.main("runNumber:99 randomSeed:99 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:50000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-MEVectorWHD saveTo:MEVectorWHD mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels".split(" "));
		MMNEAT.main("runNumber:99 randomSeed:99 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:50000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-MECPPNWHD saveTo:MECPPNWHD mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true cleanOldNetworks:false includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:false includeSquareWaveFunction:false includeFullSawtoothFunction:false includeSigmoidFunction:false includeAbsValFunction:false includeSawtoothFunction:false".split(" "));
	}
}
