package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.CPPNOrBlockVectorGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
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
@SuppressWarnings("rawtypes")
public class CPPNOrVectorToVolumeGenerator implements ShapeGenerator {

	private VectorToVolumeGenerator forVectors;
	private ThreeDimensionalVolumeGenerator forCPPN;
	
	public CPPNOrVectorToVolumeGenerator() {
		forVectors = new VectorToVolumeGenerator();
		forCPPN = new ThreeDimensionalVolumeGenerator();
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Block> generateShape(Genotype genome, MinecraftCoordinates corner, BlockSet blockSet) {
		
		List<Block> blocks;
		CPPNOrBlockVectorGenotype either = (CPPNOrBlockVectorGenotype) genome;
		
		if(either.getFirstForm()) { // first form is CPPN, use ThreeDimensionalVolumeGenerator
			Network first = (Network) either.getCurrentGenotype();
			blocks = forCPPN.generateShape((Genotype) first, corner, blockSet);
		} else { // second form is Vector, use VectorToVolumeGenerator
			Genotype<ArrayList<Double>> second = either.getCurrentGenotype();
			blocks = forVectors.generateShape(second, corner, blockSet);
		}
		return blocks;
	}

	@Override
	public String[] getNetworkOutputLabels() {
		return ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet);
	}

}
