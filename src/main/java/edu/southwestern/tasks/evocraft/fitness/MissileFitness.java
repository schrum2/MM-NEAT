package edu.southwestern.tasks.evocraft.fitness;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Pair;

/**
 * Creates a target based on integer parameters. This target eventually 
 * gets examined for missing blocks that indicates they were blown up.
 * 
 * @author raffertyt
 *
 */
public class MissileFitness extends TimedEvaluationMinecraftFitnessFunction {
	
	private MinecraftCoordinates targetCornerOffset;
	private BlockType targetBlockType;
	private BlockType[] acceptedBlownUpBlockTypes;
	private MinecraftCoordinates rotatedCompassTarget1;
	private MinecraftCoordinates rotatedCompassTarget2;

	//constructor to create the accepted block type list
	public MissileFitness() {
		int xOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeX");
		int yOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeY");
		int zOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeZ");
		targetCornerOffset = new MinecraftCoordinates(xOffset, yOffset, zOffset);

		targetBlockType = BlockType.values()[Parameters.parameters.integerParameter("minecraftMissleTargetBlockType")];
		acceptedBlownUpBlockTypes = new BlockType[] {targetBlockType};
	}
	
	@Override
	public double minFitness() {
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		int numBlocksInTarget = ranges.x() * ranges.z() * ranges.y();
		if(Parameters.parameters.booleanParameter("minecraftCompassMissileTargets")) {
			numBlocksInTarget *= 4; // because there are 4 targets
		}
		return -numBlocksInTarget; // negative since left over blocks are bad
	}

	//shape dimensions
	@Override
	public double maxFitness() {
		return 0;
	}

	@Override
	public Double earlyEvaluationTerminationResult(MinecraftCoordinates corner, List<Block> originalBlocks,
			ArrayList<Pair<Long, List<Block>>> history, List<Block> newShapeBlockList) {
		
		// If there have been two consecutive empty readings, then assume the shape has been completely obliterated,
		// and compute the final score in the normal fashion early.
		if(newShapeBlockList.isEmpty() && history.get(history.size() - 2).t2.isEmpty()) {
			return calculateFinalScore(history, corner, originalBlocks);
		} else {
			return null;
		}
	}
	
	@Override
	public void preSpawnSetup(MinecraftCoordinates shapeCorner) {
		// Create structure to be blown up
		//changing the last add to a sub might fix the slight target offset from it intended position
//		MinecraftClient.getMinecraftClient();
//		MinecraftClient.clearAndVerify(shapeCorner.add(targetCornerOffset));
		MinecraftClient.getMinecraftClient().fillCube(shapeCorner.add(targetCornerOffset), shapeCorner.add(targetCornerOffset).add(MinecraftUtilClass.getRanges().sub(1)), targetBlockType);
	
		//
		if(Parameters.parameters.booleanParameter("minecraftCompassMissileTargets")) {
			//Target opposite the original
			MinecraftClient.getMinecraftClient().fillCube(shapeCorner.sub(targetCornerOffset), shapeCorner.sub(targetCornerOffset).add(MinecraftUtilClass.getRanges().sub(1)), targetBlockType);
			rotatedCompassTarget1 = new MinecraftCoordinates(targetCornerOffset.z(), targetCornerOffset.y(), -targetCornerOffset.x()); 
			//Target to the side of the shape
			MinecraftClient.getMinecraftClient().fillCube(shapeCorner.add(rotatedCompassTarget1), shapeCorner.add(rotatedCompassTarget1.add(MinecraftUtilClass.getRanges().sub(1))), targetBlockType);
			rotatedCompassTarget2 = new MinecraftCoordinates(-targetCornerOffset.z(), targetCornerOffset.y(), targetCornerOffset.x());
			//Target to the other side of the shape
			MinecraftClient.getMinecraftClient().fillCube(shapeCorner.add(rotatedCompassTarget2), shapeCorner.add(rotatedCompassTarget2.add(MinecraftUtilClass.getRanges().sub(1))), targetBlockType);
		
	
		}		
	}

	@Override
	public double calculateFinalScore(ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates shapeCorner,
			List<Block> originalBlocks) {
		List<Block> leftOverBlocksFromTarget = MinecraftClient.getMinecraftClient().readCube(shapeCorner.add(targetCornerOffset), shapeCorner.add(targetCornerOffset).add(MinecraftUtilClass.getRanges().sub(1)));
		
		if(Parameters.parameters.booleanParameter("minecraftCompassMissileTargets")) {
			leftOverBlocksFromTarget.addAll(MinecraftClient.getMinecraftClient().readCube(shapeCorner.sub(targetCornerOffset), shapeCorner.sub(targetCornerOffset).add(MinecraftUtilClass.getRanges().sub(1))));
			//Target to the side of the shape
			leftOverBlocksFromTarget.addAll(MinecraftClient.getMinecraftClient().readCube(shapeCorner.add(rotatedCompassTarget1), shapeCorner.add(rotatedCompassTarget1.add(MinecraftUtilClass.getRanges().sub(1)))));
			//Target to the other side of the shape
			leftOverBlocksFromTarget.addAll(MinecraftClient.getMinecraftClient().readCube(shapeCorner.add(rotatedCompassTarget2), shapeCorner.add(rotatedCompassTarget2.add(MinecraftUtilClass.getRanges().sub(1)))));
		
		
		}
		
		List<Block> leftOverOfTargetBlocks = MinecraftUtilClass.getDesiredBlocks(leftOverBlocksFromTarget, acceptedBlownUpBlockTypes);
				
		return -leftOverOfTargetBlocks.size();
	}
	@Override
	public boolean shapeIsWorthSaving(double fitnessScore, ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates shapeCorner, List<Block> originalBlocks) {

		double minFitness = minFitness();
		// minecraftPercentOfTarget is between 0.0 and 1.0, so portionMinFitness is a slightly higher value
		double portionMinFitness = minFitness * Parameters.parameters.doubleParameter("minecraftPercentOfTarget");
		
		return (fitnessScore > portionMinFitness);
	}
	public static void main(String[] args) {

		try {
			//MMNEAT.main("runNumber:665 randomSeed:665 useWoxSerialization:false minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:20 maxGens:3005 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:10 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-ESObserverVectorTEST saveTo:ESObserverVectorTEST minecraftContainsWholeMAPElitesArchive:false rememberParentScores:true".split(" ")); 
			MMNEAT.main("runNumber:108 randomSeed:100 minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftMissileFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:false mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:20 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:1 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:missileminecraft log:MissileMinecraft-DirectedBiggerVectorPistonOrientation saveTo:DirectedBiggerVectorPistonOrientation mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 startY:40 extraSpaceBetweenMinecraftShapes:100 minecraftTargetDistancefromShapeY:0 minecraftTargetDistancefromShapeX:50 minecraftTargetDistancefromShapeZ:0 minecraftCompassMissileTargets:true".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
