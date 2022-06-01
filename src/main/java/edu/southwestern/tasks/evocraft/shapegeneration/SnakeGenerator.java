package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
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
		
		while(!done) {
			numberOfIterations++;
			//System.out.println(numberOfIterations);
			MinecraftCoordinates direction = ShapeGenerator.generateBlock(corner, blockSet, snake, net, ranges, distanceInEachPlane, xi, yi, zi);
			
			if(direction == null || numberOfIterations == 100) { // the 100 should be a command line parameter (maxSnakeLength)
				done = true;
			} else {
				xi += direction.x();
				yi += direction.y();
				zi += direction.z();
			}
			
			// Never allowed to generate beneath absolute y=0. Out of bounds.
			if(yi < 0) done = true;
		}
		assert numberOfIterations < 101;
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
