package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MineCraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.graphics.ThreeDimensionalUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

public class ThreeDimensionalVolumeGenerator<T extends Network> implements ShapeGenerator<T> {

	@Override
	public List<Block> generateShape(Genotype<T> genome, MineCraftCoordinates corner, BlockSet blockSet) {
		List<Block> blocks = new ArrayList<>();
		Network net = genome.getPhenotype();
		MineCraftCoordinates ranges = new MineCraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
		boolean distanceInEachPlane = Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane");
		int numBlockTypes = blockSet.getPossibleBlocks().length;
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					double[] inputs = ThreeDimensionalUtil.get3DObjectCPPNInputs(xi, yi, zi, ranges.x(), ranges.y(), ranges.z(), -1, distanceInEachPlane);
					double[] outputs = net.process(inputs);
					
					if(outputs[OUTPUT_INDEX_PRESENCE] > VOXEL_EXPRESSION_THRESHOLD) {
						ArrayList<Double> blockPreferences = new ArrayList<Double>(numBlockTypes);
						for(int i = 1; i <= numBlockTypes; i++) {
							blockPreferences.add(outputs[i]);
						}
						int typeIndex = StatisticsUtilities.argmax(blockPreferences);
						// TODO: Add way to evolve orientation
						Orientation blockOrientation = Orientation.NORTH;
						Block b = new Block(corner.add(new MineCraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[typeIndex], blockOrientation);
						blocks.add(b);
					}
				}
			}
		}
		return blocks;
	}

}
