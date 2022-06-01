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

public class ThreeDimensionalVolumeGenerator<T extends Network> implements ShapeGenerator<T> {

	@Override
	public List<Block> generateShape(Genotype<T> genome, MinecraftCoordinates corner, BlockSet blockSet) {
		List<Block> blocks = new ArrayList<>();
		Network net = genome.getPhenotype();
		MinecraftCoordinates ranges = new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
		boolean distanceInEachPlane = Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane");
		for(int xi = 0; xi < ranges.x(); xi++) {
			for(int yi = 0; yi < ranges.y(); yi++) {
				for(int zi = 0; zi < ranges.z(); zi++) {
					ShapeGenerator.generateBlock(corner, blockSet, blocks, net, ranges, distanceInEachPlane, xi, yi, zi);
				}
			}
		}
		return blocks;
	}

	@Override
	public String[] getNetworkOutputLabels() {
		return ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet);
	}
}
