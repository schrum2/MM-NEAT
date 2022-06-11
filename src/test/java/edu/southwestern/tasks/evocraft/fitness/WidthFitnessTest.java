package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

public class WidthFitnessTest {

	private WidthFitness ff;

	@Before
	public void before() {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:15"});
		ff = new WidthFitness();
	}
	
	@Test
	public void testMaxFitness() {
		assertEquals(15, ff.maxFitness(), 0);
	}

	@Test
	public void testFitnessFromBlocks() {
		// Should not matter
		MinecraftCoordinates loc = new MinecraftCoordinates(11102,5,-32434);
		List<Block> blocks = new ArrayList<>();
		assertEquals(0, ff.fitnessFromBlocks(loc, blocks), 0);
		blocks.add(new Block(11102,5,-32434,BlockType.REDSTONE_BLOCK,null));
		assertEquals(1, ff.fitnessFromBlocks(loc, blocks), 0);
		blocks.add(new Block(11102,6,-32434,BlockType.REDSTONE_BLOCK,null));
		assertEquals(1, ff.fitnessFromBlocks(loc, blocks), 0);
		blocks.add(new Block(11102,6,-32433,BlockType.REDSTONE_BLOCK,null));
		assertEquals(1, ff.fitnessFromBlocks(loc, blocks), 0);
		blocks.add(new Block(11103,6,-32433,BlockType.REDSTONE_BLOCK,null));
		assertEquals(2, ff.fitnessFromBlocks(loc, blocks), 0);
		blocks.add(new Block(11105,6,-32433,BlockType.REDSTONE_BLOCK,null));
		assertEquals(4, ff.fitnessFromBlocks(loc, blocks), 0);		
	}

	@Test
	public void testMinFitness() {
		assertEquals(0, ff.minFitness(), 0);
	}

}
