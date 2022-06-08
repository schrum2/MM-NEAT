package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
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
		
		MinecraftCoordinates occupiedCoordinate = new MinecraftCoordinates(xi,yi,zi);
		
		while(!done) {
			occupied.add(occupiedCoordinate);
			numberOfIterations++;
			//System.out.println(numberOfIterations);
			MinecraftCoordinates direction = ShapeGenerator.generateBlock(corner, blockSet, snake, net, ranges, distanceInEachPlane, xi, yi, zi);
			
			if(direction == null || numberOfIterations >= Parameters.parameters.integerParameter("minecraftMaxSnakeLength")) {
				done = true;
			} else {
				xi += direction.x();
				yi += direction.y();
				zi += direction.z();
			}
			
			assert !(Parameters.parameters.booleanParameter("minecraftRedirectConfinedSnakes") || Parameters.parameters.booleanParameter("minecraftStopConfinedSnakes")) ||
					snake.size() <= ranges.x() * ranges.y() * ranges.z() : ranges + ":" + snake;
			
			// Never allowed to generate beneath absolute y=0. Out of bounds.
			if(yi < 0) done = true;
			
			// Could be in there after saving
			MinecraftCoordinates possiblyOccupied = new MinecraftCoordinates(xi,yi,zi);
			if(occupied.contains(possiblyOccupied)) done = true;
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
