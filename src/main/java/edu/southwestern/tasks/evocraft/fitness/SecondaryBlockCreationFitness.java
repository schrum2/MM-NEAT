package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import org.nd4j.nativeblas.Nd4jCpu.check_numerics;

import com.clearspring.analytics.util.Pair;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;

public abstract class SecondaryBlockCreationFitness extends TimedEvaluationMinecraftFitnessFunction{
	//currently making for water and lava
	
	//take an array to specify blocks
	//take a blockset we create with
	
	//check for secondary blocks in a timed evaluation minecraft fitness function
		//-figure out time wait for this, maybe just a parameter setting? minecraftMandatoryWaitTime
	
	//reward for secondary block creation at end of evaluation time
		// count blocks of desired type
	
	
	//private blocktypes array - set to parameter list of block types
	//TODO: undo hardcoding of block types
	private BlockType[] acceptedBlockTypes = {BlockType.COBBLESTONE, BlockType.OBSIDIAN};

	public BlockType[] getAcceptedBlockTypes() {
		return acceptedBlockTypes;
	}

	public void setAcceptedBlockTypes(BlockType[] acceptedBlockTypes) {
		this.acceptedBlockTypes = acceptedBlockTypes;
	}
	
	@Override
	public double calculateFinalScore (ArrayList<Pair<Long,List<Block>>> history, MinecraftCoordinates corner, List<Block> originalBlocks) {
		//check the original block list with the history, last read
		//create variable to last entry in history using history.size
		List<Block> finalBlocksList = history.get(history.size()-1).right;
		//check that list against originalBlocks to get a list of only the desired blocks
		finalBlocksList = MinecraftUtilClass.getDesiredBlocks(finalBlocksList, acceptedBlockTypes);
		
		//add all the blocks.... which should just be the length of the list
		return finalBlocksList.size();
		
	}
	//using the history list check if there are new blocks at each read
	
	//use get desired blocks

}
