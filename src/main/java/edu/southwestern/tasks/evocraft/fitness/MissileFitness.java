package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import com.clearspring.analytics.util.Pair;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;


/**
 * TODO
 * 
 * @author raffertyt
 *
 */
public class MissileFitness extends TimedEvaluationMinecraftFitnessFunction {

	private MinecraftCoordinates targetCornerOffset;
	private BlockType targetBlockType;
	private BlockType[] acceptedBlownUpBlocks;
	//constructor to create the accepted block type list

	public MissileFitness() {
		int xOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeY");
		int yOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeY");
		int zOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeY");
		targetCornerOffset = new MinecraftCoordinates(xOffset, yOffset, zOffset);

		targetBlockType = BlockType.values()[Parameters.parameters.integerParameter("minecraftMissleTargetBlockType")];
		acceptedBlownUpBlocks = new BlockType[] {targetBlockType};
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
	public double fitnessScore(MinecraftCoordinates corner, List<Block> originalBlocks) {

		//		// TODO: Create target structure here
		//		List<Block> targetBlockList = new ArrayList<>();
		//		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		//		for(int i = 0; i < ranges.x(); i++) {
		//			for(int j = 0; j < ranges.x(); j++) {
		//				for(int k = 0; k < ranges.x(); k++) {
		//					MinecraftCoordinates loopCoordinates = new MinecraftCoordinates(i, j, k); 
		//					MinecraftCoordinates nextBlockCoordinates = new MinecraftCoordinates(targetCornerOffset.add(loopCoordinates));
		//					targetBlockList.add(new Block(nextBlockCoordinates.x(), nextBlockCoordinates.y(), nextBlockCoordinates.z(), BlockType.SLIME, Orientation.WEST));
		//				}
		//			}
		//		}
		//		MinecraftClient.getMinecraftClient().spawnBlocks(targetBlockList);


		// Create structure to be blown up
		MinecraftClient.getMinecraftClient().fillCube(corner.add(targetCornerOffset), corner.add(targetCornerOffset).add(MinecraftUtilClass.getRanges()), targetBlockType);
		// Evaluate as normal
		return super.fitnessScore(corner, originalBlocks);
	}


	@Override
	public double calculateFinalScore(ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates corner,
			List<Block> originalBlocks) {

		List<Block> leftOverBlocksFromTarget = MinecraftClient.getMinecraftClient().readCube(corner.add(targetCornerOffset), corner.add(targetCornerOffset).add(MinecraftUtilClass.getRanges()));
		List<Block> leftOverOfTargetBlocks = MinecraftUtilClass.getDesiredBlocks(leftOverBlocksFromTarget, acceptedBlownUpBlocks);
		return -leftOverOfTargetBlocks.size();
	}


}
