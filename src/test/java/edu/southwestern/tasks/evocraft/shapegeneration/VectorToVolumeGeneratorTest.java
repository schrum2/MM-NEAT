package edu.southwestern.tasks.evocraft.shapegeneration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask;
import edu.southwestern.tasks.evocraft.blocks.MachineBlockSet;

public class VectorToVolumeGeneratorTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testGenerateShape() {
		
		Parameters.initializeParameterCollections("minecraftXRange:2 minecraftYRange:2 minecraftZRange:2 minecraftFakeTestFitness:true minecraftEvolveOrientation:true minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false spaceBetweenMinecraftShapes:7 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true".split(" "));
		MMNEAT.task = new MinecraftLonerShapeTask();
		
		int perBlock = 3;
		int numBlocks = 2 * 2 * 2;
		VectorToVolumeGenerator vtv = new VectorToVolumeGenerator();
		
		int vars = numBlocks*perBlock;
		ArrayList<Double> list = new ArrayList<>(vars);
		for(int i = 0; i < vars; i++) {
			list.add(0.75); // All present, all same orientation, all same block
		}
		BoundedRealValuedGenotype genotype = new BoundedRealValuedGenotype(list);
		MachineBlockSet blockSet = new MachineBlockSet();
		
		List<Block> blocks = vtv.generateShape(genotype, new MinecraftCoordinates(0,0,0), blockSet);
		
		assertEquals(numBlocks, blocks.size());
		for(Block b : blocks) {
			assertEquals(BlockType.STICKY_PISTON, b.type());
			assertEquals(Orientation.UP, b.orientation());
		}
		
		// Switch some blocks to being absent
		list.set(0, 0.0);
		list.set(3, 0.0);
		list.set(9, 0.0);
		
		// Change orientation of some blocks
		list.set(8, 0.0);
		list.set(14, 0.25);
		list.set(17, 1.0);

		// Change type of some blocks
		list.set(7, 0.0);
		list.set(13, 0.25);
		list.set(16, 1.0);
		
		BoundedRealValuedGenotype genotype2 = new BoundedRealValuedGenotype(list);
		
		List<Block> blocks2 = vtv.generateShape(genotype2, new MinecraftCoordinates(0,0,0), blockSet);

		assertEquals(numBlocks - 3, blocks2.size());
		Block first = blocks2.get(0);
		assertEquals(Orientation.NORTH, first.orientation());		
		assertEquals(BlockType.QUARTZ_BLOCK, first.type());

		Block second = blocks2.get(1);
		assertEquals(Orientation.WEST, second.orientation());		
		assertEquals(BlockType.SLIME, second.type());
		
		Block third = blocks2.get(1);
		assertEquals(Orientation.DOWN, third.orientation());		
		assertEquals(BlockType.OBSERVER, third.type());
		
	}

}
