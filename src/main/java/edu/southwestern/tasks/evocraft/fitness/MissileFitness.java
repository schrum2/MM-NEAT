package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

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
		int min = ranges.x() * ranges.z() * ranges.y();
		return -min;
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
		MinecraftClient.getMinecraftClient().fillCube(shapeCorner.add(targetCornerOffset), shapeCorner.add(targetCornerOffset).add(MinecraftUtilClass.getRanges().sub(1)), targetBlockType);
		System.out.println("targetCornerOffset" + targetCornerOffset);
		System.out.println("TARGET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	@Override
	public double calculateFinalScore(ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates shapeCorner,
			List<Block> originalBlocks) {

		List<Block> leftOverBlocksFromTarget = MinecraftClient.getMinecraftClient().readCube(shapeCorner.add(targetCornerOffset), shapeCorner.add(targetCornerOffset).add(MinecraftUtilClass.getRanges().sub(1)));
		List<Block> leftOverOfTargetBlocks = MinecraftUtilClass.getDesiredBlocks(leftOverBlocksFromTarget, acceptedBlownUpBlockTypes);
		return -leftOverOfTargetBlocks.size();
	}


}
