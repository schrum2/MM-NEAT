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
import edu.southwestern.util.stats.Statistic;

public class VectorToVolumeGeneratorTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testGenerateShape() {
		
		Parameters.initializeParameterCollections("minecraftXRange:2 minecraftYRange:2 minecraftZRange:2 minecraftFakeTestFitness:true minecraftEvolveOrientation:true minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false spaceBetweenMinecraftShapes:7 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true".split(" "));
		MMNEAT.fitnessFunctions = new ArrayList<ArrayList<String>>();
		MMNEAT.fitnessFunctions.add(new ArrayList<String>());
		MMNEAT.aggregationOverrides = new ArrayList<Statistic>();
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
			assertEquals(BlockType.STICKY_PISTON.ordinal(), b.type());
			assertEquals(Orientation.UP.ordinal(), b.orientation());
		}
		
		// Switch some blocks to being absent
		list.set(0, 0.0);
		list.set(3, 0.0);
		list.set(9, 0.0);
		
		// Change orientation of some blocks
		list.set(8, 0.0); // first
		list.set(14, 0.25); // second
		list.set(17, 1.0); // third

		// Change type of some blocks
		list.set(7, 0.0); // first
		list.set(13, 0.25); // second
		list.set(16, 1.0); // third
		
		BoundedRealValuedGenotype genotype2 = new BoundedRealValuedGenotype(list);
		
		System.out.println(blocks);
		List<Block> blocks2 = vtv.generateShape(genotype2, new MinecraftCoordinates(0,0,0), blockSet);
		System.out.println(blocks2);

		assertEquals(numBlocks - 3, blocks2.size());
		Block first = blocks2.get(0);
		assertEquals(Orientation.NORTH.ordinal(), first.orientation());		
		assertEquals(BlockType.QUARTZ_BLOCK.ordinal(), first.type());

		Block second = blocks2.get(1);
		assertEquals(Orientation.WEST.ordinal(), second.orientation());		
		assertEquals(BlockType.SLIME.ordinal(), second.type());
		
		Block third = blocks2.get(2);
		assertEquals(Orientation.DOWN.ordinal(), third.orientation());		
		assertEquals(BlockType.OBSERVER.ordinal(), third.type());
		
	}

}
