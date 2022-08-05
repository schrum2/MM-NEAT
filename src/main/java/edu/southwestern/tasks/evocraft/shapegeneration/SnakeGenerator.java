package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * 
 * @author richey2
 *
 * @param <T>
 */
public class SnakeGenerator<T extends Network> implements ShapeGenerator<T> {

	@Override
	public List<Block> generateShape(Genotype<T> genome, MinecraftCoordinates corner, BlockSet blockSet) {
		
		// Create CPPN out of genome
		Network net = genome.getPhenotype();
		
		// List of blocks that make up snake
		List<Block> snake = new ArrayList<>();
		
		// Stores all the coordinates that are already occupied with a block
		HashSet<MinecraftCoordinates> occupied = new HashSet<>();
		
		// Ranges for the x, y, and z direction
		MinecraftCoordinates ranges = new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
		
		boolean done = false;
		int numberOfIterations = 0;
		
		boolean distanceInEachPlane = Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane");
		
		int xi = ranges.x()/2;
		int yi = ranges.y()/2;
		int zi = ranges.z()/2;
		
		MinecraftCoordinates coordinatesToAdd = new MinecraftCoordinates(xi,yi,zi);
		
		while(!done) {
			//System.out.println("num itr:"+ numberOfIterations);
			occupied.add(coordinatesToAdd);
			//System.out.println(occupied);
			numberOfIterations++;
			//System.out.println(numberOfIterations);
			List<BlockType> bs = Arrays.asList(blockSet.getPossibleBlocks());
			MinecraftCoordinates direction = ShapeGenerator.generateBlock(corner, bs, snake, net, ranges, distanceInEachPlane, xi, yi, zi);
			
			if(direction == null || numberOfIterations >= Parameters.parameters.integerParameter("minecraftMaxSnakeLength")) {
				//System.out.println("Direction: " + direction + "Number of its: " + numberOfIterations);
				//System.out.println("Something is either null or too many");
				done = true;
			} else {
				xi += direction.x();
				yi += direction.y();
				zi += direction.z();

				assert !(Parameters.parameters.booleanParameter("minecraftRedirectConfinedSnakes") || Parameters.parameters.booleanParameter("minecraftStopConfinedSnakes")) ||
				snake.size() <= ranges.x() * ranges.y() * ranges.z() : ranges + ":\n" + snake + ":\n" + occupied;

				// Never allowed to generate beneath absolute y=0. Out of bounds.
				if(yi < 0) {
					//System.out.println("Goes below the y");
					done = true;
				} else {
					// Could be in there after saving
					coordinatesToAdd = new MinecraftCoordinates(xi,yi,zi);
					//System.out.println(numberOfIterations+ ": Does "+occupied+" contain "+coordinatesToAdd);
					if(occupied.contains(coordinatesToAdd)) {
						//System.out.println("\tYES");
						done = true;
					}
				}
			}
		}
		assert numberOfIterations <= Parameters.parameters.integerParameter("minecraftMaxSnakeLength");
		//System.out.println("return snake: " + snake);
		return snake;
	}

	@Override
	public String[] getNetworkOutputLabels() {
		String[] firstPart = ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet);
		String[] secondPart = {"-X","-Y","-Z","+X","+Y","+Z","continue"};
		String[] result = ArrayUtil.combineArrays(firstPart,secondPart);
		return result;
	}

}
