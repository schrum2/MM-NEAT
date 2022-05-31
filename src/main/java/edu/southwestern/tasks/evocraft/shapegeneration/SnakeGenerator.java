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
			
			MinecraftCoordinates direction = ShapeGenerator.generateBlock(corner, blockSet, snake, net, ranges, distanceInEachPlane, xi, yi, zi);
			
			if(direction == null || numberOfIterations == 100) { // the 100 should be a command line parameter (maxSnakeLength)
				done = true;
			} else {
				xi += direction.x();
				yi += direction.y();
				zi += direction.z();
			}
		}
		return snake;
	}

	@Override
	public String[] getNetworkOutputLabels() {
		String[] firstPart = ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet);
		String[] secondPart = {"-X","-Y","-Z","+X","+Y","+Z","continue"};
		
		int finalLength = firstPart.length + secondPart.length;
		
		String[] result = new String[finalLength];
		
		for(int i = 0; i < finalLength; i++) {
			if(i < firstPart.length) {
				result[i] = firstPart[i];
			} else {
				assert(i >= finalLength);
				result[i] = secondPart[i-firstPart.length];
			}
		}
		return result;
	}

}
