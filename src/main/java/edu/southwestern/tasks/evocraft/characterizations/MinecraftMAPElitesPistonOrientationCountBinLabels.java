package edu.southwestern.tasks.evocraft.characterizations;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.EastWestPistonCountFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.NorthSouthPistonCountFitness;
import edu.southwestern.tasks.evocraft.fitness.UpDownPistonCountFitness;

public class MinecraftMAPElitesPistonOrientationCountBinLabels extends MinecraftMAPElitesBinLabels {

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new NorthSouthPistonCountFitness(), new UpDownPistonCountFitness(), new EastWestPistonCountFitness());
	private int dim = Parameters.parameters.integerParameter("minecraftPistonLabelSize");
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			dim = Parameters.parameters.integerParameter("minecraftPistonLabelSize");
			int size = dim * dim * dim; // size is the total possible volume
			
			labels = new ArrayList<String>(size+1);
			
			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities (i < size would just give a range of 0-999)
			for(int xi = 0; xi <dim; xi++) {
				for(int yi = 0; yi < dim; yi++) {
					for(int zi = 0; zi < dim; zi++) {
						labels.add("NS"+xi+"UD"+yi+"EW"+zi);
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) { 
		int yDim = dim;
		int zDim = dim;
		//System.out.println(Arrays.toString(multi));

		multi[0] = Math.min(multi[0], dim-1);
		multi[1] = Math.min(multi[1], dim-1);
		multi[2] = Math.min(multi[2], dim-1);
		
		
		int binIndex = multi[0]*yDim*zDim + multi[1]*zDim + multi[2];

		assert binIndex < binLabels().size() : "Out of Bounds: " +Arrays.toString(multi) + " mapped to " + binIndex + " for dim = "+dim;
		
		return binIndex;
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {dim,dim,dim};
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int[] multi = super.multiDimensionalIndices(keys);
		// If the number of pistons with a given orientation exceeds the dim, then simply reduce it to the max interval
		multi[0] = Math.min(multi[0], dim-1);
		multi[1] = Math.min(multi[1], dim-1);
		multi[2] = Math.min(multi[2], dim-1);

		assert multi[0]*dim*dim + multi[1]*dim + multi[2] < binLabels().size() : "Out of Bounds: " +Arrays.toString(multi) + " mapped to " + (multi[0]*dim*dim + multi[1]*dim + multi[2]) + " for dim = "+dim;
		
		return multi;
	}
	
	
	@Override
	public List<MinecraftFitnessFunction> properties() { return properties; }

//	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
//		MMNEAT.main("runNumber:99 randomSeed:99 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-MEObserverVectorPistonOrientation saveTo:MEObserverVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5".split(" "));;
//	}
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		//MMNEAT.main("runNumber:99 randomSeed:99 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet trials:1 mu:100 maxGens:50000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:waterlavaminecraft log:WaterLavaMinecraft-NumLavaFitness saveTo:NumLavaFitness mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWaterVSLavaBinLabels minecraftTypeCountFitness:true minecraftDesiredBlockType:83".split(" "));
		//MMNEAT.main("runNumber:99 randomSeed:99 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.ThreeDimensionalVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:50000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-MECPPNWHD saveTo:MECPPNWHD mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true cleanOldNetworks:false includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:false includeSquareWaveFunction:false includeFullSawtoothFunction:false includeSigmoidFunction:false includeAbsValFunction:false includeSawtoothFunction:false".split(" "));
		//MMNEAT.main("runNumber:1 randomSeed:1 minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:false trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:false parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:waterlavaminecraft log:WaterLavaMinecraft-NumBiggerWaterVSLava saveTo:NumBiggerWaterVSLava mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWaterVSLavaBinLabels minecraftPistonLabelSize:5 spaceBetweenMinecraftShapes:15 minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.WaterAndLavaBlockSet minecraftWaterLavaSecondaryCreationFitness:false displayDiagonally:false minecraftTypeCountFitness:true minecraftDesiredBlockType:83 crossover:edu.southwestern.evolution.crossover.ArrayCrossover".split(" "));
	
	MMNEAT.main("runNumber:4 randomSeed:4 minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.IntegersToVolumeGenerator minecraftChangeCenterOfMassFitness:false trials:1 mu:100 maxGens:60000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:bigminecraft log:BigMinecraft-IntegerNumRedstone saveTo:IntegerNumRedstone mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels minecraftPistonLabelSize:5 spaceBetweenMinecraftShapes:15 minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.SimpleSolidBlockSet crossover:edu.southwestern.evolution.crossover.ArrayCrossover minecraftTypeCountFitness:true minecraftDesiredBlockType:8".split(" "));
	}
}
