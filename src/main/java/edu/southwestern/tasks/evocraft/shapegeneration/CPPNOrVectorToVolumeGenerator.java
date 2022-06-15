package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;

/**
 * This shape generator uses either CPPN or VectorToVolume generation
 * depending on the type/form of the genotype.
 * 
 * @author Alejandro Medina
 *
 */
public class CPPNOrVectorToVolumeGenerator implements ShapeGenerator<ArrayList<Double>> {

	@Override
	public List<Block> generateShape(Genotype<ArrayList<Double>> genome, MinecraftCoordinates corner,
			BlockSet blockSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNetworkOutputLabels() {
		return ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet);
	}

}
