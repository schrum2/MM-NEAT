package edu.southwestern.tasks.evocraft.fitness;

import java.util.List;
import java.util.stream.Stream;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.stats.StatisticsUtilities;

public class NegativeSpaceCountFitness extends CheckBlocksInSpaceFitness {

	@Override
	public double fitnessFromBlocks(MinecraftCoordinates corner, List<Block> blocks) {
		// Makes arrays from the coordinates of the streams
		Stream<Block> blocklessX = blocks.parallelStream().filter(b -> b.type() != BlockType.AIR.ordinal());
		double[] blockArrX = blocklessX.mapToDouble(b -> b.x()).toArray();
		Stream<Block> blocklessY = blocks.parallelStream().filter(b -> b.type() != BlockType.AIR.ordinal());
		double[] blockArrY = blocklessY.mapToDouble(b -> b.y()).toArray();
		Stream<Block> blocklessZ = blocks.parallelStream().filter(b -> b.type() != BlockType.AIR.ordinal());
		double[] blockArrZ = blocklessZ.mapToDouble(b -> b.z()).toArray();
		
		// If only air, returns max value
		if(blockArrX.length==0) return Integer.MIN_VALUE;
			
		// Gets mins from the arrays
		int minX = (int) StatisticsUtilities.minimum(blockArrX);
		int minY = (int) StatisticsUtilities.minimum(blockArrY);
		int minZ = (int) StatisticsUtilities.minimum(blockArrZ);
		System.out.println("X:"+minX+"Y:"+minY+"Z:"+minZ);
		
		// Gets maxes from the arrays
		int maxX = (int) StatisticsUtilities.maximum(blockArrX);
		int maxY = (int) StatisticsUtilities.maximum(blockArrY);
		int maxZ = (int) StatisticsUtilities.maximum(blockArrZ);
		System.out.println("X:"+maxX+"Y:"+maxY+"Z:"+maxZ);
		
		int negativeBlocks = 0;
		for(Block b : blocks) {
			if(b.type() == BlockType.AIR.ordinal()) {
				if(b.x()>=minX && b.x()<=maxX) {
					if(b.y()>=minY && b.y()<=maxY) {
						if(b.z()>=minZ && b.z()<=maxZ) {
							negativeBlocks++;
						}
					}
				}
			}
		}
		System.out.println("NegativeBlocks:"+negativeBlocks);
		return negativeBlocks;
//		for(Block b : blocks) {
//			if(b.type() != BlockType.AIR.ordinal()) {
//				// If any new min or max, change to it. Air blocks cannot be included in these coordinates (There is definitely a better way to do this)
//				if(b.x()>maxX) maxX=(b.x()-corner.x())+1;
//				if(b.x()<minX) minX=(b.x()-corner.x());
//				if(b.y()>maxY) maxY=(b.y()-corner.y())+1;
//				if(b.y()<minY) minY=(b.y()-corner.y());
//				if(b.z()>maxZ) {
//					
//					maxZ=(b.z()-corner.z())+1;
//					System.out.println("MAX=========================="+b.z());
//					System.out.println("MAX=========================="+corner.z());
//				}
//				if(b.z()<minZ) {
//					
//					minZ=(b.z()-corner.z());
//					System.out.println("MIN=========================="+minZ);
//				}
//			
//				// Total keeps track of all of the blocks that are not air within the shape
//				total++;
//			}
//			
//			
//		}
//		// Computes size of the shape based on the coordinates, then subtracts the non-air blocks to get negative space
//		System.out.println("X:"+(maxX-minX)+" Y:"+(maxY-minY)+" Z:"+(maxZ-minZ));
//		int sizeOfShape = (maxX-minX)*(maxY-minY)*(maxZ-minZ);
//		int negativeBlocks = sizeOfShape-total;
//		System.out.println("Size of shape = "+sizeOfShape+" Total = "+total);
//		System.out.println("Negative Blocks:"+negativeBlocks);
//		if(negativeBlocks<0) System.exit(1);
		//return 0;
	}

	@Override
	public double maxFitness() {
		// The max fitness here is the largest possible cube -2 because to get that designated negative space, there must be at least 2 cubes
		return Parameters.parameters.integerParameter("minecraftXRange") * Parameters.parameters.integerParameter("minecraftYRange") * Parameters.parameters.integerParameter("minecraftZRange")-2;
	}
	
}
