package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.graphics.ThreeDimensionalUtil;

public class SnakeGeneration<T extends Network> implements ShapeGenerator<T> {

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
			
			double[] inputs = ThreeDimensionalUtil.get3DObjectCPPNInputs(xi, yi, zi, ranges.x(), ranges.y(), ranges.z(), -1, distanceInEachPlane);
			double[] outputs = net.process(inputs);
			
			//List<Integer> direction = nextDirection();
			if(numberOfIterations == 100) { // the 100 should be a command line parameter (maxSnakeLength)
				done = true;
			} else {
				
			}
		}
		return snake;
	}

}
